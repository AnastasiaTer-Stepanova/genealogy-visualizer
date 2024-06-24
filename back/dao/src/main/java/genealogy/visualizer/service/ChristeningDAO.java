package genealogy.visualizer.service;

import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.entity.Christening;

public interface ChristeningDAO extends CrudDAO<Christening>, FilterDAO<Christening, ChristeningFilterDTO> {
}
