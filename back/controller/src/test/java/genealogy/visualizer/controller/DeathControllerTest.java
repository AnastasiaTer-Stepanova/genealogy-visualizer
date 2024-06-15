package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.FullNameFilter;
import genealogy.visualizer.service.DeathDAO;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ArchiveDocumentControllerTest.assertArchiveDocument;
import static genealogy.visualizer.controller.LocalityControllerTest.assertLocality;
import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeathControllerTest extends IntegrationTest {

    @Autowired
    DeathDAO deathDAO;

    private static final String PATH = "/death";

    @Test
    void saveTest() throws Exception {
        Death deathSave = generator.nextObject(Death.class);
        EasyArchiveDocument archiveDocumentSave = generator.nextObject(EasyArchiveDocument.class);
        deathSave.setArchiveDocument(archiveDocumentSave);
        EasyPerson personSave = generator.nextObject(EasyPerson.class);
        deathSave.setPerson(personSave);
        EasyLocality localitySave = generator.nextObject(EasyLocality.class);
        deathSave.setLocality(localitySave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(deathSave));
        Death response = objectMapper.readValue(responseJson, Death.class);
        assertNotNull(response);
        assertDeath(response, deathSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Death responseGet = objectMapper.readValue(responseJsonGet, Death.class);
        assertNotNull(responseGet);
        assertDeath(responseGet, deathSave);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Death deathExist = generateRandomExistDeath();
        Death deathUpdate = generator.nextObject(Death.class);
        deathUpdate.setId(deathExist.getId());
        EasyArchiveDocument archiveDocumentUpdate = generator.nextObject(EasyArchiveDocument.class);
        deathUpdate.setArchiveDocument(archiveDocumentUpdate);
        EasyPerson personUpdate = generator.nextObject(EasyPerson.class);
        deathUpdate.setPerson(personUpdate);
        EasyLocality localityUpdate = generator.nextObject(EasyLocality.class);
        deathUpdate.setLocality(localityUpdate);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(deathUpdate));
        Death response = objectMapper.readValue(responseJson, Death.class);
        assertNotNull(response);
        assertDeath(response, deathUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Death responseGet = objectMapper.readValue(responseJsonGet, Death.class);
        assertNotNull(responseGet);
        assertDeath(responseGet, deathUpdate);
    }

    @Test
    void updateWithNullFieldTest() throws Exception {
        genealogy.visualizer.entity.Death deathExist = generateRandomExistDeath();
        Death deathUpdate = generator.nextObject(Death.class);
        deathUpdate.setId(deathExist.getId());
        deathUpdate.setArchiveDocument(null);
        deathUpdate.setPerson(null);
        deathUpdate.setLocality(null);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(deathUpdate));
        Death response = objectMapper.readValue(responseJson, Death.class);
        assertNotNull(response);
        assertDeath(response, deathUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Death responseGet = objectMapper.readValue(responseJsonGet, Death.class);
        assertNotNull(responseGet);
        assertDeath(responseGet, deathUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Death deathExist = generateRandomExistDeath();
        String responseJson = deleteRequest(PATH + "/" + deathExist.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(deathRepository.findById(deathExist.getId()).isEmpty());
        assertFalse(archiveDocumentRepository.findById(deathExist.getArchiveDocument().getId()).isEmpty());
        assertFalse(localityRepository.findById(deathExist.getLocality().getId()).isEmpty());
        assertFalse(personRepository.findById(deathExist.getPerson().getId()).isEmpty());
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Death deathExist = generateRandomExistDeath();
        String responseJson = getRequest(PATH + "/" + deathExist.getId());
        Death response = objectMapper.readValue(responseJson, Death.class);
        assertNotNull(response);
        assertEquals(response.getId(), deathExist.getId());
        assertDeath(response, deathExist);
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        FullNameFilter fullNameFilter = new FullNameFilter()
                .name("Иван")
                .surname("Иванович")
                .lastName("Иванов");
        DeathFilter filter = new DeathFilter()
                .fullName(fullNameFilter)
                .archiveDocumentId(archiveDocumentExisting.getId())
                .deathYear(1850);
        List<genealogy.visualizer.entity.Death> deathsSave = generator.objects(genealogy.visualizer.entity.Death.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Death death : deathsSave) {
            death.setPerson(null);
            death.setLocality(null);
            if (generator.nextBoolean()) {
                death.setArchiveDocument(archiveDocumentExisting);
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
                death.setFullName(fullName);
                LocalDate date = LocalDate.of(filter.getDeathYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
                death.setDate(date);
                count++;
            } else {
                genealogy.visualizer.entity.ArchiveDocument archiveDocumentSave = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
                archiveDocumentSave.setArchive(null);
                archiveDocumentSave.setPreviousRevisions(Collections.emptyList());
                archiveDocumentSave.setFamilyRevisions(Collections.emptyList());
                archiveDocumentSave.setChristenings(Collections.emptyList());
                archiveDocumentSave.setMarriages(Collections.emptyList());
                archiveDocumentSave.setDeaths(Collections.emptyList());
                archiveDocumentSave.setNextRevision(null);
                genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = archiveDocumentRepository.saveAndFlush(archiveDocumentSave);
                death.setArchiveDocument(archiveDocumentExist);
            }
        }
        List<genealogy.visualizer.entity.Death> deathsExist = deathRepository.saveAllAndFlush(deathsSave);
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyDeath> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyDeath.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyDeath::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Death death : deathsExist) {
            if (death.getFullName().getName().toLowerCase().contains(filter.getFullName().getName().toLowerCase()) &&
                    death.getFullName().getSurname().toLowerCase().contains(filter.getFullName().getSurname().toLowerCase()) &&
                    death.getFullName().getLastName().toLowerCase().contains(filter.getFullName().getLastName().toLowerCase()) &&
                    death.getArchiveDocument().getId().equals(filter.getArchiveDocumentId()) &&
                    death.getDate().getYear() == filter.getDeathYear()) {
                assertTrue(findIds.contains(death.getId()));
            }
        }
    }

    @Test
    void saveUnauthorizedTest() throws Exception {
        Death object = generator.nextObject(Death.class);
        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void updateUnauthorizedTest() throws Exception {
        Death object = generator.nextObject(Death.class);
        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        Death object = generator.nextObject(Death.class);
        deleteUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        deathRepository.deleteAll();
        personRepository.deleteAll();
        super.tearDown();
    }

    static void assertDeath(EasyDeath death1, genealogy.visualizer.entity.Death death2) {
        assertNotNull(death1);
        assertNotNull(death2);
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
        assertFullName(death1.getRelative(), death2.getRelative());
        assertAge(death1.getAge(), death2.getAge());
        assertEquals(death1.getCause(), death2.getCause());
        assertEquals(death1.getComment(), death2.getComment());
        assertEquals(death1.getBurialPlace(), death2.getBurialPlace());
    }

    static void assertDeath(Death death1, Death death2) {
        assertNotNull(death1);
        assertNotNull(death2);
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
        assertFullName(death1.getRelative(), death2.getRelative());
        assertAge(death1.getAge(), death2.getAge());
        assertEquals(death1.getCause(), death2.getCause());
        assertEquals(death1.getComment(), death2.getComment());
        assertEquals(death1.getBurialPlace(), death2.getBurialPlace());
        assertLocality(death1.getLocality(), death2.getLocality());
        assertPerson(death1.getPerson(), death2.getPerson());
        assertArchiveDocument(death1.getArchiveDocument(), death2.getArchiveDocument());
    }

    static void assertDeath(Death death1, genealogy.visualizer.entity.Death death2) {
        assertNotNull(death1);
        assertNotNull(death2);
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
        assertFullName(death1.getRelative(), death2.getRelative());
        assertAge(death1.getAge(), death2.getAge());
        assertEquals(death1.getCause(), death2.getCause());
        assertEquals(death1.getComment(), death2.getComment());
        assertEquals(death1.getBurialPlace(), death2.getBurialPlace());
        assertLocality(death1.getLocality(), death2.getLocality());
        assertPerson(death1.getPerson(), death2.getPerson());
        assertArchiveDocument(death1.getArchiveDocument(), death2.getArchiveDocument());
    }

    static void assertDeath(EasyDeath death1, EasyDeath death2) {
        if (death1 == null || death2 == null) {
            assertNull(death1);
            assertNull(death2);
            return;
        }
        assertNotNull(death1);
        assertNotNull(death2);
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
        assertFullName(death1.getRelative(), death2.getRelative());
        assertAge(death1.getAge(), death2.getAge());
        assertEquals(death1.getCause(), death2.getCause());
        assertEquals(death1.getComment(), death2.getComment());
        assertEquals(death1.getBurialPlace(), death2.getBurialPlace());
    }

    private genealogy.visualizer.entity.Death generateRandomExistDeath() {
        genealogy.visualizer.entity.Archive archiveSave = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveSave.setArchiveDocuments(null);

        genealogy.visualizer.entity.ArchiveDocument archiveDocumentSave = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        archiveDocumentSave.setArchive(archiveRepository.saveAndFlush(archiveSave));
        archiveDocumentSave.setPreviousRevisions(Collections.emptyList());
        archiveDocumentSave.setFamilyRevisions(Collections.emptyList());
        archiveDocumentSave.setChristenings(Collections.emptyList());
        archiveDocumentSave.setMarriages(Collections.emptyList());
        archiveDocumentSave.setDeaths(Collections.emptyList());
        archiveDocumentSave.setNextRevision(null);

        genealogy.visualizer.entity.Death deathSave = generator.nextObject(genealogy.visualizer.entity.Death.class);
        deathSave.setArchiveDocument(archiveDocumentRepository.saveAndFlush(archiveDocumentSave));

        genealogy.visualizer.entity.Person personSave = generator.nextObject(genealogy.visualizer.entity.Person.class);
        personSave.setChristening(null);
        personSave.setPartners(Collections.emptyList());
        personSave.setChildren(Collections.emptyList());
        personSave.setRevisions(Collections.emptyList());
        personSave.setMarriages(Collections.emptyList());
        personSave.setParents(Collections.emptyList());
        personSave.setDeath(null);
        personSave.setDeathLocality(localityExisting);
        personSave.setBirthLocality(localityExisting);
        deathSave.setPerson(personRepository.saveAndFlush(personSave));

        deathSave.setLocality(localityExisting);
        return deathRepository.save(deathSave);
    }

}
