package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.PersonDAO;

import java.util.List;

public class PersonDAOImpl implements PersonDAO {

    private final PersonRepository personRepository;

    public PersonDAOImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> getAllEasyPersons() {
        return personRepository.findAll();
    }
}
