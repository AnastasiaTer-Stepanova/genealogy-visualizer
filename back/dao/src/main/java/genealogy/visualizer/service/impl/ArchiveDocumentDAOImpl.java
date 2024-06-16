package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.ArchiveDocumentFilterDTO;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.helper.RepositoryEasyModelHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArchiveDocumentDAOImpl implements ArchiveDocumentDAO {

    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final ArchiveRepository archiveRepository;
    private final ChristeningRepository christeningRepository;
    private final DeathRepository deathRepository;
    private final EntityManager entityManager;
    private final FamilyRevisionRepository familyRevisionRepository;
    private final MarriageRepository marriageRepository;

    public ArchiveDocumentDAOImpl(ArchiveRepository archiveRepository,
                                  ChristeningRepository christeningRepository,
                                  DeathRepository deathRepository,
                                  EntityManager entityManager,
                                  FamilyRevisionRepository familyRevisionRepository,
                                  MarriageRepository marriageRepository,
                                  ArchiveDocumentRepository archiveDocumentRepository) {
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.archiveRepository = archiveRepository;
        this.christeningRepository = christeningRepository;
        this.deathRepository = deathRepository;
        this.entityManager = entityManager;
        this.familyRevisionRepository = familyRevisionRepository;
        this.marriageRepository = marriageRepository;
    }

    private static final RepositoryEasyModelHelper<FamilyRevision> familyRevisionHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Christening> christeningHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Marriage> marriageHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Death> deathHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Archive> archiveHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        archiveDocumentRepository.updateNextRevisionId(id, null);
        familyRevisionRepository.updateArchiveDocumentId(id, null);
        christeningRepository.updateArchiveDocumentId(id, null);
        marriageRepository.updateArchiveDocumentId(id, null);
        deathRepository.updateArchiveDocumentId(id, null);
        archiveDocumentRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArchiveDocument save(ArchiveDocument archiveDocument) {
        if (archiveDocument.getId() != null)
            throw new IllegalArgumentException("Cannot save archive document with id");
        archiveDocument = updateLinks(archiveDocument);

        ArchiveDocument archiveDocumentForSave = archiveDocument.clone();
        archiveDocumentForSave.setFamilyRevisions(Collections.emptyList());
        archiveDocumentForSave.setChristenings(Collections.emptyList());
        archiveDocumentForSave.setDeaths(Collections.emptyList());
        archiveDocumentForSave.setMarriages(Collections.emptyList());
        archiveDocumentForSave.setPreviousRevisions(Collections.emptyList());

        ArchiveDocument savedArchiveDocument = archiveDocumentRepository.save(archiveDocumentForSave);
        updateLinks(savedArchiveDocument, archiveDocument);
        entityManager.clear();
        return this.findFullInfoById(savedArchiveDocument.getId());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArchiveDocument update(ArchiveDocument archiveDocument) {
        if (archiveDocument.getId() == null)
            throw new IllegalArgumentException("Cannot update archive document without id");
        archiveDocument = updateLinks(archiveDocument);
        ArchiveDocument updatedArchiveDocument = archiveDocumentRepository.update(archiveDocument);
        if (updatedArchiveDocument == null)
            throw new EmptyResultDataAccessException("Updating archive document failed", 1);
        updateLinks(updatedArchiveDocument, archiveDocument);
        entityManager.clear();
        return this.findFullInfoById(updatedArchiveDocument.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ArchiveDocument findFullInfoById(Long id) {
        String errorMes = String.format("Archive document not found by id: %d", id);
        archiveDocumentRepository.findWithArchiveAndNextRevisionAndPreviousRevisions(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        archiveDocumentRepository.findWithDeaths(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        archiveDocumentRepository.findWithChristenings(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        archiveDocumentRepository.findWithRevisions(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        return archiveDocumentRepository.findWithMarriages(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchiveDocument> filter(ArchiveDocumentFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ArchiveDocument> cq = cb.createQuery(ArchiveDocument.class);
        Root<ArchiveDocument> root = cq.from(ArchiveDocument.class);
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getAbbreviation() != null) {
            predicates.add(cb.like(cb.lower(root.get("abbreviation")), "%" + filter.getAbbreviation().toLowerCase() + "%"));
        }
        if (filter.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        if (filter.getType() != null) {
            predicates.add(cb.equal(cb.lower(root.get("type")), filter.getType().getName().toLowerCase()));
        }
        if (filter.getYear() != null) {
            predicates.add(cb.equal(root.get("year"), filter.getYear()));
        }
        if (filter.getArchiveId() != null) {
            Join<ArchiveDocument, Archive> join = root.join("archive", JoinType.LEFT);
            predicates.add(cb.equal(join.get("id"), filter.getArchiveId()));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument) {
        if (archiveDocument.getId() != null) {
            return this.findFullInfoById(archiveDocument.getId());
        }
        if (archiveDocument.getArchive() != null && archiveDocument.getArchive().getId() == null) {
            archiveRepository.findArchivedByName(archiveDocument.getArchive().getName())
                    .or(() -> Optional.of(archiveRepository.save(archiveDocument.getArchive())))
                    .ifPresent(archiveDocument::setArchive);
        }
        return Optional.ofNullable(findArchiveDocument(archiveDocument))
                .orElse(this.save(archiveDocument));
    }

    @Transactional(readOnly = true)
    protected ArchiveDocument findArchiveDocument(ArchiveDocument archiveDocument) {
        if (archiveDocument.getId() != null) {
            return archiveDocumentRepository.findById(archiveDocument.getId()).orElse(null);
        }
        if (archiveDocument.getArchive() != null && archiveDocument.getArchive().getId() != null &&
                archiveDocument.getFund() != null && archiveDocument.getCatalog() != null && archiveDocument.getInstance() != null &&
                archiveDocument.getBunch() != null && archiveDocument.getYear() != null && archiveDocument.getType() != null) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> cq = cb.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> adRoot = cq.from(ArchiveDocument.class);
            Join<ArchiveDocument, Archive> aJoin = adRoot.join("archive", JoinType.LEFT);

            Predicate predicate = cb.and(
                    cb.equal(aJoin.get("id"), archiveDocument.getArchive().getId()),
                    cb.equal(adRoot.get("fund"), archiveDocument.getFund()),
                    cb.equal(adRoot.get("catalog"), archiveDocument.getCatalog()),
                    cb.equal(adRoot.get("instance"), archiveDocument.getInstance()),
                    cb.equal(adRoot.get("bunch"), archiveDocument.getBunch()),
                    cb.equal(adRoot.get("year"), archiveDocument.getYear()),
                    cb.equal(adRoot.get("type"), archiveDocument.getType().getName())
            );
            cq.where(predicate);
            List<ArchiveDocument> result = entityManager.createQuery(cq).getResultList();
            if (result != null && result.size() == 1) {
                return result.getFirst();
            } else if (result != null && result.size() > 1) {
                throw new RuntimeException(String.format("Archive document has more than one with params archive_id: %s, " +
                                "fund: %s, catalog: %s, instance: %s, bunch: %s, year: %s, type: %s", archiveDocument.getArchive().getId(),
                        archiveDocument.getFund(), archiveDocument.getCatalog(), archiveDocument.getInstance(),
                        archiveDocument.getBunch(), archiveDocument.getYear(), archiveDocument.getType().getName()));
            }
        }
        return null;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected ArchiveDocument updateLinks(ArchiveDocument info) {
        info.setArchive(info.getArchive() != null ?
                archiveHelper.saveEntityIfNotExist(info.getArchive(), info.getArchive().getId(), archiveRepository) :
                null);
        info.setNextRevision(info.getNextRevision() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(info.getNextRevision(), info.getNextRevision().getId(), archiveDocumentRepository) :
                null);
        return info;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(ArchiveDocument existInfo, ArchiveDocument newInfo) {

        familyRevisionHelper.updateEntities(
                existInfo.getId(),
                existInfo.getFamilyRevisions(),
                newInfo.getFamilyRevisions(),
                FamilyRevision::getId,
                familyRevisionRepository,
                familyRevisionRepository::updateArchiveDocumentIdById);

        christeningHelper.updateEntities(
                existInfo.getId(),
                existInfo.getChristenings(),
                newInfo.getChristenings(),
                Christening::getId,
                christeningRepository,
                christeningRepository::updateArchiveDocumentIdById);

        marriageHelper.updateEntities(
                existInfo.getId(),
                existInfo.getMarriages(),
                newInfo.getMarriages(),
                Marriage::getId,
                marriageRepository,
                marriageRepository::updateArchiveDocumentIdById);

        deathHelper.updateEntities(
                existInfo.getId(),
                existInfo.getDeaths(),
                newInfo.getDeaths(),
                Death::getId,
                deathRepository,
                deathRepository::updateArchiveDocumentIdById);

        archiveDocumentHelper.updateEntities(
                existInfo.getId(),
                existInfo.getPreviousRevisions(),
                newInfo.getPreviousRevisions(),
                ArchiveDocument::getId,
                archiveDocumentRepository,
                archiveDocumentRepository::updateNextRevisionIdById);
    }
}
