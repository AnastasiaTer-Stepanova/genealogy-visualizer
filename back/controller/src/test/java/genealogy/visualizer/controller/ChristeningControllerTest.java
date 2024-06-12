package genealogy.visualizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.GodParent;
import genealogy.visualizer.api.model.Locality;
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
import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Locality localitySave = generator.nextObject(Locality.class);
        christeningSave.setLocality(localitySave);
        List<GodParent> godParentsSave = generator.objects(GodParent.class, generator.nextInt(5, 10)).toList();
        godParentsSave.forEach(gp -> gp.setLocality(generator.nextBoolean() ? localitySave : generator.nextObject(Locality.class)));
        christeningSave.setGodParents(godParentsSave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(christeningSave));
        Christening response = getChristeningFromJson(responseJson);
        assertNotNull(response);
        assertChristening(response, christeningSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Christening responseGet = getChristeningFromJson(responseJsonGet);
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
        Locality localityUpdate = generator.nextObject(Locality.class);
        christeningUpdate.setLocality(localityUpdate);
        List<GodParent> godParentsUpdate = new ArrayList<>(generator.objects(GodParent.class, generator.nextInt(2, 3)).toList());
        godParentsUpdate.forEach(gp -> gp.setLocality(generator.nextBoolean() ? localityUpdate : generator.nextObject(Locality.class)));
        christeningExist.getGodParents().forEach(gp -> {
            if (generator.nextBoolean()) {
                godParentsUpdate.add(godParentMapper.toDTO(gp));
            }
        });
        christeningUpdate.setGodParents(godParentsUpdate);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(christeningUpdate));
        Christening response = getChristeningFromJson(responseJson);
        assertNotNull(response);
        assertChristening(response, christeningUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Christening responseGet = getChristeningFromJson(responseJsonGet);
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
        Christening response = getChristeningFromJson(responseJson);
        assertNotNull(response);
        assertChristening(response, christeningUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Christening responseGet = getChristeningFromJson(responseJsonGet);
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
        Christening response = getChristeningFromJson(responseJson);
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
                .christeningYear(1850);
        List<genealogy.visualizer.entity.Christening> christeningsSave = generator.objects(genealogy.visualizer.entity.Christening.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Christening christening : christeningsSave) {
            christening.setPerson(null);
            christening.setLocality(null);
            christening.setGodParents(null);
            if (generator.nextBoolean()) {
                christening.setArchiveDocument(archiveDocumentExisting);
                christening.setSex(genealogy.visualizer.entity.enums.Sex.FEMALE);
                christening.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getName() : filter.getName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                LocalDate christeningDate = LocalDate.of(filter.getChristeningYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
                christening.setChristeningDate(christeningDate);
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
                archiveDocumentIds.add(archiveDocumentExist.getId());
                christening.setArchiveDocument(archiveDocumentExist);
            }
        }
        List<genealogy.visualizer.entity.Christening> christeningExist = christeningRepository.saveAllAndFlush(christeningsSave);
        christeningExist.forEach(ad -> christeningIds.add(ad.getId()));
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

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        christeningIds.forEach(id -> christeningDAO.delete(id));
        personRepository.deleteAllById(personIds);
        localityRepository.deleteAllById(localityIds);
        archiveDocumentRepository.deleteAllById(archiveDocumentIds);
        super.tearDown();
    }

    private Christening getChristeningFromJson(String responseJson) throws JsonProcessingException {
        Christening response = objectMapper.readValue(responseJson, Christening.class);
        if (response != null) {
            if (response.getArchiveDocument() != null) {
                archiveDocumentIds.add(response.getArchiveDocument().getId());
            }
            if (response.getLocality() != null) {
                localityIds.add(response.getLocality().getId());
            }
            if (response.getPerson() != null) {
                personIds.add(response.getPerson().getId());
            }
            if (response.getGodParents() != null) {
                response.getGodParents().forEach(gp -> {
                    if (gp.getLocality() != null) {
                        localityIds.add(gp.getLocality().getId());
                    }
                });
            }
            christeningIds.add(response.getId());
        }
        return response;
    }

    static void assertChristening(EasyChristening christening1, EasyChristening christening2) {
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
        if (christening2.getGodParents() != null) {
            assertEquals(christening1.getGodParents().size(), christening2.getGodParents().size());
            List<GodParent> godParents1 = christening1.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<GodParent> godParents2 = christening2.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < godParents1.size(); i++) {
                assertGodParent(godParents1.get(i), godParents2.get(i));
            }
        }
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
        if (christening2.getGodParents() != null) {
            assertEquals(christening1.getGodParents().size(), christening2.getGodParents().size());
            List<GodParent> godParents1 = christening1.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.model.GodParent> godParents2 = christening2.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < godParents1.size(); i++) {
                assertGodParent(godParents1.get(i), godParents2.get(i));
            }
        }
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
        genealogy.visualizer.entity.Archive archiveSave = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveSave.setArchiveDocuments(null);
        genealogy.visualizer.entity.Archive archiveExist = archiveRepository.saveAndFlush(archiveSave);
        archiveIds.add(archiveExist.getId());

        genealogy.visualizer.entity.ArchiveDocument archiveDocumentSave = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        archiveDocumentSave.setArchive(archiveExist);
        archiveDocumentSave.setPreviousRevisions(Collections.emptyList());
        archiveDocumentSave.setFamilyRevisions(Collections.emptyList());
        archiveDocumentSave.setChristenings(Collections.emptyList());
        archiveDocumentSave.setMarriages(Collections.emptyList());
        archiveDocumentSave.setDeaths(Collections.emptyList());
        archiveDocumentSave.setNextRevision(null);
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = archiveDocumentRepository.saveAndFlush(archiveDocumentSave);
        archiveDocumentIds.add(archiveDocumentExist.getId());

        genealogy.visualizer.entity.Christening christeningSave = generator.nextObject(genealogy.visualizer.entity.Christening.class);
        christeningSave.setArchiveDocument(archiveDocumentExist);

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
        genealogy.visualizer.entity.Person personExist = personRepository.saveAndFlush(personSave);
        christeningSave.setPerson(personExist);
        personIds.add(personExist.getId());

        List<genealogy.visualizer.entity.model.GodParent> godParentsSave = generator.objects(genealogy.visualizer.entity.model.GodParent.class, generator.nextInt(5, 10)).toList();
        godParentsSave.forEach(gp -> gp.setLocality(localityExisting));
        christeningSave.setGodParents(godParentsSave);
        christeningSave.setLocality(localityExisting);
        genealogy.visualizer.entity.Christening christeningExist = christeningRepository.save(christeningSave);
        christeningIds.add(christeningExist.getId());
        return christeningExist;
    }
}
