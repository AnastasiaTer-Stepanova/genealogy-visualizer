package genealogy.visualizer.service;

import genealogy.visualizer.dto.LocalityFilterDTO;
import genealogy.visualizer.entity.Locality;

public interface LocalityDAO extends CrudDAO<Locality>, FilterDAO<Locality, LocalityFilterDTO> {
}
