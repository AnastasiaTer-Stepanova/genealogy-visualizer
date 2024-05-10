package genealogy.visualizer.controller;

import genealogy.visualizer.api.FamilyRevisionApi;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.api.model.GetById200Response;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import genealogy.visualizer.service.util.record.ResponseRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FamilyRevisionController implements FamilyRevisionApi {

    private final FamilyRevisionService familyRevisionService;

    public FamilyRevisionController(FamilyRevisionService familyRevisionService) {
        this.familyRevisionService = familyRevisionService;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        familyRevisionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<GetById200Response> getById(Long id) {
        ResponseRecord<FamilyRevision> result = familyRevisionService.getById(id);
        if (result == null || result.error() != null) {
            return new ResponseEntity<>(result != null ? result.error() : null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(result.value());
    }

    @Override
    public ResponseEntity<GetById200Response> save(FamilyRevisionSave familyRevisionSave) {
        ResponseRecord<FamilyRevision> result = familyRevisionService.save(familyRevisionSave);
        return ResponseEntity.ok().body(result.value());
    }

    @Override
    public ResponseEntity<GetById200Response> update(FamilyRevision familyRevision) {
        ResponseRecord<FamilyRevision> result = familyRevisionService.update(familyRevision);
        if (result == null || result.error() != null) {
            return new ResponseEntity<>(result != null ? result.error() : null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(result.value());
    }
}
