package genealogy.visualizer.service;

import genealogy.visualizer.entity.FamilyRevision;

import java.util.List;

public interface FamilyRevisionDAO {

    void saveBatch(List<FamilyRevision> familyRevisions);
}
