package genealogy.visualizer.service;

import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.entity.Person;

public interface PersonDAO extends CrudDAO<Person>, FilterDAO<Person, PersonFilterDTO> {
}
