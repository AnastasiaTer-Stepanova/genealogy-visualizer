package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static genealogy.visualizer.service.helper.FilterHelper.addArchiveDocumentIdFilter;
import static genealogy.visualizer.service.helper.FilterHelper.addFullNameFilter;

public class FamilyRevisionDAOImpl implements FamilyRevisionDAO {

    private final FamilyRevisionRepository familyRevisionRepository;
    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final EntityManager entityManager;

    public FamilyRevisionDAOImpl(FamilyRevisionRepository familyRevisionRepository,
                                 ArchiveDocumentDAO archiveDocumentDAO,
                                 EntityManager entityManager) {
        this.familyRevisionRepository = familyRevisionRepository;
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveBatch(List<FamilyRevision> familyRevisions) {
        familyRevisionRepository.saveAll(familyRevisions);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        FamilyRevision familyRevision = familyRevisionRepository.findById(id).orElse(null);
        if (familyRevision == null) return;
        familyRevision.getPartner().setPartner(null);
        familyRevisionRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public FamilyRevision save(FamilyRevision familyRevision) {
        ArchiveDocument archiveDocument = familyRevision.getArchiveDocument();
        if (archiveDocument != null && archiveDocument.getId() == null) {
            archiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(archiveDocument);
            familyRevision.setArchiveDocument(archiveDocument);
        }
        if (familyRevision.getPartner() != null && familyRevision.getPartner().getFullName() != null &&
                familyRevision.getPartner().getFullName().getName() != null) {
            FamilyRevision partner = familyRevision.getPartner();
            ArchiveDocument partnerArchiveDocument = partner.getArchiveDocument();
            if (partnerArchiveDocument != null && partnerArchiveDocument.getId() == null) {
                partnerArchiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(partnerArchiveDocument);
                partner.setArchiveDocument(partnerArchiveDocument);
            }
            partner.setPartner(familyRevision);
            List<FamilyRevision> result = familyRevisionRepository.saveAll(List.of(familyRevision, partner));
            return result.getFirst();
        } else {
            familyRevision.setPartner(null);
            return familyRevisionRepository.save(familyRevision);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public FamilyRevision update(FamilyRevision familyRevision) {
        if (familyRevision.getId() == null)
            throw new IllegalArgumentException("Cannot update familyRevision without id");
        FamilyRevision partner = null;
        if (familyRevision.getPartner() != null && familyRevision.getPartner().getFullName() != null
                && familyRevision.getPartner().getFullName().getName() != null && familyRevision.getPartner().getId() == null) {
            partner = familyRevision.getPartner();
            partner = save(partner);
        }
        FamilyRevision updatedFamilyRevision = familyRevisionRepository.update(familyRevision);
        if (updatedFamilyRevision == null) {
            return null;
        }
        if (partner != null) {
            partner.setPartner(updatedFamilyRevision);
        }
        updatedFamilyRevision.setAnotherNames(familyRevision.getAnotherNames());
        updatedFamilyRevision.setArchiveDocument(familyRevision.getArchiveDocument());
        return updatedFamilyRevision;
    }

    @Override
    public FamilyRevision findFullInfoById(Long id) {
        return familyRevisionRepository.findFullInfoById(id).orElse(null);
    }

    @Override
    public List<FamilyRevision> filter(FamilyRevisionFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FamilyRevision> cq = cb.createQuery(FamilyRevision.class);
        Root<FamilyRevision> root = cq.from(FamilyRevision.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(addArchiveDocumentIdFilter(cb, root, filter.getArchiveDocumentId()));
        predicates.addAll(addFullNameFilter(cb, root, filter.getFullName(), "fullName"));
        if (filter.getFamilyRevisionNumber() != null) {
            predicates.add(cb.equal(root.get("familyRevisionNumber"), filter.getFamilyRevisionNumber()));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<FamilyRevision> findFamilyRevisionsByNumberFamilyAndArchiveDocumentId(Long archiveDocumentId,
                                                                                      short familyNumber,
                                                                                      boolean isFindWithHavePerson) {
        return isFindWithHavePerson ?
                familyRevisionRepository.findFamilyRevisionsByNumberFamilyAndArchiveDocumentId(archiveDocumentId, familyNumber)
                        .orElse(Collections.emptyList()) :
                familyRevisionRepository.findFamilyRevisionsByNumberFamilyAndArchiveDocumentIdWithoutPerson(archiveDocumentId, familyNumber)
                        .orElse(Collections.emptyList());
    }

    @Override
    public List<FamilyRevision> findFamilyRevisionsByNextFamilyRevisionNumberAndArchiveDocumentId(Long archiveDocumentId,
                                                                                                  short familyNumber,
                                                                                                  boolean isFindWithHavePerson) {
        return isFindWithHavePerson ?
                familyRevisionRepository.findFamilyRevisionsByNextFamilyRevisionNumberAndArchiveDocumentId(archiveDocumentId, familyNumber)
                        .orElse(Collections.emptyList()) :
                familyRevisionRepository.findFamilyRevisionsByNextFamilyRevisionNumberAndArchiveDocumentIdWithoutPerson(archiveDocumentId, familyNumber)
                        .orElse(Collections.emptyList());
    }

    @Override
    public void updatePersonIdByPersonId(Long personId, Long newPersonId) {
        familyRevisionRepository.updatePersonIdByPersonId(personId, newPersonId);
    }

    @Override
    public void updatePersonIdById(Long id, Long newPersonId) {
        familyRevisionRepository.updatePersonIdById(id, newPersonId);
    }
}
