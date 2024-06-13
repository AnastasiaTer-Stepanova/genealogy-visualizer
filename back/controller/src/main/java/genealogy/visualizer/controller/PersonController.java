package genealogy.visualizer.controller;

import genealogy.visualizer.api.PersonApi;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.service.person.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController implements PersonApi {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyPerson>> filter(PersonFilter personFilter) {
        return ResponseEntity.ok(personService.filter(personFilter));
    }

    @Override
    public ResponseEntity<Person> getById(Long id) {
        return ResponseEntity.ok(personService.getById(id));
    }

    @Override
    public ResponseEntity<Person> save(Person person) {
        return ResponseEntity.ok(personService.save(person));
    }

    @Override
    public ResponseEntity<List<EasyPerson>> search(String body) {
        return ResponseEntity.ok(personService.search(body));
    }

    @Override
    public ResponseEntity<Person> update(Person person) {
        return ResponseEntity.ok(personService.update(person));
    }
}
