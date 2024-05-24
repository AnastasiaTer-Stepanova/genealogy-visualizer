package genealogy.visualizer.service;

import genealogy.visualizer.entity.FamilyRevision;

import java.util.List;

public interface FamilyRevisionDAO {

    void saveBatch(List<FamilyRevision> familyRevisions);

    void delete(Long id);

    FamilyRevision save(FamilyRevision familyRevision);

    FamilyRevision update(FamilyRevision familyRevision);

    FamilyRevision findFullInfoById(Long id);

    List<FamilyRevision> findFamilyRevisionsByNumberFamilyAndArchiveDocumentId(Long archiveDocumentId, short familyNumber, boolean isFindWithHavePerson);

    List<FamilyRevision> findFamilyRevisionsByNextFamilyRevisionNumberAndArchiveDocumentId(Long archiveDocumentId, short familyNumber, boolean isFindWithHavePerson);

    void updatePersonIdByPersonId(Long personId, Long newPersonId);

    void updatePersonIdById(Long id, Long newPersonId);

}
