package genealogy.visualizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import genealogy.visualizer.api.model.Age;
import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.DateInfo;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FullName;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.LocalityMapper;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.util.randomizer.DateInfoRandomizer;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static genealogy.visualizer.config.EasyRandomParamsBuilder.getGeneratorParams;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ChristeningRepository christeningRepository;

    @Autowired
    DeathRepository deathRepository;

    @Autowired
    MarriageRepository marriageRepository;

    @Autowired
    FamilyRevisionRepository familyRevisionRepository;

    @Autowired
    LocalityMapper localityMapper;

    @Autowired
    ObjectMapper objectMapper;

    ArchiveDocument archiveDocumentExisting;

    Locality localityExisting;

    Archive archiveExisting;
    static CycleAvoidingMappingContext cycleAvoidingMappingContext = new CycleAvoidingMappingContext();

    static EasyRandom generator;

    final Set<Long> localityIds = new HashSet<>();
    final Set<Long> archiveIds = new HashSet<>();
    final Set<Long> archiveDocumentIds = new HashSet<>();
    final Set<Long> personIds = new HashSet<>();
    final Set<Long> christeningIds = new HashSet<>();
    final Set<Long> deathIds = new HashSet<>();
    final Set<Long> marriageIds = new HashSet<>();
    final Set<Long> familyRevisionIds = new HashSet<>();

    static {
        EasyRandomParameters parameters = getGeneratorParams()
                .randomize(named("nextRevision").and(ofType(ArchiveDocument.class)), () -> null)
                .randomize(named("partner").and(ofType(FamilyMember.class)), () -> null)
                .randomize(named("person").and(ofType(EasyPerson.class)), () -> null)
                .randomize(named("name").and(ofType(String.class)), () -> new StringRandomizer().getRandomValue())
                .randomize(DateInfo.class, () -> new DateInfoRandomizer().getRandomValue())
                .randomize(Age.class, () -> new Age(new BigDecimalRangeRandomizer(Double.valueOf(0.0), Double.valueOf(99.9), Integer.valueOf(1)).getRandomValue(),
                        Age.TypeEnum.values()[new Random().nextInt(Age.TypeEnum.values().length)]));
        generator = new EasyRandom(parameters);
    }

    @BeforeEach
    void setUp() {
        localityExisting = localityRepository.save(generator.nextObject(genealogy.visualizer.entity.Locality.class));
        localityIds.add(localityExisting.getId());
        archiveExisting = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveRepository.saveAndFlush(archiveExisting);
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentEntity = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        archiveDocumentEntity.setArchive(archiveExisting);
        archiveDocumentExisting = archiveDocumentMapper.toDTO(archiveDocumentRepository.saveAndFlush(archiveDocumentEntity), cycleAvoidingMappingContext);
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

    String postRequest(String path, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        post(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String deleteRequest(String path) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        delete(path))
                .andExpectAll(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String getRequest(String path) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        get(path))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String putRequest(String path, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        put(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    static void assertArchiveDocument(ArchiveDocument archiveDocument1, ArchiveDocument archiveDocument2) {
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

    static void assertArchiveDocument(ArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
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

    static void assertFullName(FullName fullName1, genealogy.visualizer.entity.model.FullName fullName2) {
        assertNotNull(fullName1);
        assertNotNull(fullName2);
        assertEquals(fullName1.getName(), fullName2.getName());
        assertEquals(fullName1.getLastName(), fullName2.getLastName());
        assertEquals(fullName1.getSurname(), fullName2.getSurname());
        assertEquals(StringUtils.join(fullName1.getStatuses(), ", "), fullName2.getStatus());
    }

    static void assertFullName(FullName fullName1, FullName fullName2) {
        assertNotNull(fullName1);
        assertNotNull(fullName2);
        assertEquals(fullName1.getName(), fullName2.getName());
        assertEquals(fullName1.getLastName(), fullName2.getLastName());
        assertEquals(fullName1.getSurname(), fullName2.getSurname());
        assertEquals(fullName1.getStatuses().size(), fullName2.getStatuses().size());
        fullName1.getStatuses().
                forEach(anotherName -> assertTrue(fullName2.getStatuses().contains(anotherName)));

    }

    static void assertAge(Age age1, genealogy.visualizer.entity.model.Age age2) {
        assertNotNull(age1);
        assertNotNull(age2);
        assertEquals(0, age1.getAge().compareTo(age2.getAge()));
        assertEquals(age1.getType().getValue(), age2.getType().getName());
    }

    static void assertAge(Age age1, Age age2) {
        assertNotNull(age1);
        assertNotNull(age2);
        assertEquals(0, age1.getAge().compareTo(age2.getAge()));
        assertEquals(age1.getType().getValue(), age2.getType().getValue());
    }

    static void assertLocality(genealogy.visualizer.api.model.Locality locality1, genealogy.visualizer.api.model.Locality locality2) {
        assertNotNull(locality1);
        assertNotNull(locality2);
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType(), locality2.getType());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        locality1.getAnotherNames().
                forEach(anotherName -> assertTrue(locality2.getAnotherNames().contains(anotherName)));
    }
}
