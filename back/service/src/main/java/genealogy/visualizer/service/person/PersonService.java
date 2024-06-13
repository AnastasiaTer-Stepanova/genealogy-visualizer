package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;

import java.util.List;

public interface PersonService {

    void delete(Long id);

    Person getById(Long id);

    Person save(Person person);

    Person update(Person person);

    List<EasyPerson> filter(PersonFilter filter);

    List<EasyPerson> search(String string);
}
