package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

public interface ArchiveService extends CrudService<Archive>, FilterService<EasyArchive, ArchiveFilter> {
}
