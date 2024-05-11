package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.FamilyRevision;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FamilyFamilyRevisionDAOTest extends JpaAbstractTest {

    @Autowired
    private FamilyRevisionDAO familyFamilyRevisionDAO;

    @Test
    void saveBatchTest() {
        List<FamilyRevision> familyRevisions = generateFamilyRevisions();
        familyFamilyRevisionDAO.saveBatch(familyRevisions);
        for (FamilyRevision revision : familyRevisions) {
            assertNotNull(revision.getId());
            FamilyRevision revisionFromDB = entityManager.find(FamilyRevision.class, revision.getId());
            entityManager.flush();
            assertEquals(revisionFromDB.getArchiveDocument(), archiveDocument);
            if (!revision.getAnotherNames().isEmpty()) {
                assertNotNull(revisionFromDB.getAnotherNames());
            }
        }
    }

    private List<FamilyRevision> generateFamilyRevisions() {
        List<FamilyRevision> familyRevisions = generator.objects(FamilyRevision.class, generator.nextInt(5, 15)).toList();
        for (FamilyRevision familyRevision : familyRevisions) {
            familyRevision.setId(null);
            familyRevision.setPerson(null);
            familyRevision.setPartner(null);
            familyRevision.setArchiveDocument(archiveDocument);
            if (generator.nextBoolean()) {
                List<String> anotherNamesInRevision = generator.objects(String.class, generator.nextInt(1, 3)).toList();
                familyRevision.setAnotherNames(anotherNamesInRevision);
            }
        }
        familyRevisions.getFirst().setPartner(familyRevisions.getLast());
        familyRevisions.getLast().setPartner(familyRevisions.getFirst());
        return familyRevisions;
    }


}