package genealogy.visualizer.service;

import genealogy.visualizer.dto.DeathFilterDTO;
import genealogy.visualizer.entity.Death;

public interface DeathDAO extends CrudDAO<Death>, FilterDAO<Death, DeathFilterDTO> {
}
