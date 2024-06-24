package genealogy.visualizer.service;

import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.FamilyRevision;

import java.util.List;

public interface FamilyRevisionDAO extends CrudDAO<FamilyRevision>, FilterDAO<FamilyRevision, FamilyRevisionFilterDTO> {

    void saveBatch(List<FamilyRevision> familyRevisions);
}
