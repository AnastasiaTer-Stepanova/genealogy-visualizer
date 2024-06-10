package genealogy.visualizer.service;

import genealogy.visualizer.entity.Archive;

public interface ArchiveDAO {

    void delete(Long id);

    Archive save(Archive archive);

    Archive update(Archive archive);

    Archive findFullInfoById(Long id);

}
