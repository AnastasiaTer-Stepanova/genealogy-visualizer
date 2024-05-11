package genealogy.visualizer;

import genealogy.visualizer.config.DaoConfig;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.model.FullName;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static genealogy.visualizer.config.EasyRandomParamsBuilder.getGeneratorParams;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DaoConfig.class)
public class JpaAbstractTest {

    @Autowired
    protected TestEntityManager entityManager;

    protected ArchiveDocument archiveDocument;
    protected Locality locality;

    protected static EasyRandom generator;

    static {
        generator = new EasyRandom(getGeneratorParams());
    }

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setId(null);
        archiveDocument.setArchive(archive);
        archiveDocument.getArchive().setId(null);
        archiveDocument.getArchive().setArchiveDocuments(Collections.emptyList());
        entityManager.persistAndFlush(archiveDocument.getArchive());
        entityManager.persistAndFlush(archiveDocument);
        locality = generator.nextObject(Locality.class);
        locality.setId(null);
        int count = generator.nextInt(3);
        if (count == 0) {
            locality.setAnotherNames(Collections.emptyList());
        } else {
            List<String> anotherNames = generator.objects(String.class, 2).toList();
            locality.setAnotherNames(anotherNames);
        }
        entityManager.persistAndFlush(locality);
    }

    protected void assertFullName(FullName fullName1, FullName fullName2) {
        assertEquals(fullName1.getName(), fullName2.getName());
        assertEquals(fullName1.getSurname(), fullName2.getSurname());
        assertEquals(fullName1.getLastName(), fullName2.getLastName());
        assertEquals(fullName1.getStatus(), fullName2.getStatus());
    }

    protected void assertLocality(Locality locality1, Locality locality2) {
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType(), locality2.getType());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        for (String anotherName : locality1.getAnotherNames()) {
            assertTrue(locality2.getAnotherNames().contains(anotherName));
        }
    }

}
