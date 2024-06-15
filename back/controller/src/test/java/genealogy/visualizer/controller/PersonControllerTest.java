package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.FullNameFilter;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.api.model.Sex;
import genealogy.visualizer.entity.enums.DateRangeType;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.service.PersonDAO;
import org.apache.commons.lang3.StringUtils;
import org.jeasy.random.randomizers.misc.EnumRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevision;
import static genealogy.visualizer.controller.LocalityControllerTest.assertLocality;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonControllerTest extends IntegrationTest {

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PersonDAO personDAO;

    private static final String PATH = "/person";

    @Test
    void saveTest() throws Exception {
        Person personSave = generatePerson();
        String requestJson = objectMapper.writeValueAsString(personSave);
        String responseJson = postRequest(PATH, requestJson);
        Person response = objectMapper.readValue(responseJson, Person.class);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertPerson(response, personSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Person responseGet = objectMapper.readValue(responseJsonGet, Person.class);
        assertNotNull(responseGet);
        assertPerson(responseGet, personSave);
    }

    @Test
    void updateDeleteLinksTest() throws Exception {
        genealogy.visualizer.entity.Person personEntity = personDAO.save(personMapper.toEntity(generatePerson()));
        Person personUpdate = generatePerson();
        personUpdate.setId(personEntity.getId());
        personUpdate.setBirthLocality(null);
        personUpdate.setDeathLocality(null);
        personUpdate.setDeath(null);
        personUpdate.setChristening(null);
        personUpdate.setRevisions(Collections.emptyList());
        personUpdate.setParents(Collections.emptyList());
        personUpdate.setChildren(Collections.emptyList());
        personUpdate.setPartners(Collections.emptyList());
        personUpdate.setMarriages(Collections.emptyList());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(personUpdate));
        Person response = objectMapper.readValue(responseJson, Person.class);
        assertNotNull(response);
        assertEquals(response.getId(), personUpdate.getId());
        assertPerson(response, personUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Person responseGet = objectMapper.readValue(responseJsonGet, Person.class);
        assertNotNull(responseGet);
        assertPerson(responseGet, personUpdate);
    }

    @Test
    void updateAddLinksTest() throws Exception {
        genealogy.visualizer.entity.Person personEntity = generator.nextObject(genealogy.visualizer.entity.Person.class);
        personEntity.setBirthLocality(null);
        personEntity.setDeathLocality(null);
        personEntity.setDeath(null);
        personEntity.setChristening(null);
        personEntity.setRevisions(Collections.emptyList());
        personEntity.setParents(Collections.emptyList());
        personEntity.setChildren(Collections.emptyList());
        personEntity.setPartners(Collections.emptyList());
        personEntity.setMarriages(Collections.emptyList());
        personEntity = personRepository.save(personEntity);
        Person personUpdate = generatePerson();
        personUpdate.setId(personEntity.getId());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(personUpdate));
        String responseJson1 = putRequest(PATH, responseJson);
        Person response = objectMapper.readValue(responseJson1, Person.class);
        assertNotNull(response);
        assertEquals(response.getId(), personUpdate.getId());
        assertPerson(response, personUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Person responseGet = objectMapper.readValue(responseJsonGet, Person.class);
        assertNotNull(responseGet);
        assertPerson(responseGet, personUpdate);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Person personEntity = personMapper.toEntity(generatePerson());
        personEntity = personDAO.save(personEntity);
        Person personUpdate = generatePerson();
        personUpdate.setId(personEntity.getId());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(personUpdate));
        String responseJson1 = putRequest(PATH, responseJson);
        Person response = objectMapper.readValue(responseJson1, Person.class);
        assertNotNull(response);
        assertEquals(response.getId(), personUpdate.getId());
        assertPerson(response, personUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Person responseGet = objectMapper.readValue(responseJsonGet, Person.class);
        assertNotNull(responseGet);
        assertPerson(responseGet, personUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        Person person = generatePerson();
        genealogy.visualizer.entity.Person personEntity = personMapper.toEntity(person);
        personEntity = personDAO.save(personEntity);
        String responseJson = deleteRequest(PATH + "/" + personEntity.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(personRepository.findById(personEntity.getId()).isEmpty());
        personEntity.getChildren().forEach(p -> assertFalse(personRepository.findById(p.getId()).isEmpty()));
        personEntity.getPartners().forEach(p -> assertFalse(personRepository.findById(p.getId()).isEmpty()));
        personEntity.getParents().forEach(p -> assertFalse(personRepository.findById(p.getId()).isEmpty()));
        personEntity.getRevisions().forEach(r -> assertFalse(familyRevisionRepository.findById(r.getId()).isEmpty()));
        personEntity.getMarriages().forEach(m -> assertFalse(marriageRepository.findById(m.getId()).isEmpty()));
        assertFalse(christeningRepository.findById(personEntity.getChristening().getId()).isEmpty());
        assertFalse(deathRepository.findById(personEntity.getDeath().getId()).isEmpty());
        assertFalse(localityRepository.findById(personEntity.getBirthLocality().getId()).isEmpty());
        assertFalse(localityRepository.findById(personEntity.getDeathLocality().getId()).isEmpty());
    }

    @Test
    void getByIdTest() throws Exception {
        Person person = generatePerson();
        genealogy.visualizer.entity.Person personEntity = personMapper.toEntity(person);
        personEntity = personDAO.save(personEntity);
        String responseJson = getRequest(PATH + "/" + personEntity.getId());
        Person response = objectMapper.readValue(responseJson, Person.class);
        assertNotNull(response);
        assertEquals(response.getId(), personEntity.getId());
        assertPerson(response, person);
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        FullNameFilter fullNameFilter = new FullNameFilter()
                .name("Иван")
                .surname("Иванович")
                .lastName("Иванов");
        PersonFilter filter = new PersonFilter()
                .fullName(fullNameFilter)
                .birthYear(1820)
                .deathYear(1870)
                .sex(Sex.MALE);
        List<genealogy.visualizer.entity.Person> personsSave = generator.objects(genealogy.visualizer.entity.Person.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        EnumRandomizer<DateRangeType> enumRandomizer = new EnumRandomizer<>(DateRangeType.class);
        genealogy.visualizer.entity.model.DateInfo birthDate = new genealogy.visualizer.entity.model.DateInfo(
                stringRandomizer.getRandomValue() + filter.getBirthYear() + stringRandomizer.getRandomValue(),
                enumRandomizer.getRandomValue());
        genealogy.visualizer.entity.model.DateInfo deathDate = new genealogy.visualizer.entity.model.DateInfo(
                stringRandomizer.getRandomValue() + filter.getDeathYear() + stringRandomizer.getRandomValue(),
                enumRandomizer.getRandomValue());
        for (genealogy.visualizer.entity.Person person : personsSave) {
            person.setParents(Collections.emptyList());
            person.setPartners(Collections.emptyList());
            person.setChildren(Collections.emptyList());
            person.setRevisions(Collections.emptyList());
            person.setMarriages(Collections.emptyList());
            person.setChristening(null);
            person.setDeath(null);
            person.setDeathLocality(null);
            person.setBirthLocality(null);
            if (generator.nextBoolean()) {
                genealogy.visualizer.entity.model.FullName fullName = new genealogy.visualizer.entity.model.FullName();
                fullName.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? fullNameFilter.getName() : fullNameFilter.getName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                fullName.setSurname(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? fullNameFilter.getSurname() : fullNameFilter.getSurname().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                fullName.setLastName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? fullNameFilter.getLastName() : fullNameFilter.getLastName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                person.setFullName(fullName);
                person.setSex(genealogy.visualizer.entity.enums.Sex.MALE);
                person.setBirthDate(birthDate);
                person.setDeathDate(deathDate);
                count++;
            }
        }
        List<genealogy.visualizer.entity.Person> personsExist = personRepository.saveAllAndFlush(personsSave);
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyPerson> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyPerson.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyPerson::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Person person : personsExist) {
            if (person.getFullName().getName().toLowerCase().contains(filter.getFullName().getName().toLowerCase()) &&
                    person.getFullName().getSurname().toLowerCase().contains(filter.getFullName().getSurname().toLowerCase()) &&
                    person.getFullName().getLastName().toLowerCase().contains(filter.getFullName().getLastName().toLowerCase()) &&
                    person.getBirthDate().getDate().contains(filter.getBirthYear().toString()) &&
                    person.getDeathDate().getDate().contains(filter.getDeathYear().toString()) &&
                    person.getSex().name().equals(filter.getSex().name())) {
                assertTrue(findIds.contains(person.getId()));
            }
        }
    }

    @Test
    void searchTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        List<String> strings = generator.objects(String.class, 3).toList();
        List<genealogy.visualizer.entity.Person> personsSave = generator.objects(genealogy.visualizer.entity.Person.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Person person : personsSave) {
            person.setParents(Collections.emptyList());
            person.setPartners(Collections.emptyList());
            person.setChildren(Collections.emptyList());
            person.setRevisions(Collections.emptyList());
            person.setMarriages(Collections.emptyList());
            person.setChristening(null);
            person.setDeath(null);
            person.setDeathLocality(null);
            person.setBirthLocality(null);
            if (generator.nextBoolean()) {
                genealogy.visualizer.entity.model.FullName fullName = new genealogy.visualizer.entity.model.FullName();
                fullName.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? strings.get(0) : strings.get(0).toUpperCase()) +
                        stringRandomizer.getRandomValue());
                fullName.setSurname(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? strings.get(1) : strings.get(1).toUpperCase()) +
                        stringRandomizer.getRandomValue());
                fullName.setLastName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? strings.get(2) : strings.get(2).toUpperCase()) +
                        stringRandomizer.getRandomValue());
                person.setFullName(fullName);
                count++;
            }
        }
        List<genealogy.visualizer.entity.Person> personsExist = personRepository.saveAllAndFlush(personsSave);
        String responseJson = getRequest(PATH + "/search", StringUtils.join(strings, " "));
        List<EasyPerson> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyPerson.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyPerson::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Person person : personsExist) {
            if (person.getFullName().getName().toLowerCase().contains(strings.get(0).toLowerCase()) &&
                    person.getFullName().getSurname().toLowerCase().contains(strings.get(1).toLowerCase()) &&
                    person.getFullName().getLastName().toLowerCase().contains(strings.get(2).toLowerCase())) {
                assertTrue(findIds.contains(person.getId()));
            }
        }
    }

    @Test
    void saveUnauthorizedTest() throws Exception {
        Person object = generator.nextObject(Person.class);
        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void updateUnauthorizedTest() throws Exception {
        Person object = generator.nextObject(Person.class);
        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        Person object = generator.nextObject(Person.class);
        deleteUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        christeningRepository.deleteAll();
        familyRevisionRepository.deleteAll();
        deathRepository.deleteAll();
        marriageRepository.deleteAll();
        personRepository.deleteAll();
        super.tearDown();
    }

    private Person generatePerson() {
        EasyPerson mother = generator.nextObject(EasyPerson.class);
        EasyPerson father = generator.nextObject(EasyPerson.class);
        Person personSave = generator.nextObject(Person.class);
        personSave.getFullName().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        personSave.setChristening(generator.nextObject(EasyChristening.class));
        personSave.setDeath(generator.nextObject(EasyDeath.class));
        personSave.setMarriages(generator.objects(EasyMarriage.class, generator.nextInt(1, 3)).toList());
        personSave.setBirthLocality(localityMapper.toDTO(localityExisting));
        personSave.setDeathLocality(generator.nextObject(EasyLocality.class));
        personSave.setParents(List.of(mother, father));
        personSave.setChildren(generator.objects(EasyPerson.class, generator.nextInt(1, 3)).toList());
        personSave.setPartners(generator.objects(EasyPerson.class, generator.nextInt(1, 3)).toList());
        personSave.setRevisions(generator.objects(EasyFamilyMember.class, generator.nextInt(2, 5)).toList());
        return personSave;
    }

    static void assertPerson(Person expected, Person actual) {
        assertDateInfo(expected.getBirthDate(), actual.getBirthDate());
        assertDateInfo(expected.getDeathDate(), actual.getDeathDate());
        assertFullName(expected.getFullName(), actual.getFullName());
        assertLocality(expected.getBirthLocality(), actual.getBirthLocality());
        assertLocality(expected.getDeathLocality(), actual.getDeathLocality());
        assertChristening(expected.getChristening(), actual.getChristening());
        assertDeath(expected.getDeath(), actual.getDeath());

        assertEquals(expected.getMarriages().size(), actual.getMarriages().size());
        expected.getMarriages().sort(Comparator.comparing(r -> r.getWife().getName()));
        List<EasyMarriage> actualMarriages = actual.getMarriages().stream().sorted(Comparator.comparing(r -> r.getWife().getName())).toList();
        for (int i = 0; i < expected.getMarriages().size(); i++) {
            assertMarriage(expected.getMarriages().get(i), actualMarriages.get(i));
        }

        assertEquals(expected.getParents().size(), actual.getParents().size());
        expected.getParents().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyPerson> actualParents = actual.getParents().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getParents().size(); i++) {
            assertPerson(expected.getParents().get(i), actualParents.get(i));
        }

        assertEquals(expected.getChildren().size(), actual.getChildren().size());
        expected.getChildren().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyPerson> actualChildren = actual.getChildren().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getChildren().size(); i++) {
            assertPerson(expected.getChildren().get(i), actualChildren.get(i));
        }

        assertEquals(expected.getPartners().size(), actual.getPartners().size());
        expected.getPartners().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyPerson> actualPartners = actual.getPartners().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getPartners().size(); i++) {
            assertPerson(expected.getPartners().get(i), actualPartners.get(i));
        }

        assertEquals(expected.getRevisions().size(), actual.getRevisions().size());
        expected.getRevisions().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyFamilyMember> actualRevisions = actual.getRevisions().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getRevisions().size(); i++) {
            assertFamilyRevision(expected.getRevisions().get(i), actualRevisions.get(i));
        }
    }

    static void assertPerson(Person expected, genealogy.visualizer.entity.Person actual) {
        assertDateInfo(expected.getBirthDate(), actual.getBirthDate());
        assertDateInfo(expected.getDeathDate(), actual.getDeathDate());
        assertFullName(expected.getFullName(), actual.getFullName());
        assertLocality(expected.getBirthLocality(), actual.getBirthLocality());
        assertLocality(expected.getDeathLocality(), actual.getDeathLocality());
        assertChristening(expected.getChristening(), actual.getChristening());
        assertDeath(expected.getDeath(), actual.getDeath());

        assertEquals(expected.getMarriages().size(), actual.getMarriages().size());
        expected.getMarriages().sort(Comparator.comparing(r -> r.getWife().getName()));
        List<genealogy.visualizer.entity.Marriage> actualMarriages = actual.getMarriages().stream().sorted(Comparator.comparing(r -> r.getWife().getName())).toList();
        for (int i = 0; i < expected.getMarriages().size(); i++) {
            assertMarriage(expected.getMarriages().get(i), actualMarriages.get(i));
        }

        assertEquals(expected.getParents().size(), actual.getParents().size());
        expected.getParents().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<genealogy.visualizer.entity.Person> actualParents = actual.getParents().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getParents().size(); i++) {
            assertPerson(expected.getParents().get(i), actualParents.get(i));
        }

        assertEquals(expected.getChildren().size(), actual.getChildren().size());
        expected.getChildren().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<genealogy.visualizer.entity.Person> actualChildren = actual.getChildren().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getChildren().size(); i++) {
            assertPerson(expected.getChildren().get(i), actualChildren.get(i));
        }

        assertEquals(expected.getPartners().size(), actual.getPartners().size());
        expected.getPartners().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<genealogy.visualizer.entity.Person> actualPartners = actual.getPartners().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getPartners().size(); i++) {
            assertPerson(expected.getPartners().get(i), actualPartners.get(i));
        }

        assertEquals(expected.getRevisions().size(), actual.getRevisions().size());
        expected.getRevisions().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<genealogy.visualizer.entity.FamilyRevision> actualRevisions = actual.getRevisions().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getRevisions().size(); i++) {
            assertFamilyRevision(expected.getRevisions().get(i), actualRevisions.get(i));
        }
    }

    protected static void assertPerson(EasyPerson expected, EasyPerson actual) {
        if (expected == null || actual == null) {
            assertNull(expected);
            assertNull(actual);
            return;
        }
        assertDateInfo(expected.getBirthDate(), actual.getBirthDate());
        assertDateInfo(expected.getDeathDate(), actual.getDeathDate());
        assertFullName(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getSex(), actual.getSex());
    }

    protected static void assertPerson(EasyPerson expected, genealogy.visualizer.entity.Person actual) {
        assertDateInfo(expected.getBirthDate(), actual.getBirthDate());
        assertDateInfo(expected.getDeathDate(), actual.getDeathDate());
        assertFullName(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getSex().name(), actual.getSex().name());
    }
}
