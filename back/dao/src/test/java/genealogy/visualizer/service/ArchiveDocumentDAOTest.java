package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveDocumentDAOTest extends JpaAbstractTest {

    @Autowired
    private ArchiveDocumentDAO archiveDocumentDAO;

    @Autowired
    private ArchiveDocumentRepository archiveDocumentRepository;

    @Test
    void saveNewArchDocTest() {
        ArchiveDocument archDocForSave = getArchiveDocumentForSave();
        String catalog = "Catalog";
        archDocForSave.setCatalog(catalog);
        ArchiveDocument result = archiveDocumentDAO.saveOrFindIfExistDocument(archDocForSave);
        assertTrue(archiveDocumentRepository.existsById(result.getId()));
        assertNotNull(result.getId());
        assertNotEquals(result.getId(), archiveDocument.getId());
        assertNotNull(result.getArchive().getId());
        assertEquals(archiveDocument.getBunch(), result.getBunch());
        assertEquals(archiveDocument.getInstance(), result.getInstance());
        assertEquals(archiveDocument.getFund(), result.getFund());
        assertEquals(catalog, result.getCatalog());
        assertEquals(archiveDocument.getYear(), result.getYear());
        assertEquals(archiveDocument.getType(), result.getType());
        assertEquals(archiveDocument.getArchive().getName(), result.getArchive().getName());
        assertEquals(archiveDocument.getArchive().getId(), result.getArchive().getId());
    }

    @Test
    void saveExistArchDocTest() {
        ArchiveDocument archDocForSave = getArchiveDocumentForSave();
        ArchiveDocument result = archiveDocumentDAO.saveOrFindIfExistDocument(archDocForSave);
        assertNotNull(result.getId());
        assertNotNull(result.getArchive().getId());
        assertEquals(archiveDocument.getId(), result.getId());
        asserArchiveDocument(archiveDocument, result);
        assertEquals(archiveDocument.getArchive().getId(), result.getArchive().getId());
    }

    @Test
    void saveNewArchDocAndNewArchTest() {
        ArchiveDocument archDocForSave = getArchiveDocumentForSave();
        String catalog = "Catalog";
        archDocForSave.setCatalog(catalog);
        String archName = "Archive Name";
        archDocForSave.getArchive().setName(archName);
        ArchiveDocument result = archiveDocumentDAO.saveOrFindIfExistDocument(archDocForSave);

        assertTrue(archiveDocumentRepository.existsById(result.getId()));
        assertNotNull(result.getId());
        assertNotEquals(result.getId(), archiveDocument.getId());
        assertNotNull(result.getArchive().getId());
        assertNotEquals(archiveDocument.getArchive().getId(), result.getArchive().getId());
        assertEquals(archName, result.getArchive().getName());
        assertEquals(archiveDocument.getBunch(), result.getBunch());
        assertEquals(archiveDocument.getInstance(), result.getInstance());
        assertEquals(archiveDocument.getFund(), result.getFund());
        assertEquals(catalog, result.getCatalog());
        assertEquals(archiveDocument.getYear(), result.getYear());
        assertEquals(archiveDocument.getType(), result.getType());
    }

    @Test
    void updateNextRevisionDocumentTest() {
        ArchiveDocument archDocForSave = getArchiveDocumentForSave();
        String catalog = "Catalog";
        String archName = "Archive Name";
        archDocForSave.setCatalog(catalog);
        archDocForSave.getArchive().setName(archName);
        ArchiveDocument nextArchDocForSave = getArchiveDocumentForSave();
        nextArchDocForSave.setCatalog(catalog + "1");
        nextArchDocForSave.getArchive().setName(archName + "1");
        archDocForSave.setNextRevision(nextArchDocForSave);
        archiveDocumentDAO.updateNextRevisionDocument(archDocForSave);
        ArchiveDocument savedDocument = archiveDocumentRepository.findArchiveDocumentByConstraint(archDocForSave).orElseThrow();
        asserArchiveDocument(savedDocument, archDocForSave);
        asserArchiveDocument(savedDocument.getNextRevision(), nextArchDocForSave);
    }

    @Test
    void updateExistNextRevisionDocumentTest() {
        ArchiveDocument nextArchDocForSave = getArchiveDocumentForSave();
        String catalog = "Catalog";
        String archName = "Archive Name";
        nextArchDocForSave.setCatalog(catalog);
        nextArchDocForSave.getArchive().setName(archName);
        archiveDocument.setNextRevision(nextArchDocForSave);
        archiveDocumentDAO.updateNextRevisionDocument(archiveDocument);
        ArchiveDocument savedDocument = archiveDocumentRepository.findArchiveDocumentByConstraint(archiveDocument).orElseThrow();
        asserArchiveDocument(savedDocument, archiveDocument);
        asserArchiveDocument(savedDocument.getNextRevision(), nextArchDocForSave);
    }

    private void asserArchiveDocument(ArchiveDocument expected, ArchiveDocument actual) {
        assertEquals(expected.getBunch(), actual.getBunch());
        assertEquals(expected.getInstance(), actual.getInstance());
        assertEquals(expected.getFund(), actual.getFund());
        assertEquals(expected.getCatalog(), actual.getCatalog());
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getArchive().getName(), actual.getArchive().getName());
    }

    private ArchiveDocument getArchiveDocumentForSave() {
        return new ArchiveDocument(
                archiveDocument.getType(),
                archiveDocument.getYear(),
                archiveDocument.getFund(),
                archiveDocument.getCatalog(),
                archiveDocument.getInstance(),
                archiveDocument.getBunch(),
                new Archive(archiveDocument.getArchive().getName())
        );
    }
}
