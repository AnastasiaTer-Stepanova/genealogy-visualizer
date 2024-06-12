package genealogy.visualizer.controller;

import genealogy.visualizer.api.LocalityApi;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.service.locality.LocalityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocalityController implements LocalityApi {

    private final LocalityService localityService;

    public LocalityController(LocalityService localityService) {
        this.localityService = localityService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        localityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<EasyLocality>> filter(LocalityFilter localityFilter) {
        return ResponseEntity.ok(localityService.filter(localityFilter));
    }

    @Override
    public ResponseEntity<Locality> getById(Long id) {
        return ResponseEntity.ok(localityService.getById(id));
    }

    @Override
    public ResponseEntity<Locality> save(Locality locality) {
        return ResponseEntity.ok(localityService.save(locality));
    }

    @Override
    public ResponseEntity<Locality> update(Locality locality) {
        return ResponseEntity.ok(localityService.update(locality));
    }
}
