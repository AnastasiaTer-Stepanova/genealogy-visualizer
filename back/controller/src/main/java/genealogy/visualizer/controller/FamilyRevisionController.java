package genealogy.visualizer.controller;

import genealogy.visualizer.api.FamilyRevisionApi;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
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
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<FamilyMember> getById(Long id) {
        return ResponseEntity.ok(familyRevisionService.getById(id));
    }

    @Override
    public ResponseEntity<FamilyMember> save(FamilyMember familyMember) {
        return ResponseEntity.ok(familyRevisionService.save(familyMember));
    }

    @Override
    public ResponseEntity<FamilyMember> update(FamilyMember familyMember) {
        return ResponseEntity.ok(familyRevisionService.update(familyMember));
    }

    @Override
    public ResponseEntity<List<FamilyMemberFullInfo>> getFamilyRevisionByNum(FamilyMemberFilter familyMemberFilter) {
        return ResponseEntity.ok(familyRevisionService.getFamilyMemberFullInfoList(familyMemberFilter));
    }
}
