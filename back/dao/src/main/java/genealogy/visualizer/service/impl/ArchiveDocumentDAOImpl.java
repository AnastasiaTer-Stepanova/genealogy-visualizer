package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ArchiveDocumentDAOImpl implements ArchiveDocumentDAO {

    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final ArchiveRepository archiveRepository;

    public ArchiveDocumentDAOImpl(ArchiveDocumentRepository archiveDocumentRepository,
                                  ArchiveRepository archiveRepository) {
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.archiveRepository = archiveRepository;
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
        return archiveDocumentRepository.findArchiveDocumentByConstraint(archiveDocument)
                .or(() -> {
                    ArchiveDocument nextRevision = archiveDocument.getNextRevision();
                    if (nextRevision != null) {
                        Archive nextArchive = nextRevision.getArchive();
                        if (nextRevision.getArchive().getId() == null) {
                            archiveRepository.findArchivedByName(nextArchive.getName())
                                    .or(() -> Optional.of(archiveRepository.save(nextArchive)))
                                    .ifPresent(nextRevision::setArchive);
                        }
                        archiveDocumentRepository.findArchiveDocumentByConstraint(nextRevision)
                                .or(() -> Optional.of(archiveDocumentRepository.save(nextRevision)))
                                .ifPresent(archiveDocument::setNextRevision);
                    }
                    return Optional.of(archiveDocumentRepository.save(archiveDocument));
                })
                .orElseThrow();
    }

    @Override
    public ArchiveDocument findArchiveDocumentWithFamilyRevisionByNumberFamily(Long archiveDocumentId, short familyNumber) {
        return archiveDocumentRepository.findArchiveDocumentWithFamilyRevisionByNumberFamily(archiveDocumentId, familyNumber)
                .orElse(null);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateNextRevisionDocument(ArchiveDocument archiveDocument) {
        if (archiveDocument == null || archiveDocument.getNextRevision() == null)
            throw new NullPointerException("Archive Document with next revision null");
        ArchiveDocument nextRevisionDocument = archiveDocument.getNextRevision();
        if (archiveDocument.getId() == null) {
            this.saveOrFindIfExistDocument(archiveDocument);
            return;
        }
        if (nextRevisionDocument.getId() == null) {
            nextRevisionDocument = this.saveOrFindIfExistDocument(nextRevisionDocument);
        }
        archiveDocumentRepository.updateNextRevisionId(archiveDocument.getId(), nextRevisionDocument.getId());
    }
}
