package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.GodParent;
import genealogy.visualizer.api.model.Sex;
import genealogy.visualizer.mapper.GodParentMapper;
import genealogy.visualizer.service.ChristeningDAO;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

class ChristeningControllerTest extends IntegrationTest {

    @Autowired
    ChristeningDAO christeningDAO;

    @Autowired
    GodParentMapper godParentMapper;

    private static final String PATH = "/christening";

    @Test
    void saveTest() throws Exception {
        Christening christeningSave = generator.nextObject(Christening.class);
        EasyArchiveDocument archiveDocumentSave = generator.nextObject(EasyArchiveDocument.class);
        christeningSave.setArchiveDocument(archiveDocumentSave);
        EasyPerson personSave = generator.nextObject(EasyPerson.class);
        christeningSave.setPerson(personSave);
        EasyLocality localitySave = generator.nextObject(EasyLocality.class);
        christeningSave.setLocality(localitySave);
        List<GodParent> godParentsSave = generator.objects(GodParent.class, generator.nextInt(5, 10)).toList();
        godParentsSave.forEach(gp -> gp.setLocality(generator.nextBoolean() ? localitySave : generator.nextObject(EasyLocality.class)));
        christeningSave.setGodParents(godParentsSave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(christeningSave));
        Christening response = objectMapper.readValue(responseJson, Christening.class);
        assertNotNull(response);
        assertChristening(response, christeningSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Christening responseGet = objectMapper.readValue(responseJsonGet, Christening.class);
        assertNotNull(responseGet);
        assertChristening(responseGet, christeningSave);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = generateRandomExistChristening();
        Christening christeningUpdate = generator.nextObject(Christening.class);
        christeningUpdate.setId(christeningExist.getId());
        EasyArchiveDocument archiveDocumentUpdate = generator.nextObject(EasyArchiveDocument.class);
        christeningUpdate.setArchiveDocument(archiveDocumentUpdate);
        EasyPerson personUpdate = generator.nextObject(EasyPerson.class);
        christeningUpdate.setPerson(personUpdate);
        EasyLocality localityUpdate = generator.nextObject(EasyLocality.class);
        christeningUpdate.setLocality(localityUpdate);
        List<GodParent> godParentsUpdate = new ArrayList<>(generator.objects(GodParent.class, generator.nextInt(2, 3)).toList());
        godParentsUpdate.forEach(gp -> gp.setLocality(generator.nextBoolean() ? localityUpdate : generator.nextObject(EasyLocality.class)));
        christeningExist.getGodParents().forEach(gp -> {
            if (generator.nextBoolean()) {
                godParentsUpdate.add(godParentMapper.toDTO(gp));
            }
        });
        christeningUpdate.setGodParents(godParentsUpdate);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(christeningUpdate));
        Christening response = objectMapper.readValue(responseJson, Christening.class);
        assertNotNull(response);
        assertChristening(response, christeningUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Christening responseGet = objectMapper.readValue(responseJsonGet, Christening.class);
        assertNotNull(responseGet);
        assertChristening(responseGet, christeningUpdate);
    }

    @Test
    void updateWithNullFieldTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = generateRandomExistChristening();
        Christening christeningUpdate = generator.nextObject(Christening.class);
        christeningUpdate.setId(christeningExist.getId());
        christeningUpdate.setArchiveDocument(null);
        christeningUpdate.setPerson(null);
        christeningUpdate.setLocality(null);
        christeningUpdate.setGodParents(Collections.emptyList());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(christeningUpdate));
        Christening response = objectMapper.readValue(responseJson, Christening.class);
        assertNotNull(response);
        assertChristening(response, christeningUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Christening responseGet = objectMapper.readValue(responseJsonGet, Christening.class);
        assertNotNull(responseGet);
        assertChristening(responseGet, christeningUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = generateRandomExistChristening();
        String responseJson = deleteRequest(PATH + "/" + christeningExist.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(christeningRepository.findById(christeningExist.getId()).isEmpty());
        assertFalse(archiveDocumentRepository.findById(christeningExist.getArchiveDocument().getId()).isEmpty());
        assertFalse(localityRepository.findById(christeningExist.getLocality().getId()).isEmpty());
        assertFalse(personRepository.findById(christeningExist.getPerson().getId()).isEmpty());
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = generateRandomExistChristening();
        String responseJson = getRequest(PATH + "/" + christeningExist.getId());
        Christening response = objectMapper.readValue(responseJson, Christening.class);
        assertNotNull(response);
        assertEquals(response.getId(), christeningExist.getId());
        assertChristening(response, christeningExist);
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        ChristeningFilter filter = new ChristeningFilter()
                .name("Иван")
                .archiveDocumentId(archiveDocumentExisting.getId())
                .sex(Sex.FEMALE)
                .christeningYear(1850)
                .isFindWithHavePerson(false);
        List<genealogy.visualizer.entity.Christening> christeningsSave = generator.objects(genealogy.visualizer.entity.Christening.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Christening christening : christeningsSave) {
            christening.setLocality(null);
            christening.setGodParents(null);
            christening.setPerson(null);
            if (generator.nextBoolean()) {
                christening.setPerson(null);
                christening.setArchiveDocument(archiveDocumentExisting);
                christening.setSex(genealogy.visualizer.entity.enums.Sex.FEMALE);
                christening.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getName() : filter.getName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                LocalDate christeningDate = LocalDate.of(filter.getChristeningYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
                christening.setChristeningDate(christeningDate);
                count++;
            } else {
                christening.setPerson(getEmptySavedPerson());
                christening.setArchiveDocument(getEmptySavedArchiveDocument());
            }
        }
        List<genealogy.visualizer.entity.Christening> christeningExist = christeningRepository.saveAllAndFlush(christeningsSave);
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyChristening> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyChristening.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyChristening::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Christening christening : christeningExist) {
            if (christening.getName().toLowerCase().contains(filter.getName().toLowerCase()) &&
                    christening.getArchiveDocument().getId().equals(filter.getArchiveDocumentId()) &&
                    christening.getSex().name().equals(filter.getSex().name()) &&
                    christening.getChristeningDate().getYear() == filter.getChristeningYear()) {
                assertTrue(findIds.contains(christening.getId()));
            }
        }
    }

    @Test
    void saveUnauthorizedTest() throws Exception {
        Christening object = generator.nextObject(Christening.class);
        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void updateUnauthorizedTest() throws Exception {
        Christening object = generator.nextObject(Christening.class);
        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        Christening object = generator.nextObject(Christening.class);
        deleteUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        christeningRepository.deleteAll();
        personRepository.deleteAll();
        super.tearDown();
    }

    static void assertChristening(EasyChristening christening1, EasyChristening christening2) {
        if (christening1 == null || christening2 == null) {
            assertNull(christening1);
            assertNull(christening2);
            return;
        }
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex(), christening2.getSex());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
        assertEquals(christening1.getComment(), christening2.getComment());
        assertFullName(christening1.getFather(), christening2.getFather());
        assertFullName(christening1.getMother(), christening2.getMother());
    }

    static void assertChristening(EasyChristening christening1, genealogy.visualizer.entity.Christening christening2) {
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex().name(), christening2.getSex().name());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
        assertEquals(christening1.getComment(), christening2.getComment());
        assertFullName(christening1.getFather(), christening2.getFather());
        assertFullName(christening1.getMother(), christening2.getMother());
    }

    static void assertChristening(Christening christening1, Christening christening2) {
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex().name(), christening2.getSex().name());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
        assertEquals(christening1.getComment(), christening2.getComment());
        assertFullName(christening1.getFather(), christening2.getFather());
        assertFullName(christening1.getMother(), christening2.getMother());
        assertLocality(christening1.getLocality(), christening2.getLocality());
        if (christening2.getGodParents() != null) {
            assertEquals(christening1.getGodParents().size(), christening2.getGodParents().size());
            List<GodParent> godParents1 = christening1.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<GodParent> godParents2 = christening2.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < godParents1.size(); i++) {
                assertGodParent(godParents1.get(i), godParents2.get(i));
            }
        }
        assertPerson(christening1.getPerson(), christening2.getPerson());
        assertArchiveDocument(christening1.getArchiveDocument(), christening2.getArchiveDocument());
        assertLocality(christening1.getLocality(), christening2.getLocality());
    }

    static void assertChristening(Christening christening1, genealogy.visualizer.entity.Christening christening2) {
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex().name(), christening2.getSex().name());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
        assertEquals(christening1.getComment(), christening2.getComment());
        assertFullName(christening1.getFather(), christening2.getFather());
        assertFullName(christening1.getMother(), christening2.getMother());
        assertLocality(christening1.getLocality(), christening2.getLocality());
        if (christening2.getGodParents() != null) {
            assertEquals(christening1.getGodParents().size(), christening2.getGodParents().size());
            List<GodParent> godParents1 = christening1.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.model.GodParent> godParents2 = christening2.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < godParents1.size(); i++) {
                assertGodParent(godParents1.get(i), godParents2.get(i));
            }
        }
        assertPerson(christening1.getPerson(), christening2.getPerson());
        assertArchiveDocument(christening1.getArchiveDocument(), christening2.getArchiveDocument());
        assertLocality(christening1.getLocality(), christening2.getLocality());
    }

    private static void assertGodParent(GodParent godParent1, GodParent godParent2) {
        assertNotNull(godParent1);
        assertNotNull(godParent2);
        assertFullName(godParent1.getFullName(), godParent2.getFullName());
        assertFullName(godParent1.getRelative(), godParent2.getRelative());
        assertLocality(godParent1.getLocality(), godParent2.getLocality());
    }

    private static void assertGodParent(GodParent godParent1, genealogy.visualizer.entity.model.GodParent godParent2) {
        assertNotNull(godParent1);
        assertNotNull(godParent2);
        assertFullName(godParent1.getFullName(), godParent2.getFullName());
        assertFullName(godParent1.getRelative(), godParent2.getRelative());
        assertLocality(godParent1.getLocality(), godParent2.getLocality());
    }

    private genealogy.visualizer.entity.Christening generateRandomExistChristening() {

        genealogy.visualizer.entity.Christening christeningSave = generator.nextObject(genealogy.visualizer.entity.Christening.class);
        christeningSave.setArchiveDocument(getEmptySavedArchiveDocument());

        christeningSave.setPerson(getEmptySavedPerson());

        List<genealogy.visualizer.entity.model.GodParent> godParentsSave = generator.objects(genealogy.visualizer.entity.model.GodParent.class, generator.nextInt(5, 10)).toList();
        godParentsSave.forEach(gp -> gp.setLocality(localityExisting));
        christeningSave.setGodParents(godParentsSave);
        christeningSave.setLocality(localityExisting);
        return christeningRepository.save(christeningSave);
    }

    private genealogy.visualizer.entity.Person getEmptySavedPerson() {
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
        return personRepository.saveAndFlush(personSave);
    }

    private genealogy.visualizer.entity.ArchiveDocument getEmptySavedArchiveDocument() {
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

        return archiveDocumentRepository.saveAndFlush(archiveDocumentSave);
    }
}
