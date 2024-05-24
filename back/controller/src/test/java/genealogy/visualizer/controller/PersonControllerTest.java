package genealogy.visualizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FullName;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.service.PersonDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevision;
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
        Person response = getPersonFromJson(responseJson);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertPerson(response, personSave);
    }

    @Test
    void updateDeleteLinksTest() throws Exception {
        Person person = generatePerson();
        genealogy.visualizer.entity.Person personEntity = personMapper.toEntity(person);
        personEntity = personDAO.save(personEntity);
        Person personSave = personMapper.toDTO(personEntity);
        updateIds(personSave);
        assertPerson(personSave, person);
        personSave.setBirthLocality(null);
        personSave.setDeathLocality(null);
        personSave.setDeath(null);
        personSave.setChristening(null);
        personSave.setRevisions(Collections.emptyList());
        personSave.setParents(Collections.emptyList());
        personSave.setChildren(Collections.emptyList());
        personSave.setPartners(Collections.emptyList());
        personSave.setMarriages(Collections.emptyList());
        personSave.setFullName(generator.nextObject(FullName.class));
        String requestJson = objectMapper.writeValueAsString(personSave);
        String responseJson = putRequest(PATH, requestJson);
        Person response = getPersonFromJson(responseJson);
        assertNotNull(response);
        assertEquals(response.getId(), personSave.getId());
        assertPerson(response, personSave);
    }

    @Test
    void updateAddLinksTest() throws Exception {
        Person personSave = generatePerson();
        genealogy.visualizer.entity.Person personEntity = personMapper.toEntity(personSave);
        personEntity.setBirthLocality(null);
        personEntity.setDeathLocality(null);
        personEntity.setDeath(null);
        personEntity.setChristening(null);
        personEntity.setRevisions(Collections.emptyList());
        personEntity.setParents(Collections.emptyList());
        personEntity.setChildren(Collections.emptyList());
        personEntity.setPartners(Collections.emptyList());
        personEntity.setMarriages(Collections.emptyList());
        personDAO.save(personEntity);
        updateIds(personEntity);
        personSave.setId(personEntity.getId());
        personSave.setFullName(generator.nextObject(FullName.class));
        String requestJson = objectMapper.writeValueAsString(personSave);
        String responseJson = putRequest(PATH, requestJson);
        String responseJson1 = putRequest(PATH, responseJson);
        Person response = getPersonFromJson(responseJson1);
        assertNotNull(response);
        assertEquals(response.getId(), personSave.getId());
        assertPerson(response, personSave);
    }

    @Test
    void deleteTest() throws Exception {
        Person person = generatePerson();
        genealogy.visualizer.entity.Person personEntity = personMapper.toEntity(person);
        personEntity = personDAO.save(personEntity);
        updateIds(personEntity);
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
        updateIds(personEntity);
        String responseJson = getRequest(PATH + "/" + personEntity.getId());
        Person response = getPersonFromJson(responseJson);
        assertNotNull(response);
        assertEquals(response.getId(), personEntity.getId());
        assertPerson(response, person);
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        personIds.forEach(id -> personDAO.delete(id));
        christeningRepository.deleteAllById(christeningIds);
        deathRepository.deleteAllById(deathIds);
        marriageRepository.deleteAllById(marriageIds);
        familyRevisionRepository.deleteAllById(familyRevisionIds);
        super.tearDown();
    }

    private Person generatePerson() {
        EasyPerson mother = generator.nextObject(EasyPerson.class);
        mother.setId(null);
        EasyPerson father = generator.nextObject(EasyPerson.class);
        father.setId(null);
        EasyPerson partner = generator.nextObject(EasyPerson.class);
        partner.setId(null);
        EasyPerson child = generator.nextObject(EasyPerson.class);
        child.setId(null);
        FamilyMember familyMember = generator.nextObject(FamilyMember.class);
        familyMember.setArchiveDocument(archiveDocumentExisting);
        familyMember.setId(null);
        familyMember.getPartner().setId(null);
        Christening christening = generator.nextObject(Christening.class);
        christening.setId(null);
        Death death = generator.nextObject(Death.class);
        death.setId(null);
        Marriage marriage = generator.nextObject(Marriage.class);
        marriage.setId(null);
        Person personSave = generator.nextObject(Person.class);
        personSave.setId(null);
        personSave.setChristening(christening);
        personSave.setDeath(death);
        personSave.setMarriages(List.of(marriage));
        personSave.setBirthLocality(localityMapper.toDTO(localityExisting));
        personSave.setDeathLocality(generator.nextObject(Locality.class));
        personSave.setParents(List.of(mother, father));
        personSave.setChildren(List.of(child));
        personSave.setPartners(List.of(partner));
        personSave.setRevisions(List.of(familyMember));
        return personSave;
    }

    static void assertPerson(Person expected, Person actual) {
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
        assertEquals(expected.getDeathDate(), actual.getDeathDate());
        assertEquals(expected.getFullName(), actual.getFullName());
        if (expected.getBirthLocality() != null) {
            assertLocality(expected.getBirthLocality(), actual.getBirthLocality());
        } else {
            assertNull(actual.getBirthLocality());
        }
        if (expected.getDeathLocality() != null) {
            assertLocality(expected.getDeathLocality(), actual.getDeathLocality());
        } else {
            assertNull(actual.getDeathLocality());
        }
        if (expected.getChristening() != null) {
            assertChristening(expected.getChristening(), actual.getChristening());
        } else {
            assertNull(actual.getChristening());
        }
        if (expected.getDeath() != null) {
            assertDeath(expected.getDeath(), actual.getDeath());
        } else {
            assertNull(actual.getDeath());
        }

        assertEquals(expected.getMarriages().size(), actual.getMarriages().size());
        expected.getMarriages().sort(Comparator.comparing(r -> r.getWife().getName()));
        List<Marriage> actualMarriages = actual.getMarriages().stream().sorted(Comparator.comparing(r -> r.getWife().getName())).toList();
        for (int i = 0; i < expected.getMarriages().size(); i++) {
            assertMarriage(expected.getMarriages().get(i), actualMarriages.get(i));
        }

        assertEquals(expected.getParents().size(), actual.getParents().size());
        expected.getParents().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyPerson> actualParents = actual.getParents().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getParents().size(); i++) {
            assertEasyPerson(expected.getParents().get(i), actualParents.get(i));
        }

        assertEquals(expected.getChildren().size(), actual.getChildren().size());
        expected.getChildren().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyPerson> actualChildren = actual.getChildren().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getChildren().size(); i++) {
            assertEasyPerson(expected.getChildren().get(i), actualChildren.get(i));
        }

        assertEquals(expected.getPartners().size(), actual.getPartners().size());
        expected.getPartners().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<EasyPerson> actualPartners = actual.getPartners().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getPartners().size(); i++) {
            assertEasyPerson(expected.getPartners().get(i), actualPartners.get(i));
        }

        assertEquals(expected.getRevisions().size(), actual.getRevisions().size());
        expected.getRevisions().sort(Comparator.comparing(r -> r.getFullName().getName()));
        List<FamilyMember> actualRevisions = actual.getRevisions().stream().sorted(Comparator.comparing(r -> r.getFullName().getName())).toList();
        for (int i = 0; i < expected.getRevisions().size(); i++) {
            assertFamilyRevision(expected.getRevisions().get(i), actualRevisions.get(i));
        }
    }

    protected static void assertEasyPerson(EasyPerson expected, EasyPerson actual) {
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
        assertEquals(expected.getDeathDate(), actual.getDeathDate());
        assertEquals(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getSex(), actual.getSex());
    }

    private Person getPersonFromJson(String responseJson) throws JsonProcessingException {
        Person response = objectMapper.readValue(responseJson, Person.class);
        updateIds(response);
        return response;
    }

    private void updateIds(Person person) {
        if (person != null) {
            personIds.add(person.getId());
            if (person.getBirthLocality() != null && person.getBirthLocality().getId() != null) {
                localityIds.add(person.getBirthLocality().getId());
            }
            if (person.getDeathLocality() != null && person.getDeathLocality().getId() != null) {
                localityIds.add(person.getDeathLocality().getId());
            }
            if (person.getChristening() != null && person.getChristening().getId() != null) {
                christeningIds.add(person.getChristening().getId());
            }
            if (person.getDeath() != null && person.getDeath().getId() != null) {
                deathIds.add(person.getDeath().getId());
            }
            if (person.getMarriages() != null && !person.getMarriages().isEmpty()) {
                marriageIds.addAll(person.getMarriages().stream()
                        .map(m -> m.getId() != null ? m.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
            if (person.getRevisions() != null && !person.getRevisions().isEmpty()) {
                person.getRevisions().forEach(r -> {
                    if (r.getId() != null) {
                        familyRevisionIds.add(r.getId());
                    }
                    if (r.getPartner() != null && r.getPartner().getId() != null) {
                        familyRevisionIds.add(r.getPartner().getId());
                    }
                });
            }
            if (person.getChildren() != null && !person.getChildren().isEmpty()) {
                personIds.addAll(person.getChildren().stream()
                        .map(c -> c.getId() != null ? c.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
            if (person.getPartners() != null && !person.getPartners().isEmpty()) {
                personIds.addAll(person.getPartners().stream()
                        .map(p -> p.getId() != null ? p.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
            if (person.getParents() != null && !person.getParents().isEmpty()) {
                personIds.addAll(person.getParents().stream()
                        .map(p -> p.getId() != null ? p.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
        }
    }

    private void updateIds(genealogy.visualizer.entity.Person person) {
        if (person != null) {
            personIds.add(person.getId());
            if (person.getBirthLocality() != null && person.getBirthLocality().getId() != null) {
                localityIds.add(person.getBirthLocality().getId());
            }
            if (person.getDeathLocality() != null && person.getDeathLocality().getId() != null) {
                localityIds.add(person.getDeathLocality().getId());
            }
            if (person.getChristening() != null && person.getChristening().getId() != null) {
                christeningIds.add(person.getChristening().getId());
            }
            if (person.getDeath() != null && person.getDeath().getId() != null) {
                deathIds.add(person.getDeath().getId());
            }
            if (person.getMarriages() != null && !person.getMarriages().isEmpty()) {
                marriageIds.addAll(person.getMarriages().stream()
                        .map(m -> m.getId() != null ? m.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
            if (person.getRevisions() != null && !person.getRevisions().isEmpty()) {
                familyRevisionIds.addAll(person.getRevisions().stream()
                        .map(r -> r.getId() != null ? r.getId() : null)
                        .filter(Objects::nonNull).toList());
                person.getRevisions().forEach(r -> {
                    if (r.getPartner() != null && r.getPartner().getId() != null) {
                        familyRevisionIds.add(r.getPartner().getId());
                    }
                });
            }
            if (person.getChildren() != null && !person.getChildren().isEmpty()) {
                personIds.addAll(person.getChildren().stream()
                        .map(c -> c.getId() != null ? c.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
            if (person.getPartners() != null && !person.getPartners().isEmpty()) {
                personIds.addAll(person.getPartners().stream()
                        .map(p -> p.getId() != null ? p.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
            if (person.getParents() != null && !person.getParents().isEmpty()) {
                personIds.addAll(person.getParents().stream()
                        .map(p -> p.getId() != null ? p.getId() : null)
                        .filter(Objects::nonNull).toList());
            }
        }
    }
}
