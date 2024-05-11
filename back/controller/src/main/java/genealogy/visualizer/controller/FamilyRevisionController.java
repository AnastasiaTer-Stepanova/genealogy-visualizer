package genealogy.visualizer.controller;

import genealogy.visualizer.api.FamilyRevisionApi;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevisionList;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionErrorResponse;
import genealogy.visualizer.api.model.FamilyRevisionFilter;
import genealogy.visualizer.api.model.FamilyRevisionResponse;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
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
    public ResponseEntity<FamilyRevisionResponse> getById(Long id) {
        FamilyRevisionResponse result = familyRevisionService.getById(id);
        return switch (result) {
            case FamilyRevision f -> ResponseEntity.ok().body(f);
            case FamilyRevisionErrorResponse e -> getErrorResponse(e);
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @Override
    public ResponseEntity<FamilyRevisionResponse> save(FamilyRevisionSave familyRevisionSave) {
        FamilyRevisionResponse result = familyRevisionService.save(familyRevisionSave);
        return switch (result) {
            case FamilyRevision f -> ResponseEntity.ok().body(f);
            case FamilyRevisionErrorResponse e -> getErrorResponse(e);
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @Override
    public ResponseEntity<FamilyRevisionResponse> update(FamilyRevision familyRevision) {
        FamilyRevisionResponse result = familyRevisionService.update(familyRevision);
        return switch (result) {
            case FamilyRevision f -> ResponseEntity.ok().body(f);
            case FamilyRevisionErrorResponse e -> getErrorResponse(e);
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @Override
    public ResponseEntity<FamilyRevisionResponse> getFamilyRevisionByNum(FamilyRevisionFilter familyRevisionFilter) {
        FamilyRevisionResponse result = familyRevisionService.getArchivesWithFamilyRevision(familyRevisionFilter);
        return switch (result) {
            case ArchiveWithFamilyRevisionList f -> ResponseEntity.ok().body(f);
            case FamilyRevisionErrorResponse e -> getErrorResponse(e);
            default -> ResponseEntity.internalServerError().build();
        };
    }

    private ResponseEntity<FamilyRevisionResponse> getErrorResponse(FamilyRevisionErrorResponse result) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if (result == null || result.getCode() == null) {
            return new ResponseEntity<>(null, httpStatus);
        }
        httpStatus = HttpStatus.valueOf(result.getCode());
        return new ResponseEntity<>(result, httpStatus);
    }

}
