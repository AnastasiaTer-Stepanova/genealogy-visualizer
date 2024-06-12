package genealogy.visualizer.controller;

import genealogy.visualizer.api.DeathApi;
import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.service.death.DeathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeathController implements DeathApi {

    private final DeathService deathService;

    public DeathController(DeathService deathService) {
        this.deathService = deathService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        deathService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyDeath>> filter(DeathFilter deathFilter) {
        return ResponseEntity.ok(deathService.filter(deathFilter));
    }

    @Override
    public ResponseEntity<Death> getById(Long id) {
        return ResponseEntity.ok(deathService.getById(id));
    }

    @Override
    public ResponseEntity<Death> save(Death death) {
        return ResponseEntity.ok(deathService.save(death));
    }

    @Override
    public ResponseEntity<Death> update(Death death) {
        return ResponseEntity.ok(deathService.update(death));
    }
}
