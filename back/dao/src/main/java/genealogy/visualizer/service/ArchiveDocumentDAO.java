package genealogy.visualizer.service;

import genealogy.visualizer.entity.ArchiveDocument;

public interface ArchiveDocumentDAO {

    ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument);
}
