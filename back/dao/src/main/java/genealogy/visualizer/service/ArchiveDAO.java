package genealogy.visualizer.service;

import genealogy.visualizer.dto.ArchiveFilterDTO;
import genealogy.visualizer.entity.Archive;

import java.util.List;

public interface ArchiveDAO {

    void delete(Long id);

    Archive save(Archive archive);

    Archive update(Archive archive);

    Archive findFullInfoById(Long id);

    List<Archive> filter(ArchiveFilterDTO filter);

}
