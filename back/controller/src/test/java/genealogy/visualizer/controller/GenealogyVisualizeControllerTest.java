package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;
import genealogy.visualizer.api.model.GraphLinks;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenealogyVisualizeControllerTest extends IntegrationTest {

    @Test
    void getGenealogyVisualizeGraphTest() throws Exception {
        GenealogyVisualizeGraph response = objectMapper.readValue(getRequest("/genealogy-visualizer/graph", objectMapper.writeValueAsString(new GenealogyVisualizeRq())),
                GenealogyVisualizeGraph.class);
        assertNotNull(response);
        assertEquals(response.getPersons().size(), existingPersons.size());
        Map<Long, EasyPerson> findPersons = response.getPersons().stream()
                .collect(Collectors.toMap(EasyPerson::getId, p -> p));
        existingPersons.forEach(person -> {
            assertNotNull(findPersons.get(person.getId()));
            assertPerson(findPersons.get(person.getId()), person);
            if (person.getChildren() != null && !person.getChildren().isEmpty()) {
                person.getChildren().forEach(c -> {
                    assertTrue(response.getLinks().contains(new GraphLinks().source(c.getId()).target(person.getId())));
                    assertTrue(response.getLinks().contains(new GraphLinks().source(person.getId()).target(c.getId())));
                });
            }
            if (person.getPartners() != null && !person.getPartners().isEmpty()) {
                person.getPartners().forEach(p -> {
                    assertTrue(response.getLinks().contains(new GraphLinks().source(p.getId()).target(person.getId())));
                    assertTrue(response.getLinks().contains(new GraphLinks().source(person.getId()).target(p.getId())));
                });
            }
            if (person.getParents() != null && !person.getParents().isEmpty()) {
                person.getParents().forEach(p -> {
                    assertTrue(response.getLinks().contains(new GraphLinks().source(p.getId()).target(person.getId())));
                    assertTrue(response.getLinks().contains(new GraphLinks().source(person.getId()).target(p.getId())));
                });
            }
        });
    }
}