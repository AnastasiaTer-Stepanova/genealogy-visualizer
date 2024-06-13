package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;
import genealogy.visualizer.api.model.GraphLinks;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenealogyVisualizeControllerTest extends IntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    private final Set<Long> personIds = new HashSet<>();

    @Test
    void getGenealogyVisualizeGraphTest() throws Exception {
        List<Person> persons = generator.objects(Person.class, generator.nextInt(10, 15)).toList();
        persons.forEach(person -> {
            person.setBirthLocality(localityExisting);
            person.setDeathLocality(localityExisting);
            person.setChristening(null);
            person.setDeath(null);
            person.setMarriages(null);
            person.setRevisions(null);
            person.setParents(null);
            person.setPartners(null);
            person.setChildren(null);
        });
        persons = personRepository.saveAllAndFlush(persons);
        Set<GraphLinks> graphLinks = new HashSet<>();
        persons.get(0).setPartners(List.of(persons.get(1)));
        graphLinks.add(new GraphLinks().source(persons.get(0).getId()).target(persons.get(1).getId()));
        persons.get(2).setParents(List.of(persons.get(0), persons.get(1)));
        graphLinks.add(new GraphLinks().source(persons.get(2).getId()).target(persons.get(0).getId()));
        graphLinks.add(new GraphLinks().source(persons.get(2).getId()).target(persons.get(1).getId()));
        persons = personRepository.saveAllAndFlush(persons);
        persons.get(1).setPartners(List.of(persons.get(0)));
        graphLinks.add(new GraphLinks().source(persons.get(1).getId()).target(persons.get(0).getId()));
        persons.get(0).setChildren(List.of(persons.get(2)));
        graphLinks.add(new GraphLinks().source(persons.get(0).getId()).target(persons.get(2).getId()));
        persons.get(1).setChildren(List.of(persons.get(2)));
        graphLinks.add(new GraphLinks().source(persons.get(1).getId()).target(persons.get(2).getId()));
        persons = personRepository.saveAllAndFlush(persons);
        persons = persons.stream().sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).toList();
        personIds.addAll(persons.stream().map(Person::getId).toList());
        String responseJson = getRequest("/genealogy-visualizer/graph", objectMapper.writeValueAsString(new GenealogyVisualizeRq()));
        GenealogyVisualizeGraph response = objectMapper.readValue(responseJson, GenealogyVisualizeGraph.class);
        assertNotNull(response);
        assertEquals(response.getPersons().size(), persons.size());
        List<EasyPerson> resultPersons = response.getPersons().stream().sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).toList();
        for (int i = 0; i < persons.size(); i++) {
            assertPerson(resultPersons.get(i), persons.get(i));
        }
        Set<GraphLinks> resultLinks = response.getLinks();
        assertEquals(response.getLinks().size(), graphLinks.size());
        for (GraphLinks graphLink : graphLinks) {
            assertTrue(resultLinks.contains(graphLink));
        }
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        personRepository.deleteAllById(personIds);
        super.tearDown();
    }
}