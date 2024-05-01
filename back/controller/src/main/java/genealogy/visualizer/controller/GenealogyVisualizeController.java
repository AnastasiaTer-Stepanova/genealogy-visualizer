package genealogy.visualizer.controller;

import genealogy.visualizer.api.GenealogyVisualizeApi;
import genealogy.visualizer.api.model.GenealogyVisualizeRs;
import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;
import genealogy.visualizer.api.model.GraphLinks;
import genealogy.visualizer.api.model.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@RestController
public class GenealogyVisualizeController implements GenealogyVisualizeApi {

    @Override
    public ResponseEntity<GenealogyVisualizeRs> getGenealogyVisualizeGraph(GenealogyVisualizeRq GenealogyVisualizeRq) {
        //TODO Временная заглушка
        GenealogyVisualizeRs GenealogyVisualizeRs = new GenealogyVisualizeRs();
        GenealogyVisualizeGraph visualizeGraph = new GenealogyVisualizeGraph();
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setFullName(randomAlphabetic(20));
            person.setId(Integer.valueOf(randomNumeric(3)));
            visualizeGraph.addPersonsItem(person);
        }
        for (int i = 0; i < 4; i++) {
            GraphLinks links = new GraphLinks();
            links.setSource(visualizeGraph.getPersons().get(i).getId());
            links.setTarget(visualizeGraph.getPersons().get(i + 2).getId());
            visualizeGraph.addLinksItem(links);
        }
        GenealogyVisualizeRs.setGenealogyVisualizerGraph(visualizeGraph);
        return new ResponseEntity<>(GenealogyVisualizeRs, HttpStatus.OK);
    }
}
