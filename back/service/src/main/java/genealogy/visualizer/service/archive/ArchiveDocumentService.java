package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;

import java.util.List;

public interface ArchiveDocumentService {

    void delete(Long id);

    ArchiveDocument getById(Long id);

    ArchiveDocument save(ArchiveDocument archiveDocument);

    ArchiveDocument update(ArchiveDocument archiveDocument);

    List<EasyArchiveDocument> filter(ArchiveDocumentFilter filter);
}
