package genealogy.visualizer.service;

import genealogy.visualizer.dto.ArchiveFilterDTO;
import genealogy.visualizer.entity.Archive;

public interface ArchiveDAO extends CrudDAO<Archive>, FilterDAO<Archive, ArchiveFilterDTO> {
}
