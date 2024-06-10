package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class ArchiveDocumentDAOImpl implements ArchiveDocumentDAO {

    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final ArchiveRepository archiveRepository;
    private final EntityManager entityManager;

    public ArchiveDocumentDAOImpl(ArchiveDocumentRepository archiveDocumentRepository,
                                  ArchiveRepository archiveRepository,
                                  EntityManager entityManager) {
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.archiveRepository = archiveRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument) {
        Archive archive = archiveDocument.getArchive();
        if (archive == null) throw new NullPointerException("ArchiveDocument does not contains archive");
        if (archive.getId() == null) {
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

        archiveDocumentRepository.updateNextRevisionId(archiveDocumentId, nextRevisionId);
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
