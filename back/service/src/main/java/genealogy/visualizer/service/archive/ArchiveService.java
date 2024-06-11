package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.EasyArchive;

import java.util.List;

public interface ArchiveService {

    void delete(Long id);

    Archive getById(Long id);

    Archive save(Archive archive);

    Archive update(Archive archive);

    List<EasyArchive> filter(ArchiveFilter filter);

}
