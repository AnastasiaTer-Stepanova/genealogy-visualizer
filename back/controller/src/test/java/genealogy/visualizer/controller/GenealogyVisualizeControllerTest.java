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
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        String objectString = objectMapper.writeValueAsString(new GenealogyVisualizeRq());
        String responseJson = mockMvc.perform(
                        post("/genealogy-visualizer/graph")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
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

    private void assertPerson(EasyPerson person1, Person person2) {
        assertEquals(person1.getId(), person2.getId());
        assertEquals(person1.getBirthDate().getDate(), person2.getBirthDate().getDate());
        assertEquals(person1.getBirthDate().getDateRangeType().getValue(), person2.getBirthDate().getDateRangeType().getName());
        assertEquals(person1.getDeathDate().getDate(), person2.getDeathDate().getDate());
        assertEquals(person1.getDeathDate().getDateRangeType().getValue(), person2.getDeathDate().getDateRangeType().getName());
        assertFullName(person1.getFullName(), person2.getFullName());
    }
}