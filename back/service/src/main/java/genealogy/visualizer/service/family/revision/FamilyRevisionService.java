package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionFilter;
import genealogy.visualizer.api.model.FamilyRevisionResponse;
import genealogy.visualizer.api.model.FamilyRevisionSave;

public interface FamilyRevisionService {

    void delete(Long id);

    FamilyRevisionResponse getById(Long id);

    FamilyRevisionResponse save(FamilyRevisionSave familyRevisionSave);

    FamilyRevisionResponse update(FamilyRevision familyRevision);

    FamilyRevisionResponse getArchivesWithFamilyRevision(FamilyRevisionFilter familyRevisionFilter);
}
