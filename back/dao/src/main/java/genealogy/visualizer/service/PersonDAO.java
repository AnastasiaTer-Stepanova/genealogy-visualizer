package genealogy.visualizer.service;

import genealogy.visualizer.entity.Person;

import java.util.List;

public interface PersonDAO {

    List<Person> getAllEasyPersons();

    void delete(Long id);

    Person save(Person person);

    Person update(Person person);

    Person findFullInfoById(Long id);
}
