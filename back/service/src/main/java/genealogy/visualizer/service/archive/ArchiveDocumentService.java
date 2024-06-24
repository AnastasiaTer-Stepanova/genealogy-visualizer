package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

public interface ArchiveDocumentService extends CrudService<ArchiveDocument>, FilterService<EasyArchiveDocument, ArchiveDocumentFilter> {
}
