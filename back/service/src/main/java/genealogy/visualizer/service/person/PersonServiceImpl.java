package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.dto.FullNameFilterDTO;
import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.mapper.EasyPersonMapper;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.PersonDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

public class PersonServiceImpl implements PersonService {

    private final PersonDAO personDAO;
    private final PersonMapper personMapper;
    private final EasyPersonMapper easyPersonMapper;

    public PersonServiceImpl(PersonDAO personDAO, PersonMapper personMapper, EasyPersonMapper easyPersonMapper) {
        this.personDAO = personDAO;
        this.personMapper = personMapper;
        this.easyPersonMapper = easyPersonMapper;
    }

    @Override
    public void delete(Long id) {
        personDAO.delete(id);
    }

    @Override
    public Person getById(Long id) {
        return Optional.ofNullable(personMapper.toDTO(personDAO.findFullInfoById(id)))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Person save(Person person) {
        if (person == null || person.getId() != null) {
            throw new BadRequestException("Person must not have an id");
        }
        return personMapper.toDTO(personDAO.save(personMapper.toEntity(person)));
    }

    @Override
    public Person update(Person person) {
        if (person == null || person.getId() == null) {
            throw new BadRequestException("Person must have an id");
        }
        genealogy.visualizer.entity.Person entity;
        try {
            entity = personDAO.update(personMapper.toEntity(person));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Person for update not found");
        }
        return personMapper.toDTO(entity);
    }

    @Override
    public List<EasyPerson> filter(PersonFilter filter) {
        return Optional.ofNullable(easyPersonMapper.toDTOs(personDAO.filter(personMapper.toFilter(filter))))
                .orElseThrow(NotFoundException::new);
    }

    //TODO Планируется перейти на поиск в elasticSearch
    @Override
    public List<EasyPerson> search(String string) {
        String[] strings = string.split(" ");
        FullNameFilterDTO fullName = new FullNameFilterDTO();
        fullName.setName(strings.length > 0 ? strings[0] : null);
        fullName.setSurname(strings.length > 1 ? strings[1] : null);
        fullName.setLastName(strings.length > 2 ? strings[2] : null);
        PersonFilterDTO filter = new PersonFilterDTO();
        filter.setFullName(fullName);
        return Optional.ofNullable(easyPersonMapper.toDTOs(personDAO.filter(filter)))
                .orElseThrow(NotFoundException::new);
    }
}
