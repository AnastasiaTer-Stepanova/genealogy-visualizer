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
        List<FamilyRevision> familyRevisions = archiveDocument.getFamilyRevisions();
        List<Christening> christenings = archiveDocument.getChristenings();
        List<Death> deaths = archiveDocument.getDeaths();
        List<Marriage> marriages = archiveDocument.getMarriages();
        List<ArchiveDocument> previousRevisions = archiveDocument.getPreviousRevisions();
        archiveDocument.setFamilyRevisions(Collections.emptyList());
        archiveDocument.setChristenings(Collections.emptyList());
        archiveDocument.setDeaths(Collections.emptyList());
        archiveDocument.setMarriages(Collections.emptyList());
        archiveDocument.setPreviousRevisions(Collections.emptyList());
        archiveDocument.setArchive(archiveDocument.getArchive() != null ?
                archiveHelper.saveEntityIfNotExist(archiveDocument.getArchive(), archiveDocument.getArchive().getId(), archiveRepository) :
                null);
        archiveDocument.setNextRevision(archiveDocument.getNextRevision() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(archiveDocument.getNextRevision(), archiveDocument.getNextRevision().getId(), archiveDocumentRepository) :
                null);
        ArchiveDocument savedArchiveDocument = archiveDocumentRepository.save(archiveDocument);
        if (familyRevisions != null && !familyRevisions.isEmpty()) {
            familyRevisions.forEach(fr -> fr.setArchiveDocument(savedArchiveDocument));
            archiveDocument.setFamilyRevisions(familyRevisionHelper.saveEntitiesIfNotExist(
                    familyRevisions, familyRevisionRepository, FamilyRevision::getId));
        }
        if (christenings != null && !christenings.isEmpty()) {
            christenings.forEach(c -> c.setArchiveDocument(savedArchiveDocument));
            savedArchiveDocument.setChristenings(christeningHelper.saveEntitiesIfNotExist(
                    christenings, christeningRepository, Christening::getId));
        }
        if (marriages != null && !marriages.isEmpty()) {
            marriages.forEach(m -> m.setArchiveDocument(savedArchiveDocument));
            savedArchiveDocument.setMarriages(marriageHelper.saveEntitiesIfNotExist(
                    marriages, marriageRepository, Marriage::getId));
        }
        if (deaths != null && !deaths.isEmpty()) {
            deaths.forEach(d -> d.setArchiveDocument(savedArchiveDocument));
            savedArchiveDocument.setDeaths(deathHelper.saveEntitiesIfNotExist(
                    deaths, deathRepository, Death::getId));
        }
        if (previousRevisions != null && !previousRevisions.isEmpty()) {
            previousRevisions.forEach(pr -> pr.setNextRevision(savedArchiveDocument));
            savedArchiveDocument.setPreviousRevisions(archiveDocumentHelper.saveEntitiesIfNotExist(
                    previousRevisions, archiveDocumentRepository, ArchiveDocument::getId));
        }
        return savedArchiveDocument;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArchiveDocument update(ArchiveDocument archiveDocument) {
        if (archiveDocument.getId() == null)
            throw new IllegalArgumentException("Cannot update archive document without id");

        archiveDocument.setArchive(archiveDocument.getArchive() != null ?
                archiveHelper.saveEntityIfNotExist(archiveDocument.getArchive(), archiveDocument.getArchive().getId(), archiveRepository) :
                null);
        archiveDocument.setNextRevision(archiveDocument.getNextRevision() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(archiveDocument.getNextRevision(), archiveDocument.getNextRevision().getId(), archiveDocumentRepository) :
                null);

        ArchiveDocument updatedArchiveDocument = archiveDocumentRepository.update(archiveDocument);

        updatedArchiveDocument.setFamilyRevisions(familyRevisionHelper.updateEntities(
                updatedArchiveDocument.getId(), updatedArchiveDocument.getFamilyRevisions(), archiveDocument.getFamilyRevisions(),
                FamilyRevision::getId, familyRevisionRepository, familyRevisionRepository::updateArchiveDocumentIdById));
        updatedArchiveDocument.setChristenings(christeningHelper.updateEntities(
                updatedArchiveDocument.getId(), updatedArchiveDocument.getChristenings(), archiveDocument.getChristenings(),
                Christening::getId, christeningRepository, christeningRepository::updateArchiveDocumentIdById));
        updatedArchiveDocument.setMarriages(marriageHelper.updateEntities(
                updatedArchiveDocument.getId(), updatedArchiveDocument.getMarriages(), archiveDocument.getMarriages(),
                Marriage::getId, marriageRepository, marriageRepository::updateArchiveDocumentIdById));
        updatedArchiveDocument.setDeaths(deathHelper.updateEntities(
                updatedArchiveDocument.getId(), updatedArchiveDocument.getDeaths(), archiveDocument.getDeaths(), Death::getId,
                deathRepository, deathRepository::updateArchiveDocumentIdById));
        updatedArchiveDocument.setPreviousRevisions(archiveDocumentHelper.updateEntities(
                updatedArchiveDocument.getId(), updatedArchiveDocument.getPreviousRevisions(), archiveDocument.getPreviousRevisions(),
                ArchiveDocument::getId, archiveDocumentRepository, archiveDocumentRepository::updateNextRevisionIdById));

        return updatedArchiveDocument;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArchiveDocument findFullInfoById(Long id) {
        ArchiveDocument archiveDocument = archiveDocumentRepository.findFullInfoById(id).orElse(null);
        if (archiveDocument == null) return null;
        archiveDocumentRepository.findByIdWithDeath(id).orElseThrow();
        archiveDocumentRepository.findByIdWithMarriages(id).orElseThrow();
        archiveDocumentRepository.findByIdWithFamilyRevisions(id).orElseThrow();
        return archiveDocumentRepository.findByIdWithChristenings(id).orElseThrow();
    }

    @Override
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
        Archive archive = archiveDocument.getArchive();
        if (archive != null && archive.getId() == null) {
            archiveRepository.findArchivedByName(archive.getName())
                    .or(() -> Optional.of(archiveRepository.save(archive)))
                    .ifPresent(archiveDocument::setArchive);
        }
        ArchiveDocument result = findArchiveDocument(archiveDocument);
        if (result != null) {
            return result;
        }
        ArchiveDocument nextRevision = archiveDocument.getNextRevision();
        if (nextRevision != null) {
            Archive nextArchive = nextRevision.getArchive();
            if (nextRevision.getArchive() != null && nextRevision.getArchive().getId() == null) {
                archiveRepository.findArchivedByName(nextArchive.getName())
                        .or(() -> Optional.of(archiveRepository.save(nextArchive)))
                        .ifPresent(nextRevision::setArchive);
            }
            ArchiveDocument nextRevisionResult = findArchiveDocument(nextRevision);
            if (nextRevisionResult == null) {
                archiveDocument.setNextRevision(archiveDocumentRepository.save(nextRevision));
            }
        }
        return archiveDocumentRepository.save(archiveDocument);
    }

    @Override
    public ArchiveDocument findArchiveDocumentWithFamilyRevisionByNumberFamily(Long archiveDocumentId, short familyNumber) {
        return archiveDocumentRepository.findArchiveDocumentWithFamilyRevisionByNumberFamily(archiveDocumentId, familyNumber)
                .orElse(null);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateNextRevisionDocument(ArchiveDocument archiveDocument) {
        if (archiveDocument == null || archiveDocument.getNextRevision() == null) {
            throw new NullPointerException("Archive Document with next revision null");
        }

        ArchiveDocument nextRevisionDocument = archiveDocument.getNextRevision();
        Long archiveDocumentId = archiveDocument.getId();
        if (archiveDocumentId == null) {
            archiveDocumentId = saveOrFindIfExistDocument(archiveDocument).getId();
        }

        Long nextRevisionId = nextRevisionDocument.getId();
        if (nextRevisionId == null) {
            nextRevisionId = saveOrFindIfExistDocument(nextRevisionDocument).getId();
        }
        archiveDocumentRepository.updateNextRevisionIdById(archiveDocumentId, nextRevisionId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
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
}
