package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.Sex;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevision;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevisions;
import static genealogy.visualizer.controller.LocalityControllerTest.assertLocality;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriage;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonControllerTest extends IntegrationTest {

    private static final String PATH = "/person";

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Person personExist = existingPersons.stream()
                .filter(e -> e.getChristening() != null && e.getDeath() != null && e.getBirthLocality() != null &&
                        e.getDeathLocality() != null && e.getMarriages() != null && e.getMarriages().isEmpty() &&
                        e.getRevisions() != null && e.getRevisions().isEmpty() && e.getParents() != null && e.getParents().isEmpty() &&
                        e.getChildren() != null && e.getChildren().isEmpty() && e.getPartners() != null && e.getPartners().isEmpty())
                .findAny().orElse(existingPersons.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        Person response = objectMapper.readValue(getRequest(PATH + "/" + personExist.getId()), Person.class);
        assertEquals(6, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertPerson(response, personExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        List<EasyPerson> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(personFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyPerson.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        Set<Long> findIds = response.stream().map(EasyPerson::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Person person : existingPersons) {
            if (containsIgnoreCase(person.getFullName().getName(), personFilter.getFullName().getName()) &&
                    containsIgnoreCase(person.getFullName().getSurname(), personFilter.getFullName().getSurname()) &&
                    containsIgnoreCase(person.getFullName().getLastName(), personFilter.getFullName().getLastName()) &&
                    person.getBirthDate().getDate().contains(personFilter.getBirthYear().toString()) &&
                    person.getDeathDate().getDate().contains(personFilter.getDeathYear().toString()) &&
                    person.getSex().name().equals(personFilter.getSex().name())) {
                assertTrue(findIds.contains(person.getId()));
            }
        }
        personFilter.getFullName().setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(personFilter));
    }

    @Test
    void searchTest() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        String searchString = personFilter.getFullName().getName() + " " +
                personFilter.getFullName().getSurname() + " " +
                personFilter.getFullName().getLastName();
        List<EasyPerson> response = objectMapper.readValue(getRequest(PATH + "/search", searchString),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyPerson.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        Set<Long> findIds = response.stream().map(EasyPerson::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Person person : existingPersons) {
            if (containsIgnoreCase(person.getFullName().getName(), personFilter.getFullName().getName()) &&
                    containsIgnoreCase(person.getFullName().getSurname(), personFilter.getFullName().getSurname()) &&
                    containsIgnoreCase(person.getFullName().getLastName(), personFilter.getFullName().getLastName())) {
                assertTrue(findIds.contains(person.getId()));
            }
        }
    }

    @Test
    void saveTest() throws Exception {
        Person personSave = getPerson(existingPersons.stream()
                .filter(e -> e.getChristening() != null && e.getDeath() != null && e.getBirthLocality() != null &&
                        e.getDeathLocality() != null && e.getMarriages() != null && e.getMarriages().isEmpty() &&
                        e.getRevisions() != null && e.getRevisions().isEmpty() && e.getParents() != null && e.getParents().isEmpty() &&
                        e.getChildren() != null && e.getChildren().isEmpty() && e.getPartners() != null && e.getPartners().isEmpty())
                .findAny().orElse(existingPersons.getFirst()));
        personSave.setId(null);

        Person response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(personSave)), Person.class);
        assertPerson(response, personSave);

        Person responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Person.class);
        assertPerson(responseGet, responseGet);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(personSave));
    }

    @Test
    void updateTest() throws Exception {
        Person personUpdate = getPerson(existingPersons.stream()
                .filter(e -> e.getChristening() != null && e.getDeath() != null && e.getBirthLocality() != null &&
                        e.getDeathLocality() != null && e.getMarriages() != null && e.getMarriages().isEmpty() &&
                        e.getRevisions() != null && e.getRevisions().isEmpty() && e.getParents() != null && e.getParents().isEmpty() &&
                        e.getChildren() != null && e.getChildren().isEmpty() && e.getPartners() != null && e.getPartners().isEmpty())
                .findAny().orElse(existingPersons.getFirst()));

        Person response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(personUpdate)), Person.class);
        assertPerson(response, personUpdate);

        Person responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Person.class);
        assertPerson(responseGet, personUpdate);

        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(personUpdate));

        personUpdate.setChristening(null);
        personUpdate.setDeath(null);
        personUpdate.setDeathLocality(null);
        personUpdate.setBirthLocality(null);
        personUpdate.setMarriages(Collections.emptyList());
        personUpdate.setRevisions(Collections.emptyList());
        personUpdate.setPartners(Collections.emptyList());
        personUpdate.setParents(Collections.emptyList());
        personUpdate.setChildren(Collections.emptyList());
        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(personUpdate)), Person.class);
        assertPerson(response, personUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Person personExist = existingPersons.stream()
                .filter(e -> e.getChristening() != null && e.getDeath() != null && e.getBirthLocality() != null &&
                        e.getDeathLocality() != null && e.getMarriages() != null && e.getMarriages().isEmpty() &&
                        e.getRevisions() != null && e.getRevisions().isEmpty() && e.getParents() != null && e.getParents().isEmpty() &&
                        e.getChildren() != null && e.getChildren().isEmpty() && e.getPartners() != null && e.getPartners().isEmpty())
                .findAny().orElse(existingPersons.getFirst());

        String responseJson = deleteRequest(PATH + "/" + personExist.getId());
        existingPersons.remove(personExist);

        assertTrue(responseJson.isEmpty());
        assertTrue(personRepository.findById(personExist.getId()).isEmpty());
        if (personExist.getChildren() != null) {
            personExist.getChildren().forEach(p -> assertFalse(personRepository.findById(p.getId()).isEmpty()));
        }
        if (personExist.getPartners() != null) {
            personExist.getPartners().forEach(p -> assertFalse(personRepository.findById(p.getId()).isEmpty()));
        }
        if (personExist.getParents() != null) {
            personExist.getParents().forEach(p -> assertFalse(personRepository.findById(p.getId()).isEmpty()));
        }
        if (personExist.getRevisions() != null) {
            personExist.getRevisions().forEach(r -> assertFalse(familyRevisionRepository.findById(r.getId()).isEmpty()));
        }
        if (personExist.getMarriages() != null) {
            personExist.getMarriages().forEach(m -> assertFalse(marriageRepository.findById(m.getId()).isEmpty()));
        }
        if (personExist.getChristening() != null) {
            assertFalse(christeningRepository.findById(personExist.getChristening().getId()).isEmpty());
        }
        if (personExist.getDeath() != null) {
            assertFalse(deathRepository.findById(personExist.getDeath().getId()).isEmpty());
        }
        if (personExist.getBirthLocality() != null) {
            assertFalse(localityRepository.findById(personExist.getBirthLocality().getId()).isEmpty());
        }
        if (personExist.getDeathLocality() != null) {
            assertFalse(localityRepository.findById(personExist.getDeathLocality().getId()).isEmpty());
        }

        deleteUnauthorizedRequest(PATH + "/" + personExist.getId());
    }

    static void assertPerson(Person person1, Person person2) {
        assertPerson(toEasyPerson(person1), toEasyPerson(person2));
        assertLocality(person1.getBirthLocality(), person2.getBirthLocality());
        assertLocality(person1.getDeathLocality(), person2.getDeathLocality());
        assertChristening(person1.getChristening(), person2.getChristening());
        assertDeath(person1.getDeath(), person2.getDeath());
        assertMarriage(person1.getMarriages(), person2.getMarriages());
        assertPerson(person1.getParents(), person2.getParents());
        assertPerson(person1.getChildren(), person2.getChildren());
        assertPerson(person1.getPartners(), person2.getPartners());
        assertFamilyRevision(person1.getRevisions(), person2.getRevisions());
    }

    static void assertPerson(Person person1, genealogy.visualizer.entity.Person person2) {
        assertPerson(toEasyPerson(person1), toEasyPerson(person2));
        assertLocality(person1.getBirthLocality(), person2.getBirthLocality());
        assertLocality(person1.getDeathLocality(), person2.getDeathLocality());
        assertChristening(person1.getChristening(), person2.getChristening());
        assertDeath(person1.getDeath(), person2.getDeath());
        assertMarriages(person1.getMarriages(), person2.getMarriages());
        assertPersons(person1.getParents(), person2.getParents());
        assertPersons(person1.getChildren(), person2.getChildren());
        assertPersons(person1.getPartners(), person2.getPartners());
        assertFamilyRevisions(person1.getRevisions(), person2.getRevisions());
    }

    protected static void assertPersons(List<EasyPerson> persons1, List<genealogy.visualizer.entity.Person> persons2) {
        assertNotNull(persons2);
        assertPerson(persons1, persons2.stream().map(PersonControllerTest::toEasyPerson).toList());
    }

    protected static void assertPerson(List<EasyPerson> persons1, List<EasyPerson> persons2) {
        if (persons1 == null || persons2 == null) {
            assertNull(persons1);
            assertNull(persons2);
            return;
        }
        assertNotNull(persons1);
        assertNotNull(persons2);
        assertEquals(persons1.size(), persons2.size());
        List<EasyPerson> personsSorted1 = persons1.stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
        List<EasyPerson> personsSorted2 = persons2.stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
        for (int i = 0; i < personsSorted1.size(); i++) {
            assertPerson(personsSorted1.get(i), personsSorted2.get(i));
        }
    }

    protected static void assertPerson(EasyPerson person1, EasyPerson person2) {
        if (person1 == null || person2 == null) {
            assertNull(person1);
            assertNull(person2);
            return;
        }
        assertNotNull(person1);
        assertNotNull(person2);
        if (person1.getId() != null && person2.getId() != null) {
            assertEquals(person1.getId(), person2.getId());
        }
        assertDateInfo(person1.getBirthDate(), person2.getBirthDate());
        assertDateInfo(person1.getDeathDate(), person2.getDeathDate());
        assertFullName(person1.getFullName(), person2.getFullName());
        assertEquals(person1.getSex(), person2.getSex());
    }

    protected static void assertPerson(EasyPerson person1, genealogy.visualizer.entity.Person person2) {
        assertPerson(person1, toEasyPerson(person2));
    }

    private Person getPerson(genealogy.visualizer.entity.Person existPerson) {
        Person person = generator.nextObject(Person.class);
        person.setId(existPerson.getId());
        List<EasyPerson> parents = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList());
        existPerson.getParents().forEach(p -> {
            if (generator.nextBoolean()) {
                parents.add(easyPersonMapper.toDTO(p));
            }
        });
        person.setParents(parents);
        List<EasyPerson> partners = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList());
        existPerson.getPartners().forEach(p -> {
            if (generator.nextBoolean()) {
                partners.add(easyPersonMapper.toDTO(p));
            }
        });
        person.setPartners(partners);
        List<EasyPerson> children = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList());
        existPerson.getChildren().forEach(p -> {
            if (generator.nextBoolean()) {
                children.add(easyPersonMapper.toDTO(p));
            }
        });
        person.setChildren(children);
        List<EasyMarriage> marriages = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(5, 10)).toList());
        existPerson.getMarriages().forEach(m -> {
            if (generator.nextBoolean()) {
                marriages.add(easyMarriageMapper.toDTO(m));
            }
        });
        person.setMarriages(marriages);
        List<EasyFamilyMember> revisions = new ArrayList<>(generator.objects(EasyFamilyMember.class, generator.nextInt(5, 10)).toList());
        existPerson.getRevisions().forEach(fr -> {
            if (generator.nextBoolean()) {
                revisions.add(easyFamilyRevisionMapper.toDTO(fr));
            }
        });
        person.setRevisions(revisions);
        person.getFullName().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        person.setChristening(generator.nextObject(EasyChristening.class));
        person.setDeath(generator.nextObject(EasyDeath.class));
        person.setBirthLocality(easyLocalityMapper.toDTO(existingLocalities.get(generator.nextInt(existingLocalities.size()))));
        person.setDeathLocality(generator.nextObject(EasyLocality.class));
        return person;
    }

    private static EasyPerson toEasyPerson(genealogy.visualizer.entity.Person person) {
        if (person == null) {
            return null;
        }
        return new EasyPerson()
                .id(person.getId())
                .birthDate(toDateInfo(person.getBirthDate()))
                .deathDate(toDateInfo(person.getDeathDate()))
                .fullName(toFullName(person.getFullName()))
                .sex(Sex.valueOf(person.getSex().name()));
    }

    private static EasyPerson toEasyPerson(Person person) {
        if (person == null) {
            return null;
        }
        return new EasyPerson()
                .id(person.getId())
                .birthDate(person.getBirthDate())
                .deathDate(person.getDeathDate())
                .fullName(person.getFullName())
                .sex(person.getSex());
    }
}
