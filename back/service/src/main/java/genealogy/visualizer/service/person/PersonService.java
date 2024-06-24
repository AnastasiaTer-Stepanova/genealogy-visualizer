package genealogy.visualizer.service.person;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

import java.util.List;

public interface PersonService extends CrudService<Person>, FilterService<EasyPerson, PersonFilter> {

    List<EasyPerson> search(String string);
}
