package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.dto.FullNameFilterDTO;
import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.mapper.EasyPersonMapper;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.PersonDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public class PersonServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.Person, Person, PersonFilter, EasyPerson, PersonFilterDTO>
        implements PersonService {

    private final PersonDAO personDAO;
    private final EasyPersonMapper easyPersonMapper;

    public PersonServiceImpl(PersonDAO personDAO, PersonMapper personMapper, EasyPersonMapper easyPersonMapper) {
        super(personDAO, personDAO, personMapper, personMapper, easyPersonMapper);
        this.personDAO = personDAO;
        this.easyPersonMapper = easyPersonMapper;
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public Person getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Person save(Person person) {
        return super.save(person);
    }

    @Override
    public Person update(Person person) {
        return super.update(person);
    }

    @Override
    public List<EasyPerson> filter(PersonFilter filter) {
        return super.filter(filter);
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
        try {
            return easyPersonMapper.toDTOs(personDAO.filter(filter));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Persons not found");
        }
    }
}
