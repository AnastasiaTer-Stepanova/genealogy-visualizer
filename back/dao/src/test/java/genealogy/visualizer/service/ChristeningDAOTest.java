package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.model.GodParent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChristeningDAOTest extends JpaAbstractTest {

    @Autowired
    private ChristeningDAO christeningDAO;

    private ArchiveDocument archiveDocument;
    private Locality locality;

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setId(null);
        archiveDocument.setFamilyRevisions(Collections.emptyList());
        archiveDocument.setChristenings(Collections.emptyList());
        archiveDocument.setArchive(archive);
        archiveDocument.getArchive().setId(null);
        archiveDocument.getArchive().setArchiveDocuments(Collections.emptyList());
        entityManager.persistAndFlush(archiveDocument.getArchive());
        entityManager.persistAndFlush(archiveDocument);
        locality = generator.nextObject(Locality.class);
        locality.setId(null);
        locality.setChristenings(Collections.emptyList());
        int count = generator.nextInt(3);
        if (count == 0) {
            locality.setAnotherNames(Collections.emptyList());
        } else {
            List<String> anotherNames = generator.objects(String.class, 2).toList();
            locality.setAnotherNames(anotherNames);
        }
        entityManager.persistAndFlush(locality);
    }

    @Test
    void saveNewArchDocTest() {
        List<Christening> christenings = generateChristening();
        christenings.forEach(christening -> christeningDAO.save(christening));

        christenings.forEach(christening -> {
            Christening christeningsFromDB = entityManager.find(Christening.class, christening.getId());
            entityManager.flush();
            assertEquals(christening, christeningsFromDB);
        });
    }

    private List<Christening> generateChristening() {
        List<Christening> christenings = generator.objects(Christening.class, generator.nextInt(5, 15)).toList();
        for (Christening christening : christenings) {
            christening.setPerson(null);
            christening.setId(null);
            if (generator.nextBoolean()) {
                christening.setArchiveDocument(getArchiveDocumentForSave());
            }
            christening.getArchiveDocument().setId(null);
            christening.getArchiveDocument().getArchive().setId(null);
            int count = generator.nextInt(3);
            if (count == 0) {
                christening.setGodParents(null);
            } else {
                List<GodParent> godParents = generator.objects(GodParent.class, generator.nextInt(count)).toList();
                godParents.forEach(godParent -> {
                    if (generator.nextBoolean()) {
                        godParent.setLocality(getLocalityForSave());
                    }
                    godParent.getLocality().setId(null);
                });
                christening.setGodParents(godParents);
            }
            if (generator.nextBoolean()) {
                christening.setLocality(getLocalityForSave());
            }
            christening.getLocality().setId(null);
        }
        return christenings;
    }

    private Locality getLocalityForSave() {
        return new Locality(
                null,
                locality.getName(),
                locality.getType(),
                locality.getAddress(),
                locality.getAnotherNames(),
                Collections.emptyList());
    }

    private ArchiveDocument getArchiveDocumentForSave() {
        return new ArchiveDocument(
                archiveDocument.getType(),
                archiveDocument.getYear(),
                archiveDocument.getFund(),
                archiveDocument.getCatalog(),
                archiveDocument.getInstance(),
                archiveDocument.getBunch(),
                new Archive(archiveDocument.getArchive().getName()));
    }

}