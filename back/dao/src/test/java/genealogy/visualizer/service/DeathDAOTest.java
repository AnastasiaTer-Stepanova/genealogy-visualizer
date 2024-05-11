package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.Locality;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeathDAOTest extends JpaAbstractTest {

    @Autowired
    private DeathDAO deathDAO;

    @Test
    void saveNewDeathTest() {
        List<Death> deaths = generateDeath();
        deaths.forEach(death -> deathDAO.save(death));

        deaths.forEach(death -> {
            Death deathFromDB = entityManager.find(Death.class, death.getId());
            entityManager.flush();
            assertEquals(death, deathFromDB);
        });
    }

    private List<Death> generateDeath() {
        List<Death> deaths = generator.objects(Death.class, generator.nextInt(5, 15)).toList();
        for (Death death : deaths) {
            death.setPerson(null);
            death.setId(null);
            if (generator.nextBoolean()) {
                death.setArchiveDocument(getArchiveDocumentForSave());
            }
            death.getArchiveDocument().setId(null);
            death.getArchiveDocument().getArchive().setId(null);
            if (generator.nextBoolean()) {
                death.setLocality(getLocalityForSave());
            }
            death.getLocality().setId(null);
        }
        return deaths;
    }

    private Locality getLocalityForSave() {
        return new Locality(locality.getName(),
                locality.getType(),
                locality.getAddress(),
                locality.getAnotherNames());
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
