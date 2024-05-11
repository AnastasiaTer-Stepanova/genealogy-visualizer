package genealogy.visualizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import genealogy.visualizer.api.model.Age;
import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FullName;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.LocalityRepository;
import org.apache.commons.lang3.StringUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.BigDecimalRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static genealogy.visualizer.config.EasyRandomParamsBuilder.getGeneratorParams;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ArchiveDocumentRepository archiveDocumentRepository;

    @Autowired
    ArchiveRepository archiveRepository;

    @Autowired
    LocalityRepository localityRepository;

    @Autowired
    ArchiveDocumentMapper archiveDocumentMapper;

    ArchiveDocument archiveDocumentExisting;

    genealogy.visualizer.entity.Locality localityExisting;

    static ObjectMapper objectMapper = new ObjectMapper();

    static EasyRandom generator;

    final Set<Long> localityIds = new HashSet<>();
    final Set<Long> archiveIds = new HashSet<>();
    final Set<Long> archiveDocumentIds = new HashSet<>();

    static {
        EasyRandomParameters parameters = getGeneratorParams()
                .randomize(named("partner").and(ofType(FamilyRevision.class)), () -> null)
                .randomize(named("person").and(ofType(EasyPerson.class)), () -> null)
                .randomize(named("name").and(ofType(String.class)), () -> new StringRandomizer().getRandomValue())
                .randomize(Age.class, () -> new Age(new BigDecimalRangeRandomizer(Double.valueOf(0.0), Double.valueOf(99.9), Integer.valueOf(1)).getRandomValue(),
                        Age.TypeEnum.values()[new Random().nextInt(Age.TypeEnum.values().length)]));
        generator = new EasyRandom(parameters);
    }

    @BeforeEach
    void setUp() {
        genealogy.visualizer.entity.Locality localityExisting = localityRepository.save(generator.nextObject(genealogy.visualizer.entity.Locality.class));
        localityIds.add(localityExisting.getId());
        genealogy.visualizer.entity.Archive archiveEntity = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveRepository.saveAndFlush(archiveEntity);
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentEntity = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        archiveDocumentEntity.setArchive(archiveEntity);
        archiveDocumentExisting = archiveDocumentMapper.toDTO(archiveDocumentRepository.saveAndFlush(archiveDocumentEntity));
        archiveIds.add(archiveDocumentExisting.getArchive().getId());
        archiveDocumentIds.add(archiveDocumentExisting.getId());
        System.out.println("----------------------Start test------------------------");
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        archiveDocumentRepository.deleteAllById(archiveDocumentIds);
        archiveRepository.deleteAllById(archiveIds);
        localityRepository.deleteAllById(localityIds);
    }

    void assertArchiveDocument(ArchiveDocument archiveDocument1, ArchiveDocument archiveDocument2) {
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

    void assertArchiveDocument(ArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
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

    void assertFullName(FullName fullName1, genealogy.visualizer.entity.model.FullName fullName2) {
        assertNotNull(fullName1);
        assertNotNull(fullName2);
        assertEquals(fullName1.getName(), fullName2.getName());
        assertEquals(fullName1.getLastName(), fullName2.getLastName());
        assertEquals(fullName1.getSurname(), fullName2.getSurname());
        assertEquals(StringUtils.join(fullName1.getStatuses(), ", "), fullName2.getStatus());
    }

    void assertAge(Age age1, genealogy.visualizer.entity.model.Age age2) {
        assertNotNull(age1);
        assertNotNull(age2);
        assertEquals(0, age1.getAge().compareTo(age2.getAge()));
        assertEquals(age1.getType().getValue(), age2.getType().getName());
    }

    void assertAge(Age age1, Age age2) {
        assertNotNull(age1);
        assertNotNull(age2);
        assertEquals(0, age1.getAge().compareTo(age2.getAge()));
        assertEquals(age1.getType().getValue(), age2.getType().getValue());
    }
}
