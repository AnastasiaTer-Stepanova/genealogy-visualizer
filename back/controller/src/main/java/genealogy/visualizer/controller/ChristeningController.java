package genealogy.visualizer.controller;

import genealogy.visualizer.api.ChristeningApi;
import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.service.christening.ChristeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChristeningController implements ChristeningApi {

    private final ChristeningService christeningService;

    public ChristeningController(ChristeningService christeningService) {
        this.christeningService = christeningService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        christeningService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyChristening>> filter(ChristeningFilter christeningFilter) {
        return ResponseEntity.ok(christeningService.filter(christeningFilter));
    }

    @Override
    public ResponseEntity<Christening> getById(Long id) {
        return ResponseEntity.ok(christeningService.getById(id));
    }

    @Override
    public ResponseEntity<Christening> save(Christening christening) {
        return ResponseEntity.ok(christeningService.save(christening));
    }

    @Override
    public ResponseEntity<Christening> update(Christening christening) {
        return ResponseEntity.ok(christeningService.update(christening));
    }
}
