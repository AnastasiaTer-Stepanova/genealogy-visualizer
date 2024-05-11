package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.model.Witness;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarriageDAOTest extends JpaAbstractTest {

    @Autowired
    private MarriageDAO marriageDAO;

    @Test
    void saveNewMarriageTest() {
        List<Marriage> marriages = generateMarriage();
        marriages.forEach(marriage -> marriageDAO.save(marriage));

        marriages.forEach(marriage -> {
            Marriage marriagesFromDB = entityManager.find(Marriage.class, marriage.getId());
            entityManager.flush();
            assertEquals(marriage, marriagesFromDB);
        });
    }

    private List<Marriage> generateMarriage() {
        List<Marriage> marriages = generator.objects(Marriage.class, generator.nextInt(5, 15)).toList();
        for (Marriage marriage : marriages) {
            marriage.setPersons(Collections.emptyList());
            marriage.setId(null);
            if (generator.nextBoolean()) {
                marriage.setArchiveDocument(getArchiveDocumentForSave());
            }
            marriage.getArchiveDocument().setId(null);
            marriage.getArchiveDocument().getArchive().setId(null);
            int count = generator.nextInt(7);
            if (count == 0) {
                marriage.setWitnesses(Collections.emptyList());
            } else {
                List<Witness> witnesses = generator.objects(Witness.class, generator.nextInt(count)).toList();
                witnesses.forEach(witness -> {
                    if (generator.nextBoolean()) {
                        witness.setLocality(getLocalityForSave());
                    }
                    witness.getLocality().setId(null);
                });
                marriage.setWitnesses(witnesses);
            }
            if (generator.nextBoolean()) {
                marriage.setWifeLocality(getLocalityForSave());
            }
            if (generator.nextBoolean()) {
                marriage.setHusbandLocality(getLocalityForSave());
            }
            marriage.getHusbandLocality().setId(null);
            marriage.getWifeLocality().setId(null);
        }
        return marriages;
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