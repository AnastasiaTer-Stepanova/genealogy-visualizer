package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.service.ArchiveDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchiveDAOImpl implements ArchiveDAO {

    private final ArchiveRepository archiveRepository;

    private final ArchiveDocumentRepository archiveDocumentRepository;

    public ArchiveDAOImpl(ArchiveRepository archiveRepository, ArchiveDocumentRepository archiveDocumentRepository) {
        this.archiveRepository = archiveRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        archiveDocumentRepository.updateArchiveId(id, null);
        archiveRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive save(Archive archive) {
        if (archive.getId() != null)
            throw new IllegalArgumentException("Cannot save archive with id");
        saveArchiveDocumentsIfNotExist(archive.getArchiveDocuments());
        return archiveRepository.save(archive);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive update(Archive archive) {
        Archive updatedArchive = archiveRepository.update(archive);
        return updateArchiveDocuments(updatedArchive, archive.getArchiveDocuments());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive findFullInfoById(Long id) {
        return archiveRepository.findByIdWithArchiveDocuments(id).orElse(null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Archive updateArchiveDocuments(Archive archive, List<ArchiveDocument> archiveDocuments) {
        archiveDocuments = saveArchiveDocumentsIfNotExist(archiveDocuments);
        Set<Long> newIds = archiveDocuments.stream().map(ArchiveDocument::getId).collect(Collectors.toSet());
        Set<Long> existIds = archive.getArchiveDocuments().stream().map(ArchiveDocument::getId).collect(Collectors.toSet());

        Set<Long> idsForDelete = existIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());
        idsForDelete.forEach(id -> archiveDocumentRepository.updateArchiveIdById(id, null));

        List<ArchiveDocument> resultArchiveDocument = archive.getArchiveDocuments().stream()
                .filter(ad -> !idsForDelete.contains(ad.getId()))
                .collect(Collectors.toList());

        archiveDocuments.stream()
                .filter(ad -> newIds.contains(ad.getId()) && !existIds.contains(ad.getId()))
                .forEach(ad -> {
                    archiveDocumentRepository.updateArchiveIdById(ad.getId(), archive.getId());
                    resultArchiveDocument.add(ad);
                });

        archive.setArchiveDocuments(resultArchiveDocument);
        return archive;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected List<ArchiveDocument> saveArchiveDocumentsIfNotExist(List<ArchiveDocument> archiveDocuments) {
        if (archiveDocuments == null || archiveDocuments.isEmpty()) return Collections.emptyList();
        List<ArchiveDocument> resultArchiveDocuments = new ArrayList<>(archiveDocuments.size());
        for (ArchiveDocument archiveDocument : archiveDocuments) {
            if (archiveDocument.getId() == null) {
                resultArchiveDocuments.add(archiveDocumentRepository.save(archiveDocument));
            } else {
                archiveDocumentRepository.findById(archiveDocument.getId()).ifPresent(resultArchiveDocuments::add);
            }
        }
        return resultArchiveDocuments;
    }
}
