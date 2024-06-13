package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.dto.FullNameFilterDTO;
import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.mapper.EasyPersonMapper;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.service.PersonDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

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

    @Override
    public List<EasyPerson> filter(PersonFilter filter) {
        return easyPersonMapper.toDTOs(personDAO.filter(personMapper.toFilter(filter)));
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
        return easyPersonMapper.toDTOs(personDAO.filter(filter));
    }
}
