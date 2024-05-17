package genealogy.visualizer.controller;

import genealogy.visualizer.api.GenealogyVisualizeApi;
import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;
import genealogy.visualizer.service.graph.GenealogyVisualizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenealogyVisualizeController implements GenealogyVisualizeApi {

    private final GenealogyVisualizeService genealogyVisualizeService;

    public GenealogyVisualizeController(GenealogyVisualizeService genealogyVisualizeService) {
        this.genealogyVisualizeService = genealogyVisualizeService;
    }

    @Override
    public ResponseEntity<GenealogyVisualizeGraph> getGenealogyVisualizeGraph(GenealogyVisualizeRq genealogyVisualizeRq) {
        return ResponseEntity.ok(genealogyVisualizeService.getGenealogyVisualizeGraph(genealogyVisualizeRq));
    }
}
