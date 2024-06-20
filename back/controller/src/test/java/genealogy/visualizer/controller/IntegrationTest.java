package genealogy.visualizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import genealogy.visualizer.api.model.Age;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.DateInfo;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FullName;
import genealogy.visualizer.api.model.FullNameFilter;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.api.model.LocalityType;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.GodParent;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.entity.User;
import genealogy.visualizer.entity.Witness;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.entity.enums.Sex;
import genealogy.visualizer.mapper.EasyArchiveDocumentMapper;
import genealogy.visualizer.mapper.EasyChristeningMapper;
import genealogy.visualizer.mapper.EasyDeathMapper;
import genealogy.visualizer.mapper.EasyFamilyRevisionMapper;
import genealogy.visualizer.mapper.EasyLocalityMapper;
import genealogy.visualizer.mapper.EasyMarriageMapper;
import genealogy.visualizer.mapper.EasyPersonMapper;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.GodParentRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.repository.UserRepository;
import genealogy.visualizer.repository.WitnessRepository;
import genealogy.visualizer.service.ArchiveDAO;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.DeathDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.LocalityDAO;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.PersonDAO;
import genealogy.visualizer.service.authorization.JwtService;
import genealogy.visualizer.util.randomizer.DateInfoRandomizer;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.BigDecimalRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MimeType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static genealogy.visualizer.config.EasyRandomParamsBuilder.getGeneratorParams;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(parallel = true)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static final String AUTHORIZATION_PATH = "/authorization";
    private static final String START_TEST = "----------------------Start test------------------------";
    private static final String END_TEST = "----------------------End test------------------------";
    private static final String UNAUTHORIZED_MESSAGE = "Доступ запрещен. Пожалуйста, авторизуйтесь.";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String APPLICATION_JSON_UTF8 = new MimeType("application", "json", StandardCharsets.UTF_8).toString();

    static StringRandomizer stringRandomizer = new StringRandomizer(3);
    static List<genealogy.visualizer.entity.Archive> existingArchives = new ArrayList<>();
    static List<ArchiveDocument> existingArchiveDocuments = new ArrayList<>();
    static List<Death> existingDeaths = new ArrayList<>();
    static List<Locality> existingLocalities = new ArrayList<>();
    static List<Christening> existingChristenings = new ArrayList<>();
    static List<Marriage> existingMarriages = new ArrayList<>();
    static List<FamilyRevision> existingFamilyRevisions = new ArrayList<>();
    static List<Person> existingPersons = new ArrayList<>();

    static ArchiveFilter archiveFilter = new ArchiveFilter().abbreviation("ГАРО").name("гос архив");
    static MarriageFilter marriageFilter = new MarriageFilter()
            .husbandFullName(new FullNameFilter().name("Иван").surname("Иванович").lastName("Иванов"))
            .wifeFullName(new FullNameFilter().name("Елена").surname("Петровна").lastName("Сидорова"))
            .marriageYear(1850);
    static ArchiveDocumentFilter archiveDocumentFilter = new ArchiveDocumentFilter()
            .name("Метр книга").abbreviation("МК1").type(genealogy.visualizer.api.model.ArchiveDocumentType.PR).year(1850);
    static ChristeningFilter christeningFilter = new ChristeningFilter()
            .name("Иван").sex(genealogy.visualizer.api.model.Sex.MALE).christeningYear(1850);
    static DeathFilter deathFilter = new DeathFilter()
            .fullName(new FullNameFilter().name("Иван").surname("Иванович").lastName("Иванов"))
            .deathYear(1850);
    static FamilyMemberFilter familyMemberFilter = new FamilyMemberFilter()
            .fullName(new FullNameFilter().name("Иван").surname("Иванович").lastName("Иванов"))
            .sex(genealogy.visualizer.api.model.Sex.MALE)
            .familyRevisionNumber(50);
    static LocalityFilter localityFilter = new LocalityFilter()
            .name("вое")
            .type(LocalityType.TOWN)
            .address("Россия");
    static PersonFilter personFilter = new PersonFilter()
            .fullName(new FullNameFilter().name("Иван").surname("Иванович").lastName("Иванов"))
            .sex(genealogy.visualizer.api.model.Sex.MALE)
            .birthYear(1820)
            .deathYear(1870);

    static EasyRandom generator;
    static User userExisting;
    static String userExistingPassword;

    @BeforeAll
    static void createTestData(@Autowired PasswordEncoder passwordEncoder,
                               @Autowired UserRepository userRepository,
                               @Autowired ArchiveDAO archiveDAO,
                               @Autowired ArchiveDocumentDAO archiveDocumentDAO,
                               @Autowired LocalityDAO localityDAO,
                               @Autowired DeathDAO deathDAO,
                               @Autowired ChristeningDAO christeningDAO,
                               @Autowired MarriageDAO marriageDAO,
                               @Autowired FamilyRevisionDAO familyRevisionDAO,
                               @Autowired PersonDAO personDAO) {
        postgresContainer.start();
        EasyRandomParameters parameters = getGeneratorParams()
                .randomize(named("id").and(ofType(Long.class)), () -> null)
                .randomize(named("partner").and(ofType(FamilyMember.class)), () -> null)
                .randomize(named("person").and(ofType(EasyPerson.class)), () -> null)
                .randomize(named("name").and(ofType(String.class)), () -> new StringRandomizer().getRandomValue())
                .randomize(DateInfo.class, () -> new DateInfoRandomizer().getRandomValue())
                .randomize(Age.class, () -> new Age(new BigDecimalRangeRandomizer(Double.valueOf(0.0), Double.valueOf(99.9), Integer.valueOf(1)).getRandomValue(),
                        Age.TypeEnum.values()[new Random().nextInt(Age.TypeEnum.values().length)]));
        generator = new EasyRandom(parameters);
        generateRandomExistUser(passwordEncoder, userRepository);
        existingArchives = generateRandomExistArchives(archiveDAO);
        existingArchiveDocuments = generateRandomExistArchiveDocuments(archiveDocumentDAO);
        existingLocalities = generateRandomExistLocalities(localityDAO);
        existingDeaths = generateRandomExistDeaths(deathDAO);
        existingChristenings = generateRandomExistChristenings(christeningDAO);
        existingMarriages = generateRandomExistMarriages(marriageDAO);
        existingFamilyRevisions = generateRandomExistFamilyRevisions(familyRevisionDAO);
        existingPersons = generateRandomExistPersons(personDAO);
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtService jwtService;
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    EasyFamilyRevisionMapper easyFamilyRevisionMapper;
    @Autowired
    EasyLocalityMapper easyLocalityMapper;
    @Autowired
    EasyMarriageMapper easyMarriageMapper;
    @Autowired
    EasyPersonMapper easyPersonMapper;
    @Autowired
    EasyDeathMapper easyDeathMapper;
    @Autowired
    EasyChristeningMapper easyChristeningMapper;
    @Autowired
    EasyArchiveDocumentMapper easyArchiveDocumentMapper;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ArchiveRepository archiveRepository;
    @Autowired
    ArchiveDocumentRepository archiveDocumentRepository;
    @Autowired
    LocalityRepository localityRepository;
    @Autowired
    DeathRepository deathRepository;
    @Autowired
    ChristeningRepository christeningRepository;
    @Autowired
    MarriageRepository marriageRepository;
    @Autowired
    FamilyRevisionRepository familyRevisionRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GodParentRepository godParentRepository;
    @Autowired
    WitnessRepository witnessRepository;

    @Autowired
    ArchiveDAO archiveDAO;
    @Autowired
    ArchiveDocumentDAO archiveDocumentDAO;
    @Autowired
    LocalityDAO localityDAO;
    @Autowired
    DeathDAO deathDAO;
    @Autowired
    ChristeningDAO christeningDAO;
    @Autowired
    MarriageDAO marriageDAO;
    @Autowired
    FamilyRevisionDAO familyRevisionDAO;
    @Autowired
    PersonDAO personDAO;

    @BeforeEach
    void setUp() {
        existingArchives = existingArchives.stream().map(e -> archiveDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingArchiveDocuments = existingArchiveDocuments.stream().map(e -> archiveDocumentDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingLocalities = existingLocalities.stream().map(e -> localityDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingDeaths = existingDeaths.stream().map(e -> deathDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingChristenings = existingChristenings.stream().map(e -> christeningDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingMarriages = existingMarriages.stream().map(e -> marriageDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingFamilyRevisions = existingFamilyRevisions.stream().map(e -> familyRevisionDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        existingPersons = existingPersons.stream().map(e -> personDAO.findFullInfoById(e.getId())).collect(Collectors.toList());
        System.out.println(START_TEST);
    }

    @AfterEach
    void tearDown() {
        System.out.println(END_TEST);
    }

    @AfterAll
    static void deleteTestData(@Autowired UserRepository userRepository,
                               @Autowired ArchiveRepository archiveRepository,
                               @Autowired ArchiveDocumentRepository archiveDocumentRepository,
                               @Autowired LocalityRepository localityRepository,
                               @Autowired DeathRepository deathRepository,
                               @Autowired ChristeningRepository christeningRepository,
                               @Autowired MarriageRepository marriageRepository,
                               @Autowired FamilyRevisionRepository familyRevisionRepository,
                               @Autowired PersonRepository personRepository,
                               @Autowired GodParentRepository godParentRepository,
                               @Autowired WitnessRepository witnessRepository) {
        userRepository.deleteAllInBatch();
        godParentRepository.deleteAllInBatch();
        christeningRepository.deleteAllInBatch();
        familyRevisionRepository.deleteAllInBatch();
        witnessRepository.deleteAllInBatch();
        marriageRepository.deleteAllInBatch();
        deathRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
        archiveDocumentRepository.deleteAllInBatch();
        archiveRepository.deleteAllInBatch();
        localityRepository.deleteAllInBatch();
        postgresContainer.close();
    }

    void postUnauthorizedRequest(String path, String requestJson) throws Exception {
        unauthorizedRequest(post(path), requestJson);
    }

    void putUnauthorizedRequest(String path, String requestJson) throws Exception {
        unauthorizedRequest(put(path), requestJson);
    }

    void deleteUnauthorizedRequest(String path) throws Exception {
        unauthorizedRequest(delete(path), null);
    }

    void unauthorizedRequest(MockHttpServletRequestBuilder requestBuilder, String requestJson) throws Exception {
        String responseJson = performRequest(requestBuilder, requestJson, status().isUnauthorized(), APPLICATION_JSON_UTF8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getMessage(), UNAUTHORIZED_MESSAGE);
        assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
    }

    String postRequest(String path, String requestJson) throws Exception {
        return authorizedRequest(post(path), requestJson, status().isOk());
    }

    String deleteRequest(String path) throws Exception {
        return performRequest(delete(path).header(AUTHORIZATION_HEADER, BEARER_PREFIX + authorization()), null, status().isNoContent(), null);

    }

    String getRequest(String path) throws Exception {
        return getRequest(path, null);
    }

    String getRequest(String path, String requestJson) throws Exception {
        return performRequest(get(path), requestJson, status().isOk(), MediaType.APPLICATION_JSON.toString());
    }

    void getNotFoundRequest(String path) throws Exception {
        getNotFoundRequest(path, null);
    }

    void getNotFoundRequest(String path, String requestJson) throws Exception {
        performRequest(get(path), requestJson, status().isNotFound(), MediaType.APPLICATION_JSON.toString());
    }

    String putRequest(String path, String requestJson) throws Exception {
        return putRequest(path, requestJson, status().isOk());
    }

    String putRequest(String path, String requestJson, ResultMatcher statusResultMatchers) throws Exception {
        return authorizedRequest(put(path), requestJson, statusResultMatchers);
    }

    String authorization() throws Exception {
        genealogy.visualizer.api.model.User user = new genealogy.visualizer.api.model.User(userExisting.getLogin(), userExistingPassword);
        String responseJson = performRequest(post(AUTHORIZATION_PATH), objectMapper.writeValueAsString(user), status().isOk(), MediaType.APPLICATION_JSON.toString());
        assertNotNull(responseJson);
        assertEquals(jwtService.getLogin(responseJson), userExisting.getLogin());
        assertTrue(jwtService.isTokenValid(responseJson, user));
        return responseJson;
    }

    private String authorizedRequest(MockHttpServletRequestBuilder requestBuilder, String requestJson, ResultMatcher statusMatcher) throws Exception {
        return performRequest(requestBuilder.header(AUTHORIZATION_HEADER, BEARER_PREFIX + authorization()), requestJson, statusMatcher, MediaType.APPLICATION_JSON.toString());
    }

    String performRequest(MockHttpServletRequestBuilder requestBuilder, String requestJson, ResultMatcher statusMatcher, String contentType) throws Exception {
        String requestType = requestBuilder.buildRequest(null).getMethod();
        String requestPath = requestBuilder.buildRequest(null).getRequestURI();
        System.out.printf("------------------------ Start request: %s %s ----------------------\n", requestType, requestPath);
        ResultActions resultActions = mockMvc.perform(
                        requestJson == null ?
                                requestBuilder :
                                requestBuilder
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestJson))
                .andExpect(statusMatcher);
        if (contentType != null) {
            resultActions.andExpect(content().contentType(contentType));
        }
        String responseJson = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.printf("------------------------ End request: %s %s ------------------------\n", requestType, requestPath);
        return responseJson;
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

    static void assertAge(Age age1, Age age2) {
        assertNotNull(age1);
        assertNotNull(age2);
        assertEquals(0, age1.getAge().compareTo(age2.getAge()));
        assertEquals(age1.getType().getValue(), age2.getType().getValue());
    }

    static void assertDateInfo(DateInfo dateInfo1, DateInfo dateInfo2) {
        assertNotNull(dateInfo1);
        assertNotNull(dateInfo2);
        assertEquals(dateInfo1.getDate(), dateInfo2.getDate());
        assertEquals(dateInfo1.getDateRangeType(), dateInfo2.getDateRangeType());
    }

    static void assertAnotherNames(List<String> anotherNames1, List<String> anotherNames2) {
        if (anotherNames1 == null || anotherNames2 == null) {
            assertNull(anotherNames1);
            assertNull(anotherNames2);
            return;
        }
        assertEquals(anotherNames1.size(), anotherNames2.size());
        anotherNames1.forEach(an -> assertTrue(anotherNames2.contains(an)));
    }

    static FullName toFullName(genealogy.visualizer.entity.model.FullName fullName) {
        return new FullName()
                .name(fullName.getName())
                .lastName(fullName.getLastName())
                .surname(fullName.getSurname())
                .statuses(fullName.getStatus() != null ? List.of(fullName.getStatus().split(", ")) : Collections.emptyList());
    }

    static DateInfo toDateInfo(genealogy.visualizer.entity.model.DateInfo dateInfo) {
        return new DateInfo()
                .date(dateInfo.getDate())
                .dateRangeType(DateInfo.DateRangeTypeEnum.valueOf(dateInfo.getDateRangeType().name()));
    }

    static Age toAge(genealogy.visualizer.entity.model.Age age) {
        return new Age()
                .age(age.getAge())
                .type(Age.TypeEnum.fromValue(age.getType().getName()));
    }

    static boolean containsIgnoreCase(String str1, String str2) {
        return str1 != null && str2 != null && str1.toLowerCase().contains(str2.toLowerCase());
    }

    private static List<Archive> generateRandomExistArchives(ArchiveDAO archiveDAO) {
        List<Archive> archivesForSave = generator.objects(Archive.class, generator.nextInt(2, 5)).toList();
        archivesForSave.forEach(a -> {
            a.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? archiveFilter.getName() : archiveFilter.getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            a.setAbbreviation(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? archiveFilter.getAbbreviation() : archiveFilter.getAbbreviation().toLowerCase()) +
                    stringRandomizer.getRandomValue());
        });
        return archivesForSave.stream().map(archiveDAO::save).collect(Collectors.toList());
    }

    private static List<ArchiveDocument> generateRandomExistArchiveDocuments(ArchiveDocumentDAO archiveDocumentDAO) {
        List<ArchiveDocument> archiveDocumentsForSave = generator.objects(ArchiveDocument.class, generator.nextInt(3, 5)).toList();
        ArchiveDocument archiveDocumentNextRevisionForSave = generator.nextObject(ArchiveDocument.class);
        archiveDocumentNextRevisionForSave.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
        ArchiveDocument archiveDocumentNextRevision = archiveDocumentDAO.save(archiveDocumentNextRevisionForSave);
        archiveDocumentsForSave.forEach(archiveDocument -> {
            archiveDocument.setNextRevision(archiveDocumentNextRevision);
            archiveDocument.setPreviousRevisions(generator.objects(ArchiveDocument.class, generator.nextInt(2)).toList());
            archiveDocument.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
            archiveDocument.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? archiveDocumentFilter.getName() : archiveDocumentFilter.getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            archiveDocument.setAbbreviation(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? archiveDocumentFilter.getAbbreviation() : archiveDocumentFilter.getAbbreviation().toLowerCase()) +
                    stringRandomizer.getRandomValue());
            archiveDocument.setType(ArchiveDocumentType.PR);
            archiveDocument.setYear(archiveDocumentFilter.getYear().shortValue());
        });
        List<ArchiveDocument> archiveDocumentsSaved = archiveDocumentsForSave.stream().map(archiveDocumentDAO::save).collect(Collectors.toList());
        archiveDocumentsForSave.forEach(ad -> {
            if (ad.getPreviousRevisions() != null && !ad.getPreviousRevisions().isEmpty()) {
                ad.getPreviousRevisions().forEach(archDoc -> {
                    archDoc.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
                    archDoc.setName(stringRandomizer.getRandomValue() +
                            (generator.nextBoolean() ? archiveDocumentFilter.getName() : archiveDocumentFilter.getName().toUpperCase()) +
                            stringRandomizer.getRandomValue());
                    archDoc.setAbbreviation(stringRandomizer.getRandomValue() +
                            (generator.nextBoolean() ? archiveDocumentFilter.getAbbreviation() : archiveDocumentFilter.getAbbreviation().toLowerCase()) +
                            stringRandomizer.getRandomValue());
                    archDoc.setType(ArchiveDocumentType.PR);
                    archDoc.setYear(archiveDocumentFilter.getYear().shortValue());
                    archiveDocumentDAO.update(archDoc);
                });
            }
        });
        return archiveDocumentsSaved;
    }

    private static List<Locality> generateRandomExistLocalities(LocalityDAO localityDAO) {
        List<Locality> localitiesForSave = generator.objects(Locality.class, generator.nextInt(3, 5)).toList();
        localitiesForSave.forEach(locality -> {
            locality.setAnotherNames(generator.objects(String.class, generator.nextInt(2, 3)).collect(Collectors.toSet()));
            locality.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? localityFilter.getName() : localityFilter.getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            locality.setAddress(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? localityFilter.getAddress() : localityFilter.getAddress().toLowerCase()) +
                    stringRandomizer.getRandomValue());
            if (generator.nextBoolean()) {
                locality.setType(genealogy.visualizer.entity.enums.LocalityType.TOWN);
            }
        });
        return localitiesForSave.stream().map(localityDAO::save).collect(Collectors.toList());
    }

    private static List<Death> generateRandomExistDeaths(DeathDAO deathDAO) {
        List<Death> deathsForSave = generator.objects(Death.class, generator.nextInt(15, 20)).toList();
        deathsForSave.forEach(death -> {
            death.setLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            death.setArchiveDocument(existingArchiveDocuments.get(generator.nextInt(existingArchiveDocuments.size())));
            genealogy.visualizer.entity.model.FullName fullName = new genealogy.visualizer.entity.model.FullName();
            fullName.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? deathFilter.getFullName().getName() : deathFilter.getFullName().getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setSurname(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? deathFilter.getFullName().getSurname() : deathFilter.getFullName().getSurname().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setLastName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? deathFilter.getFullName().getLastName() : deathFilter.getFullName().getLastName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            death.setFullName(fullName);
            LocalDate date = LocalDate.of(deathFilter.getDeathYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
            death.setDate(date);
        });
        return deathsForSave.stream().map(deathDAO::save).collect(Collectors.toList());
    }

    private static List<Christening> generateRandomExistChristenings(ChristeningDAO christeningDAO) {
        List<Christening> christeningsForSave = generator.objects(Christening.class, generator.nextInt(15, 20)).toList();
        christeningsForSave.forEach(christening -> {
            List<GodParent> godParents = generator.objects(GodParent.class, generator.nextInt(2, 3)).toList();
            godParents.forEach(gp -> gp.setLocality(existingLocalities.get(generator.nextInt(existingLocalities.size()))));
            christening.setGodParents(godParents);
            christening.setLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            christening.setArchiveDocument(existingArchiveDocuments.get(generator.nextInt(existingArchiveDocuments.size())));
            christening.setSex(Sex.MALE);
            christening.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? christeningFilter.getName() : christeningFilter.getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            LocalDate christeningDate = LocalDate.of(christeningFilter.getChristeningYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
            christening.setChristeningDate(christeningDate);
        });
        return christeningsForSave.stream().map(christeningDAO::save).collect(Collectors.toList());
    }

    private static List<Marriage> generateRandomExistMarriages(MarriageDAO marriageDAO) {
        List<Marriage> marriagesForSave = generator.objects(Marriage.class, generator.nextInt(15, 20)).toList();
        marriagesForSave.forEach(marriage -> {
            List<Witness> witnesses = generator.objects(Witness.class, generator.nextInt(2, 3)).toList();
            witnesses.forEach(w -> w.setLocality(existingLocalities.get(generator.nextInt(existingLocalities.size()))));
            marriage.setWitnesses(witnesses);
            marriage.setHusbandLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            marriage.setWifeLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            marriage.setArchiveDocument(existingArchiveDocuments.get(generator.nextInt(existingArchiveDocuments.size())));
            genealogy.visualizer.entity.model.FullName husbandFullName = generator.nextObject(genealogy.visualizer.entity.model.FullName.class);
            husbandFullName.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? marriageFilter.getHusbandFullName().getName() : marriageFilter.getHusbandFullName().getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            husbandFullName.setSurname(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? marriageFilter.getHusbandFullName().getSurname() : marriageFilter.getHusbandFullName().getSurname().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            husbandFullName.setLastName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? marriageFilter.getHusbandFullName().getLastName() : marriageFilter.getHusbandFullName().getLastName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            marriage.setHusband(husbandFullName);
            genealogy.visualizer.entity.model.FullName wifeFullName = generator.nextObject(genealogy.visualizer.entity.model.FullName.class);
            wifeFullName.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? marriageFilter.getWifeFullName().getName() : marriageFilter.getWifeFullName().getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            wifeFullName.setSurname(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? marriageFilter.getWifeFullName().getSurname() : marriageFilter.getWifeFullName().getSurname().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            wifeFullName.setLastName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? marriageFilter.getWifeFullName().getLastName() : marriageFilter.getWifeFullName().getLastName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            marriage.setWife(wifeFullName);
            LocalDate marriageDate = LocalDate.of(marriageFilter.getMarriageYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
            marriage.setDate(marriageDate);
        });
        return marriagesForSave.stream().map(marriageDAO::save).collect(Collectors.toList());
    }

    private static List<FamilyRevision> generateRandomExistFamilyRevisions(FamilyRevisionDAO familyRevisionDAO) {
        List<FamilyRevision> familyRevisionsForSave = generator.objects(FamilyRevision.class, generator.nextInt(15, 20)).toList();
        familyRevisionsForSave.forEach(familyRevision -> {
            familyRevision.setAnotherNames(generator.objects(String.class, generator.nextInt(2, 5)).collect(Collectors.toSet()));
            familyRevision.setArchiveDocument(existingArchiveDocuments.get(generator.nextInt(existingArchiveDocuments.size())));
            genealogy.visualizer.entity.model.FullName fullName = new genealogy.visualizer.entity.model.FullName();
            fullName.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? familyMemberFilter.getFullName().getName() : familyMemberFilter.getFullName().getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setSurname(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? familyMemberFilter.getFullName().getSurname() : familyMemberFilter.getFullName().getSurname().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setLastName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? familyMemberFilter.getFullName().getLastName() : familyMemberFilter.getFullName().getLastName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            familyRevision.setFullName(fullName);
            familyRevision.setSex(genealogy.visualizer.entity.enums.Sex.MALE);
            familyRevision.setFamilyRevisionNumber(familyMemberFilter.getFamilyRevisionNumber().shortValue());
        });
        List<FamilyRevision> familyRevisionsSaved = familyRevisionsForSave.stream().map(familyRevisionDAO::save).toList();
        List<FamilyRevision> partnersForSave = generator.objects(FamilyRevision.class, generator.nextInt(10, 13)).toList();
        List<FamilyRevision> familyRevisions = new ArrayList<>(familyRevisionsSaved);
        partnersForSave.forEach(familyRevision -> {
            FamilyRevision partner = familyRevisions.remove(generator.nextInt(familyRevisions.size()));
            familyRevision.setPartner(partner);
            familyRevision.setFamilyRevisionNumber(partner.getFamilyRevisionNumber());
            familyRevision.setAnotherNames(generator.objects(String.class, generator.nextInt(2, 5)).collect(Collectors.toSet()));
            familyRevision.setArchiveDocument(partner.getArchiveDocument());
            genealogy.visualizer.entity.model.FullName fullName = new genealogy.visualizer.entity.model.FullName();
            fullName.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? familyMemberFilter.getFullName().getName() : familyMemberFilter.getFullName().getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setSurname(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? familyMemberFilter.getFullName().getSurname() : familyMemberFilter.getFullName().getSurname().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setLastName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? familyMemberFilter.getFullName().getLastName() : familyMemberFilter.getFullName().getLastName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            familyRevision.setFullName(fullName);
            familyRevision.setSex(genealogy.visualizer.entity.enums.Sex.MALE);
            familyRevision.setFamilyRevisionNumber(familyMemberFilter.getFamilyRevisionNumber().shortValue());
        });
        List<FamilyRevision> partnersSaved = partnersForSave.stream().map(familyRevisionDAO::save).collect(Collectors.toList());
        partnersSaved.addAll(familyRevisionsSaved);
        return partnersSaved;
    }

    private static List<Person> generateRandomExistPersons(PersonDAO personDAO) {
        List<Person> personsForSave = generator.objects(Person.class, generator.nextInt(9, 10)).toList();
        List<FamilyRevision> familyRevisions = new ArrayList<>(existingFamilyRevisions);
        List<Christening> christenings = new ArrayList<>(existingChristenings);
        List<Death> deaths = new ArrayList<>(existingDeaths);
        List<Marriage> marriages = new ArrayList<>(existingMarriages);

        personsForSave.forEach(person -> {
            person.setBirthLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeathLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeath(generator.nextBoolean() && !deaths.isEmpty() ? deaths.remove(generator.nextInt(deaths.size())) : null);
            person.setChristening(generator.nextBoolean() && !christenings.isEmpty() ? christenings.remove(generator.nextInt(christenings.size())) : null);
            int count = generator.nextInt(1, 4);
            person.setRevisions(generator.nextBoolean() && !familyRevisions.isEmpty() && familyRevisions.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> familyRevisions.remove(generator.nextInt(familyRevisions.size()))).toList() :
                    Collections.emptyList());
            count = generator.nextInt(1, 4);
            person.setMarriages(!marriages.isEmpty() && marriages.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> marriages.remove(generator.nextInt(marriages.size()))).toList() :
                    Collections.emptyList());
            genealogy.visualizer.entity.model.FullName fullName = new genealogy.visualizer.entity.model.FullName();
            fullName.setName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? personFilter.getFullName().getName() : personFilter.getFullName().getName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setSurname(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? personFilter.getFullName().getSurname() : personFilter.getFullName().getSurname().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            fullName.setLastName(stringRandomizer.getRandomValue() +
                    (generator.nextBoolean() ? personFilter.getFullName().getLastName() : personFilter.getFullName().getLastName().toUpperCase()) +
                    stringRandomizer.getRandomValue());
            person.setFullName(fullName);
            person.setSex(genealogy.visualizer.entity.enums.Sex.MALE);
            genealogy.visualizer.randomizer.DateInfoRandomizer dateInfoRandomizer = new genealogy.visualizer.randomizer.DateInfoRandomizer();
            dateInfoRandomizer.setYear(personFilter.getBirthYear());
            person.setBirthDate(dateInfoRandomizer.getRandomValue());
            dateInfoRandomizer.setYear(personFilter.getDeathYear());
            person.setDeathDate(dateInfoRandomizer.getRandomValue());
        });
        List<Person> personsSaved = personsForSave.stream().map(personDAO::save).toList();
        List<Person> results = new ArrayList<>(personsSaved);

        List<Person> partners = new ArrayList<>(personsSaved);
        List<Person> partnerForSave = generator.objects(Person.class, generator.nextInt(6, 7)).toList();
        partnerForSave.forEach(person -> {
            person.setBirthLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeathLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeath(generator.nextBoolean() && !deaths.isEmpty() ? deaths.remove(generator.nextInt(deaths.size())) : null);
            person.setChristening(generator.nextBoolean() && !christenings.isEmpty() ? christenings.remove(generator.nextInt(christenings.size())) : null);
            int count = generator.nextInt(1, 4);
            person.setRevisions(generator.nextBoolean() && !familyRevisions.isEmpty() && familyRevisions.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> familyRevisions.remove(generator.nextInt(familyRevisions.size()))).toList() :
                    Collections.emptyList());
            count = generator.nextInt(1, 3);
            person.setPartners(!partners.isEmpty() && partners.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> {
                        Person partner = partners.remove(generator.nextInt(partners.size()));
                        if (partner.getMarriages() != null && !partner.getMarriages().isEmpty()) {
                            person.setMarriages(List.of(partner.getMarriages().get(generator.nextInt(partner.getMarriages().size()))));
                        }
                        return partner;
                    }).toList() :
                    Collections.emptyList());
        });
        results.addAll(partnerForSave.stream().map(personDAO::save).toList());

        List<Person> parents = new ArrayList<>(personsSaved);
        List<Person> childrenForSave = generator.objects(Person.class, generator.nextInt(6, 7)).toList();
        childrenForSave.forEach(person -> {
            person.setBirthLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeathLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeath(generator.nextBoolean() && !deaths.isEmpty() ? deaths.remove(generator.nextInt(deaths.size())) : null);
            person.setChristening(generator.nextBoolean() && !christenings.isEmpty() ? christenings.remove(generator.nextInt(christenings.size())) : null);
            int count = generator.nextInt(1, 4);
            person.setRevisions(generator.nextBoolean() && !familyRevisions.isEmpty() && familyRevisions.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> familyRevisions.remove(generator.nextInt(familyRevisions.size()))).toList() :
                    Collections.emptyList());
            count = generator.nextInt(1, 3);
            person.setMarriages(!marriages.isEmpty() && marriages.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> marriages.remove(generator.nextInt(marriages.size()))).toList() :
                    Collections.emptyList());
            count = generator.nextInt(1, 3);
            person.setParents(!parents.isEmpty() && parents.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> parents.remove(generator.nextInt(parents.size()))).toList() :
                    Collections.emptyList());
        });
        results.addAll(childrenForSave.stream().map(personDAO::save).toList());

        List<Person> children = new ArrayList<>(personsSaved);
        List<Person> parentsForSave = generator.objects(Person.class, generator.nextInt(6, 7)).toList();
        parentsForSave.forEach(person -> {
            person.setBirthLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeathLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
            person.setDeath(generator.nextBoolean() && !deaths.isEmpty() ? deaths.remove(generator.nextInt(deaths.size())) : null);
            person.setChristening(generator.nextBoolean() && !christenings.isEmpty() ? christenings.remove(generator.nextInt(christenings.size())) : null);
            int count = generator.nextInt(1, 4);
            person.setRevisions(!familyRevisions.isEmpty() && familyRevisions.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> familyRevisions.remove(generator.nextInt(familyRevisions.size()))).toList() :
                    Collections.emptyList());
            count = generator.nextInt(1, 3);
            person.setMarriages(!marriages.isEmpty() && marriages.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> marriages.remove(generator.nextInt(marriages.size()))).toList() :
                    Collections.emptyList());
            count = generator.nextInt(1, 3);
            person.setChildren(!children.isEmpty() && children.size() > count ?
                    IntStream.range(0, count).mapToObj(i -> children.remove(generator.nextInt(children.size()))).toList() :
                    Collections.emptyList());
        });
        results.addAll(parentsForSave.stream().map(personDAO::save).toList());
        return results;
    }

    private static void generateRandomExistUser(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        genealogy.visualizer.entity.User user = generator.nextObject(genealogy.visualizer.entity.User.class);
        userExistingPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userExisting = userRepository.save(user);
    }
}
