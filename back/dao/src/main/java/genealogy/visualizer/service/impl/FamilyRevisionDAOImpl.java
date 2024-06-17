package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.helper.RepositoryEasyModelHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static genealogy.visualizer.service.helper.FilterHelper.addArchiveDocumentIdFilter;
import static genealogy.visualizer.service.helper.FilterHelper.addFullNameFilter;
import static genealogy.visualizer.service.helper.FilterHelper.getGraphsResult;

public class FamilyRevisionDAOImpl implements FamilyRevisionDAO {

    private final FamilyRevisionRepository familyRevisionRepository;
    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final PersonRepository personRepository;
    private final EntityManager entityManager;

    public FamilyRevisionDAOImpl(FamilyRevisionRepository familyRevisionRepository,
                                 ArchiveDocumentRepository archiveDocumentRepository,
                                 PersonRepository personRepository,
                                 EntityManager entityManager) {
        this.familyRevisionRepository = familyRevisionRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.personRepository = personRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<FamilyRevision> familyRevisionHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveBatch(List<FamilyRevision> familyRevisions) {
        familyRevisionRepository.saveAll(familyRevisions);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        familyRevisionRepository.updatePartnerId(id, null);
        familyRevisionRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public FamilyRevision save(FamilyRevision familyRevision) {
        if (familyRevision.getId() != null)
            throw new IllegalArgumentException("Cannot save familyRevision with id");
        familyRevision = updateLinks(familyRevision);

        FamilyRevision familyRevisionForSave = familyRevision.clone();
        familyRevisionForSave.setPartner(null);

        FamilyRevision savedFamilyRevision = familyRevisionRepository.save(familyRevisionForSave);
        updateLinks(savedFamilyRevision, familyRevision);
        entityManager.clear();
        return this.findFullInfoById(savedFamilyRevision.getId());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public FamilyRevision update(FamilyRevision familyRevision) {
        if (familyRevision.getId() == null)
            throw new IllegalArgumentException("Cannot update familyRevision without id");
        familyRevision = updateLinks(familyRevision);
        FamilyRevision updatedFamilyRevision = familyRevisionRepository.update(familyRevision);
        if (updatedFamilyRevision == null)
            throw new EmptyResultDataAccessException("Updating family revision failed", 1);
        familyRevisionRepository.deleteAnotherNamesById(updatedFamilyRevision.getId());
        if (familyRevision.getAnotherNames() != null) {
            familyRevision.getAnotherNames().forEach(an -> familyRevisionRepository.insertAnotherName(updatedFamilyRevision.getId(), an));
        }
        updateLinks(updatedFamilyRevision, familyRevision);
        entityManager.clear();
        return this.findFullInfoById(updatedFamilyRevision.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyRevision findFullInfoById(Long id) {
        return familyRevisionRepository.findFullInfoById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(String.format("Family revision not found by id: %d", id), 1));
    }

    @Override
    @Transactional(readOnly = true)
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
        if (!filter.getFindWithHavePerson()) {
            predicates.add(cb.isNull(root.get("person")));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return getGraphsResult(filter.getGraphs(), cq, entityManager);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected FamilyRevision updateLinks(FamilyRevision info) {
        info.setArchiveDocument(info.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(info.getArchiveDocument(), info.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);

        info.setPerson(info.getPerson() != null ?
                personHelper.saveEntityIfNotExist(info.getPerson(), info.getPerson().getId(), personRepository) :
                null);
        return info;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(FamilyRevision existInfo, FamilyRevision newInfo) {
        familyRevisionHelper.updateEntity(
                        existInfo.getId(),
                        existInfo.getPartner(),
                        newInfo.getPartner(),
                        FamilyRevision::getId,
                        familyRevisionRepository,
                        familyRevisionRepository::updatePartnerIdById)
                .ifPresent(savedPartner -> familyRevisionRepository.updatePartnerIdById(existInfo.getId(), savedPartner.getId()));

    }
}
