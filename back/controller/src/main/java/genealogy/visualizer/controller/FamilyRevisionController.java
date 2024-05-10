package genealogy.visualizer.controller;

import genealogy.visualizer.api.FamilyRevisionApi;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevision;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevisionList;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionFilter;
import genealogy.visualizer.api.model.FamilyRevisionResponse;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import genealogy.visualizer.service.util.record.ResponseRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        ResponseRecord<FamilyRevision> result = familyRevisionService.getById(id);
        if (result == null || result.error() != null) {
            return new ResponseEntity<>(result != null ? result.error() : null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result.value());
    }

    @Override
    public ResponseEntity<FamilyRevisionResponse> save(FamilyRevisionSave familyRevisionSave) {
        ResponseRecord<FamilyRevision> result = familyRevisionService.save(familyRevisionSave);
        if (result == null || result.error() != null) {
            return getErrorResponse(result);
        }
        return ResponseEntity.ok(result.value());
    }

    @Override
    public ResponseEntity<FamilyRevisionResponse> update(FamilyRevision familyRevision) {
        ResponseRecord<FamilyRevision> result = familyRevisionService.update(familyRevision);
        if (result == null || result.error() != null) {
            return getErrorResponse(result);
        }
        return ResponseEntity.ok(result.value());
    }

    @Override
    public ResponseEntity<FamilyRevisionResponse> getFamilyRevisionByNum(FamilyRevisionFilter familyRevisionFilter) {
        ResponseRecord<List<ArchiveWithFamilyRevision>> result = familyRevisionService.getArchivesWithFamilyRevision(familyRevisionFilter);
        if (result == null || result.error() != null) {
            return getErrorResponse(result);
        }
        return ResponseEntity.ok(new ArchiveWithFamilyRevisionList().data(result.value()));
    }

    private ResponseEntity<FamilyRevisionResponse> getErrorResponse(ResponseRecord<?> result) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if (result == null || result.error() == null) {
            return new ResponseEntity<>(null, httpStatus);
        }
        httpStatus = HttpStatus.valueOf(result.error().getCode());
        return new ResponseEntity<>(result.error(), httpStatus);
    }

}
