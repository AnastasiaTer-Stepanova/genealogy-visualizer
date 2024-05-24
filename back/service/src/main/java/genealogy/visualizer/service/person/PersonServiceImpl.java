package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.service.PersonDAO;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class PersonServiceImpl implements PersonService {

    private final PersonDAO personDAO;
    private final PersonMapper personMapper;

    public PersonServiceImpl(PersonDAO personDAO, PersonMapper personMapper) {
        this.personDAO = personDAO;
        this.personMapper = personMapper;
    }

    @Override
    public void delete(Long id) {
        personDAO.delete(id);
    }

    @Override
    public Person getById(Long id) {
        genealogy.visualizer.entity.Person entity = personDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return personMapper.toDTO(entity);
    }

    @Override
    public Person save(Person person) {
        genealogy.visualizer.entity.Person entity = personDAO.save(personMapper.toEntity(person));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return personMapper.toDTO(entity);
    }

    @Override
    public Person update(Person person) {
        genealogy.visualizer.entity.Person entity = personDAO.update(personMapper.toEntity(person));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return personMapper.toDTO(entity);
    }
}
