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
        return archiveDocumentRepository.findArchiveDocumentByConstraint(
                        archiveDocument.getArchive().getName(),
                        archiveDocument.getFund(),
                        archiveDocument.getCatalog(),
                        archiveDocument.getInstance(),
                        archiveDocument.getBunch(),
                        archiveDocument.getYear(),
                        archiveDocument.getType())
                .or(() -> Optional.of(archiveDocumentRepository.save(archiveDocument)))
                .orElseThrow();
    }

    @Override
    public ArchiveDocument findArchiveDocumentWithFamilyRevisionByNumberFamily(ArchiveDocument archiveDocument, short familyNumber) {
        return archiveDocumentRepository.findArchiveDocumentWithFamilyRevisionByNumberFamily(
                        archiveDocument.getArchive().getName(),
                        archiveDocument.getFund(),
                        archiveDocument.getCatalog(),
                        archiveDocument.getInstance(),
                        archiveDocument.getBunch(),
                        archiveDocument.getYear(),
                        archiveDocument.getType(),
                        familyNumber)
                .orElse(null);
    }
}
