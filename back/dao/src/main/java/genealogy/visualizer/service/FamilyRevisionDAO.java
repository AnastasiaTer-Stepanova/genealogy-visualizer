package genealogy.visualizer.service;

import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.FamilyRevision;

import java.util.List;

public interface FamilyRevisionDAO {

    void saveBatch(List<FamilyRevision> familyRevisions);

    void delete(Long id);

    FamilyRevision save(FamilyRevision familyRevision);

    FamilyRevision update(FamilyRevision familyRevision);

    FamilyRevision findFullInfoById(Long id);

    List<FamilyRevision> filter(FamilyRevisionFilterDTO filter);

}
