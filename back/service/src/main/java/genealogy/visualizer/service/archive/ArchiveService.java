package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.Archive;

public interface ArchiveService {

    void delete(Long id);

    Archive getById(Long id);

    Archive save(Archive archive);

    Archive update(Archive archive);

}
