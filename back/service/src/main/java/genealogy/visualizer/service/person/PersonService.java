package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.Person;

public interface PersonService {

    void delete(Long id);

    Person getById(Long id);

    Person save(Person person);

    Person update(Person person);
}
