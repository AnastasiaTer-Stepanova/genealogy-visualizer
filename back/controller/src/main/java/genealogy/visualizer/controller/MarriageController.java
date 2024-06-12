package genealogy.visualizer.controller;

import genealogy.visualizer.api.MarriageApi;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.service.marriage.MarriageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarriageController implements MarriageApi {

    private final MarriageService marriageService;

    public MarriageController(MarriageService marriageService) {
        this.marriageService = marriageService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        marriageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyMarriage>> filter(MarriageFilter marriageFilter) {
        return ResponseEntity.ok(marriageService.filter(marriageFilter));
    }

    @Override
    public ResponseEntity<Marriage> getById(Long id) {
        return ResponseEntity.ok(marriageService.getById(id));
    }

    @Override
    public ResponseEntity<Marriage> save(Marriage marriage) {
        return ResponseEntity.ok(marriageService.save(marriage));
    }

    @Override
    public ResponseEntity<Marriage> update(Marriage marriage) {
        return ResponseEntity.ok(marriageService.update(marriage));
    }
}
