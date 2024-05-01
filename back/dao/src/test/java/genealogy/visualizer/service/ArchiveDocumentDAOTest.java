package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveDocumentDAOTest extends JpaAbstractTest {

    @Autowired
    private ArchiveDocumentDAO archiveDocumentDAO;

    @Autowired
    private ArchiveDocumentRepository archiveDocumentRepository;

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
        assertEquals(archiveDocument.getBunch(), result.getBunch());
        assertEquals(archiveDocument.getInstance(), result.getInstance());
        assertEquals(archiveDocument.getFund(), result.getFund());
        assertEquals(archiveDocument.getCatalog(), result.getCatalog());
        assertEquals(archiveDocument.getYear(), result.getYear());
        assertEquals(archiveDocument.getType(), result.getType());
        assertEquals(archiveDocument.getArchive().getName(), result.getArchive().getName());
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