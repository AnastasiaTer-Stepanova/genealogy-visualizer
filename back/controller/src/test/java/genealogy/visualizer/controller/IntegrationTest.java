package genealogy.visualizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import genealogy.visualizer.api.model.Age;
import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.DateInfo;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FullName;
import genealogy.visualizer.api.model.User;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
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
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.repository.UserRepository;
import genealogy.visualizer.service.authorization.JwtService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MimeType;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;

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
    EasyArchiveDocumentMapper easyArchiveDocumentMapper;

    @Autowired
    EasyFamilyRevisionMapper easyFamilyRevisionMapper;

    @Autowired
    EasyPersonMapper easyPersonMapper;

    @Autowired
    EasyChristeningMapper easyChristeningMapper;

    @Autowired
    EasyDeathMapper easyDeathMapper;

    @Autowired
    EasyMarriageMapper easyMarriageMapper;

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
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    EasyLocalityMapper localityMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    genealogy.visualizer.entity.ArchiveDocument archiveDocumentExisting;

    genealogy.visualizer.entity.Locality localityExisting;

    genealogy.visualizer.entity.Archive archiveExisting;

    genealogy.visualizer.entity.User userExisting;

    private static final String AUTHORIZATION_PATH = "/authorization";

    String userExistingPassword;

    static EasyRandom generator;

    static {
        EasyRandomParameters parameters = getGeneratorParams()
                .randomize(named("id").and(ofType(Long.class)), () -> null)
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
        userExisting = generator.nextObject(genealogy.visualizer.entity.User.class);
        userExistingPassword = userExisting.getPassword();
        userExisting.setPassword(passwordEncoder.encode(userExistingPassword));
        userExisting = userRepository.save(userExisting);
        localityExisting = generator.nextObject(genealogy.visualizer.entity.Locality.class);
        localityExisting.setChristenings(Collections.emptyList());
        localityExisting.setPersonsWithBirthLocality(Collections.emptyList());
        localityExisting.setPersonsWithDeathLocality(Collections.emptyList());
        localityExisting = localityRepository.save(localityExisting);
        archiveExisting = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveExisting = archiveRepository.saveAndFlush(archiveExisting);
        archiveDocumentExisting = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        archiveDocumentExisting.setArchive(archiveExisting);
        archiveDocumentExisting = archiveDocumentRepository.saveAndFlush(archiveDocumentExisting);
        System.out.println("----------------------Start test------------------------");
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        archiveDocumentRepository.deleteAll();
        archiveRepository.deleteAll();
        localityRepository.deleteAll();
        userRepository.deleteAll();
    }

    void postUnauthorizedRequest(String path, String requestJson) throws Exception {
        unauthorizedRequest(post(path), requestJson);
    }

    void putUnauthorizedRequest(String path, String requestJson) throws Exception {
        unauthorizedRequest(put(path), requestJson);
    }

    void deleteUnauthorizedRequest(String path, String requestJson) throws Exception {
        unauthorizedRequest(delete(path), requestJson);
    }

    void unauthorizedRequest(MockHttpServletRequestBuilder requestBuilder, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        requestBuilder
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(new MimeType("application", "json", StandardCharsets.UTF_8).toString()),
                        content().encoding(StandardCharsets.UTF_8))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getMessage(), "Доступ запрещен. Пожалуйста, авторизуйтесь.");
        assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
        System.out.println("----------------------End request------------------------");
    }

    String postRequest(String path, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        post(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                                .header("Authorization", "Bearer " + authorization()))
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
                        delete(path)
                                .header("Authorization", "Bearer " + authorization()))
                .andExpectAll(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String getRequest(String path) throws Exception {
        return getRequest(path, null);
    }

    String getRequest(String path, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        MockHttpServletRequestBuilder requestBuilder = requestJson == null ?
                get(path) :
                get(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson);
        String responseJson = mockMvc.perform(
                        requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String getNotFoundRequest(String path) throws Exception {
        return getNotFoundRequest(path, null);
    }

    String getNotFoundRequest(String path, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        MockHttpServletRequestBuilder requestBuilder = requestJson == null ?
                get(path) :
                get(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson);
        String responseJson = mockMvc.perform(
                        requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
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
                                .content(requestJson)
                                .header("Authorization", "Bearer " + authorization()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String putNotFoundRequest(String path, String requestJson) throws Exception {
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        put(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                                .header("Authorization", "Bearer " + authorization()))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        return responseJson;
    }

    String authorization() throws Exception {
        User user = new User(userExisting.getLogin(), userExistingPassword);
        String responseJson = mockMvc.perform(
                        post(AUTHORIZATION_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(responseJson);
        assertEquals(jwtService.getLogin(responseJson), userExisting.getLogin());
        assertTrue(jwtService.isTokenValid(responseJson, user));
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

    static void assertDateInfo(DateInfo dateInfo1, genealogy.visualizer.entity.model.DateInfo dateInfo2) {
        assertNotNull(dateInfo1);
        assertNotNull(dateInfo2);
        assertEquals(dateInfo1.getDate(), dateInfo2.getDate());
        assertEquals(dateInfo1.getDateRangeType().getValue(), dateInfo2.getDateRangeType().getName());
    }

    static void assertDateInfo(DateInfo dateInfo1, DateInfo dateInfo2) {
        assertNotNull(dateInfo1);
        assertNotNull(dateInfo2);
        assertEquals(dateInfo1.getDate(), dateInfo2.getDate());
        assertEquals(dateInfo1.getDateRangeType(), dateInfo2.getDateRangeType());
    }
}
