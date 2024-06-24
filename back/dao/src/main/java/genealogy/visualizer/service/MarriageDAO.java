package genealogy.visualizer.service;

import genealogy.visualizer.dto.MarriageFilterDTO;
import genealogy.visualizer.entity.Marriage;

public interface MarriageDAO extends CrudDAO<Marriage>, FilterDAO<Marriage, MarriageFilterDTO> {
}
