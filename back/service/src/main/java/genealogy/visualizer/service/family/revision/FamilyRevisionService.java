package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.service.util.record.ResponseRecord;

public interface FamilyRevisionService {

    void delete(Long id);

    ResponseRecord<FamilyRevision> getById(Long id);

    ResponseRecord<FamilyRevision> save(FamilyRevisionSave familyRevisionSave);

    ResponseRecord<FamilyRevision> update(FamilyRevision familyRevision);
}
