package genealogy.visualizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.api.model.Witness;
import genealogy.visualizer.mapper.WitnessMapper;
import genealogy.visualizer.service.MarriageDAO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarriageControllerTest extends IntegrationTest {

    @Autowired
    MarriageDAO marriageDAO;

    @Autowired
    WitnessMapper witnessMapper;

    private static final String PATH = "/marriage";

    @Test
    void saveTest() throws Exception {
        Marriage marriageSave = generator.nextObject(Marriage.class);
        EasyArchiveDocument archiveDocumentSave = generator.nextObject(EasyArchiveDocument.class);
        marriageSave.setArchiveDocument(archiveDocumentSave);
        List<EasyPerson> personsSave = generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList();
        marriageSave.setPersons(personsSave);
        EasyLocality localitySave = generator.nextObject(EasyLocality.class);
        marriageSave.setHusbandLocality(localitySave);
        marriageSave.setWifeLocality(localityMapper.toDTO(localityExisting));
        List<Witness> witnessesSave = generator.objects(Witness.class, generator.nextInt(5, 10)).toList();
        witnessesSave.forEach(w -> w.setLocality(generator.nextBoolean() ? localitySave : generator.nextObject(EasyLocality.class)));
        marriageSave.setWitnesses(witnessesSave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(marriageSave));
        Marriage response = getMarriageFromJson(responseJson);
        assertNotNull(response);
        assertMarriage(response, marriageSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Marriage responseGet = getMarriageFromJson(responseJsonGet);
        assertNotNull(responseGet);
        assertMarriage(responseGet, marriageSave);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = generateRandomExistMarriage();
        Marriage marriageUpdate = generator.nextObject(Marriage.class);
        marriageUpdate.setId(marriageExist.getId());
        EasyArchiveDocument archiveDocumentUpdate = generator.nextObject(EasyArchiveDocument.class);
        marriageUpdate.setArchiveDocument(archiveDocumentUpdate);
        List<EasyPerson> personsUpdate = generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList();
        marriageUpdate.setPersons(personsUpdate);
        EasyLocality localityUpdate = generator.nextObject(EasyLocality.class);
        marriageUpdate.setHusbandLocality(localityUpdate);
        marriageUpdate.setWifeLocality(localityUpdate);
        List<Witness> witnessesUpdate = new ArrayList<>(generator.objects(Witness.class, generator.nextInt(5, 10)).toList());
        witnessesUpdate.forEach(w -> w.setLocality(generator.nextBoolean() ? localityUpdate : generator.nextObject(EasyLocality.class)));
        marriageExist.getWitnesses().forEach(w -> {
            if (generator.nextBoolean()) {
                witnessesUpdate.add(witnessMapper.toDTO(w));
            }
        });
        marriageUpdate.setWitnesses(witnessesUpdate);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(marriageUpdate));
        Marriage response = getMarriageFromJson(responseJson);
        assertNotNull(response);
        assertMarriage(response, marriageUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Marriage responseGet = getMarriageFromJson(responseJsonGet);
        assertNotNull(responseGet);
        assertMarriage(responseGet, marriageUpdate);
    }

    @Test
    void updateWithNullFieldTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = generateRandomExistMarriage();
        Marriage marriageUpdate = generator.nextObject(Marriage.class);
        marriageUpdate.setId(marriageExist.getId());
        marriageUpdate.setArchiveDocument(null);
        marriageUpdate.setWifeLocality(null);
        marriageUpdate.setHusbandLocality(null);
        marriageUpdate.setPersons(Collections.emptyList());
        marriageUpdate.setWitnesses(Collections.emptyList());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(marriageUpdate));
        Marriage response = getMarriageFromJson(responseJson);
        assertNotNull(response);
        assertMarriage(response, marriageUpdate);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Marriage responseGet = getMarriageFromJson(responseJsonGet);
        assertNotNull(responseGet);
        assertMarriage(responseGet, marriageUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = generateRandomExistMarriage();
        String responseJson = deleteRequest(PATH + "/" + marriageExist.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(marriageRepository.findById(marriageExist.getId()).isEmpty());
        assertFalse(archiveDocumentRepository.findById(marriageExist.getArchiveDocument().getId()).isEmpty());
        assertFalse(localityRepository.findById(marriageExist.getHusbandLocality().getId()).isEmpty());
        assertFalse(localityRepository.findById(marriageExist.getWifeLocality().getId()).isEmpty());
        marriageExist.getPersons().forEach(person -> assertFalse(personRepository.findById(person.getId()).isEmpty()));
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = generateRandomExistMarriage();
        String responseJson = getRequest(PATH + "/" + marriageExist.getId());
        Marriage response = getMarriageFromJson(responseJson);
        assertNotNull(response);
        assertEquals(response.getId(), marriageExist.getId());
        assertMarriage(response, marriageExist);
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        MarriageFilter filter = new MarriageFilter()
                .wifeName("Оля")
                .husbandName("Иван")
                .archiveDocumentId(archiveDocumentExisting.getId())
                .marriageYear(1850);
        List<genealogy.visualizer.entity.Marriage> marriagesSave = generator.objects(genealogy.visualizer.entity.Marriage.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Marriage marriage : marriagesSave) {
            marriage.setPersons(Collections.emptyList());
            marriage.setWifeLocality(null);
            marriage.setHusbandLocality(null);
            marriage.setWitnesses(null);
            if (generator.nextBoolean()) {
                marriage.setArchiveDocument(archiveDocumentExisting);
                marriage.getHusband().setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getHusbandName() : filter.getHusbandName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                marriage.getWife().setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getWifeName() : filter.getWifeName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                LocalDate marriageDate = LocalDate.of(filter.getMarriageYear(), generator.nextInt(1, 12), generator.nextInt(1, 28));
                marriage.setDate(marriageDate);
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
                marriage.setArchiveDocument(archiveDocumentExist);
            }
        }
        List<genealogy.visualizer.entity.Marriage> marriagesExist = marriageRepository.saveAllAndFlush(marriagesSave);
        marriagesExist.forEach(m -> marriageIds.add(m.getId()));
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyMarriage> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyMarriage.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyMarriage::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Marriage marriage : marriagesExist) {
            if (marriage.getHusband().getName().toLowerCase().contains(filter.getHusbandName().toLowerCase()) &&
                    marriage.getWife().getName().toLowerCase().contains(filter.getWifeName().toLowerCase()) &&
                    marriage.getArchiveDocument().getId().equals(filter.getArchiveDocumentId()) &&
                    marriage.getDate().getYear() == filter.getMarriageYear()) {
                assertTrue(findIds.contains(marriage.getId()));
            }
        }
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        marriageIds.forEach(id -> marriageDAO.delete(id));
        personRepository.deleteAllById(personIds);
        localityRepository.deleteAllById(localityIds);
        archiveDocumentRepository.deleteAllById(archiveDocumentIds);
        super.tearDown();
    }

    private Marriage getMarriageFromJson(String responseJson) throws JsonProcessingException {
        Marriage response = objectMapper.readValue(responseJson, Marriage.class);
        if (response != null) {
            if (response.getArchiveDocument() != null) {
                archiveDocumentIds.add(response.getArchiveDocument().getId());
            }
            if (response.getWifeLocality() != null) {
                localityIds.add(response.getWifeLocality().getId());
            }
            if (response.getHusbandLocality() != null) {
                localityIds.add(response.getHusbandLocality().getId());
            }
            if (response.getPersons() != null) {
                response.getPersons().forEach(p -> personIds.add(p.getId()));
            }
            if (response.getWitnesses() != null) {
                response.getWitnesses().forEach(w -> {
                    if (w.getLocality() != null) {
                        localityIds.add(w.getLocality().getId());
                    }
                });
            }
            marriageIds.add(response.getId());
        }
        return response;
    }

    static void assertMarriage(Marriage marriage1, Marriage marriage2) {
        assertNotNull(marriage1);
        assertNotNull(marriage2);
        assertEquals(marriage1.getDate(), marriage2.getDate());
        assertFullName(marriage1.getHusband(), marriage2.getHusband());
        assertFullName(marriage1.getWife(), marriage2.getWife());
        assertFullName(marriage1.getWifesFather(), marriage2.getWifesFather());
        assertFullName(marriage1.getHusbandsFather(), marriage2.getHusbandsFather());
        assertEquals(marriage1.getHusbandMarriageNumber(), marriage2.getHusbandMarriageNumber());
        assertEquals(marriage1.getWifeMarriageNumber(), marriage2.getWifeMarriageNumber());
        assertEquals(marriage1.getComment(), marriage2.getComment());
        assertAge(marriage1.getWifeAge(), marriage2.getWifeAge());
        assertAge(marriage1.getHusbandAge(), marriage2.getHusbandAge());
        assertLocality(marriage1.getWifeLocality(), marriage2.getWifeLocality());
        assertLocality(marriage1.getHusbandLocality(), marriage2.getHusbandLocality());
        assertArchiveDocument(marriage1.getArchiveDocument(), marriage2.getArchiveDocument());
        if (marriage2.getWitnesses() != null) {
            assertEquals(marriage1.getWitnesses().size(), marriage2.getWitnesses().size());
            List<Witness> witnesses1 = marriage1.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<Witness> witnesses2 = marriage2.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < witnesses1.size(); i++) {
                assertWitness(witnesses1.get(i), witnesses2.get(i));
            }
        }
        if (marriage2.getPersons() != null) {
            assertEquals(marriage1.getPersons().size(), marriage2.getPersons().size());
            List<EasyPerson> persons1 = marriage1.getPersons().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<EasyPerson> persons2 = marriage2.getPersons().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < persons1.size(); i++) {
                assertPerson(persons1.get(i), persons2.get(i));
            }
        }
    }

    static void assertMarriage(Marriage marriage1, genealogy.visualizer.entity.Marriage marriage2) {
        assertNotNull(marriage1);
        assertNotNull(marriage2);
        assertEquals(marriage1.getDate(), marriage2.getDate());
        assertFullName(marriage1.getHusband(), marriage2.getHusband());
        assertFullName(marriage1.getWife(), marriage2.getWife());
        assertFullName(marriage1.getWifesFather(), marriage2.getWifesFather());
        assertFullName(marriage1.getHusbandsFather(), marriage2.getHusbandsFather());
        assertEquals(marriage1.getHusbandMarriageNumber(), marriage2.getHusbandMarriageNumber().intValue());
        assertEquals(marriage1.getWifeMarriageNumber(), marriage2.getWifeMarriageNumber().intValue());
        assertEquals(marriage1.getComment(), marriage2.getComment());
        assertAge(marriage1.getWifeAge(), marriage2.getWifeAge());
        assertAge(marriage1.getHusbandAge(), marriage2.getHusbandAge());
        assertLocality(marriage1.getWifeLocality(), marriage2.getWifeLocality());
        assertLocality(marriage1.getHusbandLocality(), marriage2.getHusbandLocality());
        assertArchiveDocument(marriage1.getArchiveDocument(), marriage2.getArchiveDocument());
        if (marriage2.getWitnesses() != null) {
            assertEquals(marriage1.getWitnesses().size(), marriage2.getWitnesses().size());
            List<Witness> witnesses1 = marriage1.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.model.Witness> witnesses2 = marriage2.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < witnesses1.size(); i++) {
                assertWitness(witnesses1.get(i), witnesses2.get(i));
            }
        }
        if (marriage2.getPersons() != null) {
            assertEquals(marriage1.getPersons().size(), marriage2.getPersons().size());
            List<EasyPerson> persons1 = marriage1.getPersons().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.Person> persons2 = marriage2.getPersons().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < persons1.size(); i++) {
                assertPerson(persons1.get(i), persons2.get(i));
            }
        }
    }

    static void assertMarriage(EasyMarriage marriage1, EasyMarriage marriage2) {
        assertNotNull(marriage1);
        assertNotNull(marriage2);
        assertEquals(marriage1.getDate(), marriage2.getDate());
        assertFullName(marriage1.getHusband(), marriage2.getHusband());
        assertFullName(marriage1.getWife(), marriage2.getWife());
        assertFullName(marriage1.getWifesFather(), marriage2.getWifesFather());
        assertFullName(marriage1.getHusbandsFather(), marriage2.getHusbandsFather());
        assertEquals(marriage1.getHusbandMarriageNumber(), marriage2.getHusbandMarriageNumber());
        assertEquals(marriage1.getWifeMarriageNumber(), marriage2.getWifeMarriageNumber());
        assertEquals(marriage1.getComment(), marriage2.getComment());
        assertAge(marriage1.getWifeAge(), marriage2.getWifeAge());
        assertAge(marriage1.getHusbandAge(), marriage2.getHusbandAge());
    }

    static void assertMarriage(EasyMarriage marriage1, genealogy.visualizer.entity.Marriage marriage2) {
        assertNotNull(marriage1);
        assertNotNull(marriage2);
        assertEquals(marriage1.getDate(), marriage2.getDate());
        assertFullName(marriage1.getHusband(), marriage2.getHusband());
        assertFullName(marriage1.getWife(), marriage2.getWife());
        assertFullName(marriage1.getWifesFather(), marriage2.getWifesFather());
        assertFullName(marriage1.getHusbandsFather(), marriage2.getHusbandsFather());
        assertEquals(marriage1.getHusbandMarriageNumber(), marriage2.getHusbandMarriageNumber().intValue());
        assertEquals(marriage1.getWifeMarriageNumber(), marriage2.getWifeMarriageNumber().intValue());
        assertEquals(marriage1.getComment(), marriage2.getComment());
        assertAge(marriage1.getWifeAge(), marriage2.getWifeAge());
        assertAge(marriage1.getHusbandAge(), marriage2.getHusbandAge());
    }

    static void assertWitness(Witness witness1, Witness witness2) {
        assertNotNull(witness1);
        assertNotNull(witness2);
        assertFullName(witness1.getFullName(), witness2.getFullName());
        assertEquals(witness1.getWitnessType(), witness2.getWitnessType());
        assertLocality(witness1.getLocality(), witness2.getLocality());
    }

    static void assertWitness(Witness witness1, genealogy.visualizer.entity.model.Witness witness2) {
        assertNotNull(witness1);
        assertNotNull(witness2);
        assertFullName(witness1.getFullName(), witness2.getFullName());
        assertEquals(witness1.getWitnessType().getValue(), witness2.getWitnessType().getName());
        assertLocality(witness1.getLocality(), witness2.getLocality());
    }

    private genealogy.visualizer.entity.Marriage generateRandomExistMarriage() {
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

        genealogy.visualizer.entity.Marriage marriageSave = generator.nextObject(genealogy.visualizer.entity.Marriage.class);
        marriageSave.setArchiveDocument(archiveDocumentExist);

        List<genealogy.visualizer.entity.Person> personsSave = generator.objects(genealogy.visualizer.entity.Person.class, generator.nextInt(5, 10)).toList();
        personsSave.forEach(p -> {
            p.setChristening(null);
            p.setPartners(Collections.emptyList());
            p.setChildren(Collections.emptyList());
            p.setRevisions(Collections.emptyList());
            p.setMarriages(Collections.emptyList());
            p.setParents(Collections.emptyList());
            p.setDeath(null);
            p.setDeathLocality(localityExisting);
            p.setBirthLocality(localityExisting);
        });
        List<genealogy.visualizer.entity.Person> personsExist = personRepository.saveAllAndFlush(personsSave);
        marriageSave.setPersons(personsExist);
        personsExist.forEach(p -> personIds.add(p.getId()));

        List<genealogy.visualizer.entity.model.Witness> witnessesSave = generator.objects(genealogy.visualizer.entity.model.Witness.class, generator.nextInt(5, 10)).toList();
        witnessesSave.forEach(gp -> gp.setLocality(localityExisting));
        marriageSave.setWitnesses(witnessesSave);
        marriageSave.setHusbandLocality(localityExisting);
        marriageSave.setWifeLocality(localityExisting);
        genealogy.visualizer.entity.Marriage marriageExist = marriageRepository.save(marriageSave);
        marriageIds.add(marriageExist.getId());
        return marriageExist;
    }
}
