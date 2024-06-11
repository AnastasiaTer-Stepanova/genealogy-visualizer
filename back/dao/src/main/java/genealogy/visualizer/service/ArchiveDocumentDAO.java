package genealogy.visualizer.service;

import genealogy.visualizer.dto.ArchiveDocumentFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;

import java.util.List;

public interface ArchiveDocumentDAO {

    void delete(Long id);

    ArchiveDocument save(ArchiveDocument archiveDocument);

    ArchiveDocument update(ArchiveDocument archiveDocument);

    ArchiveDocument findFullInfoById(Long id);

    List<ArchiveDocument> filter(ArchiveDocumentFilterDTO filter);

    ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument);

    ArchiveDocument findArchiveDocumentWithFamilyRevisionByNumberFamily(Long archiveDocumentId, short familyNumber);

    void updateNextRevisionDocument(ArchiveDocument archiveDocument);
}
