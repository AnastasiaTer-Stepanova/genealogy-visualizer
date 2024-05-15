package genealogy.visualizer.service;

import genealogy.visualizer.entity.ArchiveDocument;

public interface ArchiveDocumentDAO {

    ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument);

    ArchiveDocument findArchiveDocumentWithFamilyRevisionByNumberFamily(ArchiveDocument archiveDocument, short familyNumber);

    void updateNextRevisionDocument(ArchiveDocument archiveDocument);
}
