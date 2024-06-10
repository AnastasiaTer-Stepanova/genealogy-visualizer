package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.EasyArchiveDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArchiveDocumentControllerTest extends IntegrationTest {


    protected static void assertArchiveDocument(ArchiveDocument archiveDocument1, ArchiveDocument archiveDocument2) {
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType(), archiveDocument2.getType());
        assertEquals(archiveDocument1.getYear(), archiveDocument2.getYear());
        assertEquals(archiveDocument1.getArchive().getAddress(), archiveDocument2.getArchive().getAddress());
        assertEquals(archiveDocument1.getArchive().getComment(), archiveDocument2.getArchive().getComment());
        assertEquals(archiveDocument1.getArchive().getName(), archiveDocument2.getArchive().getName());
    }

    protected static void assertArchiveDocument(EasyArchiveDocument archiveDocument1, EasyArchiveDocument archiveDocument2) {
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType(), archiveDocument2.getType());
        assertEquals(archiveDocument1.getYear(), archiveDocument2.getYear());
    }

    protected static void assertArchiveDocument(EasyArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType().getValue(), archiveDocument2.getType().getTitle());
        assertEquals(archiveDocument1.getYear(), (int) archiveDocument2.getYear());
    }

    protected static void assertArchiveDocument(ArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType().getValue(), archiveDocument2.getType().getTitle());
        assertEquals(archiveDocument1.getYear(), (int) archiveDocument2.getYear());
        assertEquals(archiveDocument1.getArchive().getAddress(), archiveDocument2.getArchive().getAddress());
        assertEquals(archiveDocument1.getArchive().getComment(), archiveDocument2.getArchive().getComment());
        assertEquals(archiveDocument1.getArchive().getName(), archiveDocument2.getArchive().getName());
    }
}