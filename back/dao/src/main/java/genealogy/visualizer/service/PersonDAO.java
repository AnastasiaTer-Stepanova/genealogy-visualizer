package genealogy.visualizer.service;

import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.entity.Person;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface PersonDAO {

    void delete(Long id) throws IllegalArgumentException;

    Person save(Person person) throws IllegalArgumentException, EmptyResultDataAccessException;

    Person update(Person person) throws IllegalArgumentException, EmptyResultDataAccessException;

    Person findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<Person> filter(PersonFilterDTO filter) throws EmptyResultDataAccessException;

}
