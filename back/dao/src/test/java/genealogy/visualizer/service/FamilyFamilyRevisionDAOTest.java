package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.model.AnotherNameInRevision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FamilyFamilyRevisionDAOTest extends JpaAbstractTest {

    @Autowired
    private FamilyRevisionDAO familyFamilyRevisionDAO;

    private ArchiveDocument archiveDocument;

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setId(null);
        archiveDocument.setFamilyRevisions(Collections.emptyList());
        archiveDocument.setArchive(archive);
        archiveDocument.getArchive().setId(null);
        archiveDocument.getArchive().setArchiveDocuments(Collections.emptyList());
        entityManager.persistAndFlush(archiveDocument.getArchive());
        entityManager.persistAndFlush(archiveDocument);
    }

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
                List<AnotherNameInRevision> anotherNamesInRevision = generator.objects(AnotherNameInRevision.class, generator.nextInt(1, 3)).toList();
                familyRevision.setAnotherNames(anotherNamesInRevision);
            }
        }
        familyRevisions.getFirst().setPartner(familyRevisions.getLast());
        familyRevisions.getLast().setPartner(familyRevisions.getFirst());
        return familyRevisions;
    }


}