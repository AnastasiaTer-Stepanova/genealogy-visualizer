package genealogy.visualizer.controller;

import genealogy.visualizer.api.GenealogyVisualizeApi;
import genealogy.visualizer.api.model.GenealogyVisualizeErrorResponse;
import genealogy.visualizer.api.model.GenealogyVisualizeGraph;
import genealogy.visualizer.api.model.GenealogyVisualizeResponse;
import genealogy.visualizer.api.model.GenealogyVisualizeRq;
import genealogy.visualizer.service.graph.GenealogyVisualizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenealogyVisualizeController implements GenealogyVisualizeApi {

    private final GenealogyVisualizeService genealogyVisualizeService;

    public GenealogyVisualizeController(GenealogyVisualizeService genealogyVisualizeService) {
        this.genealogyVisualizeService = genealogyVisualizeService;
    }

    @Override
    public ResponseEntity<GenealogyVisualizeResponse> getGenealogyVisualizeGraph(GenealogyVisualizeRq genealogyVisualizeRq) {
        GenealogyVisualizeResponse result = genealogyVisualizeService.getGenealogyVisualizeGraph(genealogyVisualizeRq);
        return switch (result) {
            case GenealogyVisualizeGraph g -> ResponseEntity.ok().body(g);
            case GenealogyVisualizeErrorResponse e -> getErrorResponse(e);
            default -> ResponseEntity.internalServerError().build();
        };
    }

    private ResponseEntity<GenealogyVisualizeResponse> getErrorResponse(GenealogyVisualizeErrorResponse result) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if (result == null || result.getCode() == null) {
            return new ResponseEntity<>(null, httpStatus);
        }
        httpStatus = HttpStatus.valueOf(result.getCode());
        return new ResponseEntity<>(result, httpStatus);
    }
}
