package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.ArchiveDocumentType;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ArchiveControllerTest.assertArchive;
import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevision;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveDocumentControllerTest extends IntegrationTest {

    @Autowired
    ArchiveDocumentDAO archiveDocumentDAO;

    private static final String PATH = "/archive-document";

    @Test
    void saveTest() throws Exception {
        ArchiveDocument archiveDocumentSave = generator.nextObject(ArchiveDocument.class);
        EasyArchive archiveSave = generator.nextObject(EasyArchive.class);
        archiveDocumentSave.setArchive(archiveSave);
        EasyArchiveDocument nextRevisionSave = generator.nextObject(EasyArchiveDocument.class);
        archiveDocumentSave.setNextRevision(nextRevisionSave);
        List<EasyArchiveDocument> previousRevisionsSave = generator.objects(EasyArchiveDocument.class, generator.nextInt(5, 10)).toList();
        archiveDocumentSave.setPreviousRevisions(previousRevisionsSave);
        List<EasyFamilyMember> familyRevisionsSave = generator.objects(EasyFamilyMember.class, generator.nextInt(5, 10)).toList();
        archiveDocumentSave.setFamilyRevisions(familyRevisionsSave);
        List<EasyChristening> christeningsSave = generator.objects(EasyChristening.class, generator.nextInt(5, 10)).toList();
        archiveDocumentSave.setChristenings(christeningsSave);
        List<EasyDeath> deathsSave = generator.objects(EasyDeath.class, generator.nextInt(5, 10)).toList();
        archiveDocumentSave.setDeaths(deathsSave);
        List<EasyMarriage> marriagesSave = generator.objects(EasyMarriage.class, generator.nextInt(5, 10)).toList();
        archiveDocumentSave.setMarriages(marriagesSave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(archiveDocumentSave));
        ArchiveDocument response = objectMapper.readValue(responseJson, ArchiveDocument.class);
        assertNotNull(response);
        assertArchiveDocument(response, archiveDocumentSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        ArchiveDocument responseGet = objectMapper.readValue(responseJsonGet, ArchiveDocument.class);
        assertNotNull(responseGet);
        assertArchiveDocument(responseGet, archiveDocumentSave);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = generateRandomExistArchiveDocument();
        ArchiveDocument archiveDocumentSave = generator.nextObject(ArchiveDocument.class);
        archiveDocumentSave.setId(archiveDocumentExist.getId());
        EasyArchive archiveSave = generator.nextObject(EasyArchive.class);
        archiveDocumentSave.setArchive(archiveSave);
        EasyArchiveDocument nextRevisionSave = generator.nextObject(EasyArchiveDocument.class);
        archiveDocumentSave.setNextRevision(nextRevisionSave);
        List<EasyArchiveDocument> previousRevisionsSave = new ArrayList<>(generator.objects(EasyArchiveDocument.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getPreviousRevisions().forEach(pr -> {
            if (generator.nextBoolean()) {
                previousRevisionsSave.add(easyArchiveDocumentMapper.toDTO(pr));
            }
        });
        archiveDocumentSave.setPreviousRevisions(previousRevisionsSave);
        List<EasyFamilyMember> familyRevisionsSave = new ArrayList<>(generator.objects(EasyFamilyMember.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getFamilyRevisions().forEach(fr -> {
            if (generator.nextBoolean()) {
                familyRevisionsSave.add(easyFamilyRevisionMapper.toDTO(fr));
            }
        });
        archiveDocumentSave.setFamilyRevisions(familyRevisionsSave);
        List<EasyChristening> christeningsSave = new ArrayList<>(generator.objects(EasyChristening.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getChristenings().forEach(c -> {
            if (generator.nextBoolean()) {
                christeningsSave.add(easyChristeningMapper.toDTO(c));
            }
        });
        archiveDocumentSave.setChristenings(christeningsSave);
        List<EasyDeath> deathsSave = new ArrayList<>(generator.objects(EasyDeath.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getDeaths().forEach(d -> {
            if (generator.nextBoolean()) {
                deathsSave.add(easyDeathMapper.toDTO(d));
            }
        });
        archiveDocumentSave.setDeaths(deathsSave);
        List<EasyMarriage> marriagesSave = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getMarriages().forEach(m -> {
            if (generator.nextBoolean()) {
                marriagesSave.add(easyMarriageMapper.toDTO(m));
            }
        });
        archiveDocumentSave.setMarriages(marriagesSave);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(archiveDocumentSave));
        ArchiveDocument response = objectMapper.readValue(responseJson, ArchiveDocument.class);
        assertNotNull(response);
        assertArchiveDocument(response, archiveDocumentSave);
        String responseJsonGet = getRequest(PATH + "/" + archiveDocumentSave.getId());
        ArchiveDocument responseGet = objectMapper.readValue(responseJsonGet, ArchiveDocument.class);
        assertNotNull(responseGet);
        assertArchiveDocument(responseGet, archiveDocumentSave);
    }

    @Test
    void updateWithNullFieldTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = generateRandomExistArchiveDocument();
        ArchiveDocument archiveDocumentSave = generator.nextObject(ArchiveDocument.class);
        archiveDocumentSave.setId(archiveDocumentExist.getId());
        archiveDocumentSave.setArchive(null);
        archiveDocumentSave.setNextRevision(null);
        archiveDocumentSave.setPreviousRevisions(Collections.emptyList());
        archiveDocumentSave.setFamilyRevisions(Collections.emptyList());
        archiveDocumentSave.setChristenings(Collections.emptyList());
        archiveDocumentSave.setDeaths(Collections.emptyList());
        archiveDocumentSave.setMarriages(Collections.emptyList());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(archiveDocumentSave));
        ArchiveDocument response = objectMapper.readValue(responseJson, ArchiveDocument.class);
        assertNotNull(response);
        assertArchiveDocument(response, archiveDocumentSave);
        String responseJsonGet = getRequest(PATH + "/" + archiveDocumentSave.getId());
        ArchiveDocument responseGet = objectMapper.readValue(responseJsonGet, ArchiveDocument.class);
        assertNotNull(responseGet);
        assertArchiveDocument(responseGet, archiveDocumentSave);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocument = generateRandomExistArchiveDocument();
        String responseJson = deleteRequest(PATH + "/" + archiveDocument.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(archiveDocumentRepository.findById(archiveDocument.getId()).isEmpty());
        archiveDocument.getDeaths().forEach(p -> assertFalse(deathRepository.findById(p.getId()).isEmpty()));
        archiveDocument.getChristenings().forEach(p -> assertFalse(christeningRepository.findById(p.getId()).isEmpty()));
        archiveDocument.getFamilyRevisions().forEach(p -> assertFalse(familyRevisionRepository.findById(p.getId()).isEmpty()));
        archiveDocument.getPreviousRevisions().forEach(r -> assertFalse(archiveDocumentRepository.findById(r.getId()).isEmpty()));
        archiveDocument.getMarriages().forEach(m -> assertFalse(marriageRepository.findById(m.getId()).isEmpty()));
        assertFalse(archiveDocumentRepository.findById(archiveDocument.getNextRevision().getId()).isEmpty());
        assertFalse(archiveRepository.findById(archiveDocument.getArchive().getId()).isEmpty());
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocument = generateRandomExistArchiveDocument();
        String responseJson = getRequest(PATH + "/" + archiveDocument.getId());
        ArchiveDocument response = objectMapper.readValue(responseJson, ArchiveDocument.class);
        assertNotNull(response);
        assertEquals(response.getId(), archiveDocument.getId());
        assertArchiveDocument(response, archiveDocument);
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        ArchiveDocumentFilter filter = new ArchiveDocumentFilter()
                .abbreviation("РС1")
                .name("гос архив")
                .archiveId(archiveExisting.getId())
                .type(ArchiveDocumentType.CB)
                .year(1850);
        List<genealogy.visualizer.entity.ArchiveDocument> archiveDocumentsSave = generator.objects(genealogy.visualizer.entity.ArchiveDocument.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.ArchiveDocument archiveDocument : archiveDocumentsSave) {
            archiveDocument.setDeaths(Collections.emptyList());
            archiveDocument.setMarriages(Collections.emptyList());
            archiveDocument.setChristenings(Collections.emptyList());
            archiveDocument.setFamilyRevisions(Collections.emptyList());
            archiveDocument.setPreviousRevisions(Collections.emptyList());
            archiveDocument.setNextRevision(null);
            if (generator.nextBoolean()) {
                archiveDocument.setArchive(archiveExisting);
                archiveDocument.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getName() : filter.getName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                archiveDocument.setAbbreviation(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getAbbreviation() : filter.getAbbreviation().toLowerCase()) +
                        stringRandomizer.getRandomValue());
                archiveDocument.setType(genealogy.visualizer.entity.enums.ArchiveDocumentType.CB);
                archiveDocument.setYear(filter.getYear().shortValue());
                count++;
            } else {
                archiveDocument.setArchive(null);
            }
        }
        List<genealogy.visualizer.entity.ArchiveDocument> archiveDocumentsExist = archiveDocumentRepository.saveAllAndFlush(archiveDocumentsSave);
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyArchiveDocument> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyArchiveDocument.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyArchiveDocument::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.ArchiveDocument archiveDocument : archiveDocumentsExist) {
            if (archiveDocument.getAbbreviation().toLowerCase().contains(filter.getAbbreviation().toLowerCase()) &&
                    archiveDocument.getName().toLowerCase().contains(filter.getName().toLowerCase()) &&
                    archiveDocument.getArchive().getId().equals(filter.getArchiveId()) &&
                    archiveDocument.getYear() == filter.getYear().shortValue() &&
                    archiveDocument.getType().name().equals(filter.getType().name())) {
                assertTrue(findIds.contains(archiveDocument.getId()));
            }
        }
    }

    @Test
    void saveUnauthorizedTest() throws Exception {
        ArchiveDocument object = generator.nextObject(ArchiveDocument.class);
        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void updateUnauthorizedTest() throws Exception {
        ArchiveDocument object = generator.nextObject(ArchiveDocument.class);
        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        ArchiveDocument object = generator.nextObject(ArchiveDocument.class);
        deleteUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        christeningRepository.deleteAll();
        deathRepository.deleteAll();
        marriageRepository.deleteAll();
        familyRevisionRepository.deleteAll();
        super.tearDown();
    }

    protected static void assertArchiveDocument(ArchiveDocument archiveDocument1, ArchiveDocument archiveDocument2) {
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType(), archiveDocument2.getType());
        assertEquals(archiveDocument1.getYear(), archiveDocument2.getYear());
        assertArchive(archiveDocument1.getArchive(), archiveDocument2.getArchive());
        assertArchiveDocument(archiveDocument1.getNextRevision(), archiveDocument2.getNextRevision());
        if (archiveDocument2.getPreviousRevisions() != null) {
            assertEquals(archiveDocument1.getPreviousRevisions().size(), archiveDocument2.getPreviousRevisions().size());
            List<EasyArchiveDocument> previousRevision1 = archiveDocument1.getPreviousRevisions().stream().sorted(Comparator.comparing(EasyArchiveDocument::getName)).toList();
            List<EasyArchiveDocument> previousRevision2 = archiveDocument2.getPreviousRevisions().stream().sorted(Comparator.comparing(EasyArchiveDocument::getName)).toList();
            for (int i = 0; i < previousRevision1.size(); i++) {
                assertArchiveDocument(previousRevision1.get(i), previousRevision2.get(i));
            }
        }
        if (archiveDocument2.getFamilyRevisions() != null) {
            assertEquals(archiveDocument1.getFamilyRevisions().size(), archiveDocument2.getFamilyRevisions().size());
            List<EasyFamilyMember> familyRevisions1 = archiveDocument1.getFamilyRevisions().stream().sorted(Comparator.comparing(fr -> fr.getFullName().getName())).toList();
            List<EasyFamilyMember> familyRevisions2 = archiveDocument2.getFamilyRevisions().stream().sorted(Comparator.comparing(fr -> fr.getFullName().getName())).toList();
            for (int i = 0; i < familyRevisions1.size(); i++) {
                assertFamilyRevision(familyRevisions1.get(i), familyRevisions2.get(i));
            }
        }
        if (archiveDocument2.getChristenings() != null) {
            assertEquals(archiveDocument1.getChristenings().size(), archiveDocument2.getChristenings().size());
            List<EasyChristening> christenings1 = archiveDocument1.getChristenings().stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
            List<EasyChristening> christenings2 = archiveDocument2.getChristenings().stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
            for (int i = 0; i < christenings1.size(); i++) {
                assertChristening(christenings1.get(i), christenings2.get(i));
            }
        }
        if (archiveDocument2.getMarriages() != null) {
            assertEquals(archiveDocument1.getMarriages().size(), archiveDocument2.getMarriages().size());
            List<EasyMarriage> marriages1 = archiveDocument1.getMarriages().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            List<EasyMarriage> marriages2 = archiveDocument2.getMarriages().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            for (int i = 0; i < marriages1.size(); i++) {
                assertMarriage(marriages1.get(i), marriages2.get(i));
            }
        }
        if (archiveDocument2.getDeaths() != null) {
            assertEquals(archiveDocument1.getDeaths().size(), archiveDocument2.getDeaths().size());
            List<EasyDeath> deaths1 = archiveDocument1.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<EasyDeath> deaths2 = archiveDocument2.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < deaths1.size(); i++) {
                assertDeath(deaths1.get(i), deaths2.get(i));
            }
        }
    }

    protected static void assertArchiveDocument(EasyArchiveDocument archiveDocument1, EasyArchiveDocument archiveDocument2) {
        if (archiveDocument1 == null || archiveDocument2 == null) {
            assertNull(archiveDocument1);
            assertNull(archiveDocument2);
            return;
        }
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType(), archiveDocument2.getType());
        assertEquals(archiveDocument1.getYear(), archiveDocument2.getYear());
    }

    protected static void assertArchiveDocument(EasyArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
        if (archiveDocument1 == null || archiveDocument2 == null) {
            assertNull(archiveDocument1);
            assertNull(archiveDocument2);
            return;
        }
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType().getValue(), archiveDocument2.getType().getTitle());
        assertEquals(archiveDocument1.getYear(), (int) archiveDocument2.getYear());
    }

    protected static void assertArchiveDocument(ArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType().getValue(), archiveDocument2.getType().getTitle());
        assertEquals(archiveDocument1.getYear(), (int) archiveDocument2.getYear());
        assertArchive(archiveDocument1.getArchive(), archiveDocument2.getArchive());
        assertArchiveDocument(archiveDocument1.getNextRevision(), archiveDocument2.getNextRevision());
        if (archiveDocument2.getPreviousRevisions() != null) {
            assertEquals(archiveDocument1.getPreviousRevisions().size(), archiveDocument2.getPreviousRevisions().size());
            List<EasyArchiveDocument> previousRevision1 = archiveDocument1.getPreviousRevisions().stream().sorted(Comparator.comparing(EasyArchiveDocument::getName)).toList();
            List<genealogy.visualizer.entity.ArchiveDocument> previousRevision2 = archiveDocument2.getPreviousRevisions().stream().sorted(Comparator.comparing(genealogy.visualizer.entity.ArchiveDocument::getName)).toList();
            for (int i = 0; i < previousRevision1.size(); i++) {
                assertArchiveDocument(previousRevision1.get(i), previousRevision2.get(i));
            }
        }
        if (archiveDocument2.getFamilyRevisions() != null) {
            assertEquals(archiveDocument1.getFamilyRevisions().size(), archiveDocument2.getFamilyRevisions().size());
            List<EasyFamilyMember> familyRevisions1 = archiveDocument1.getFamilyRevisions().stream().sorted(Comparator.comparing(fr -> fr.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.FamilyRevision> familyRevisions2 = archiveDocument2.getFamilyRevisions().stream().sorted(Comparator.comparing(fr -> fr.getFullName().getName())).toList();
            for (int i = 0; i < familyRevisions1.size(); i++) {
                assertFamilyRevision(familyRevisions1.get(i), familyRevisions2.get(i));
            }
        }
        if (archiveDocument2.getChristenings() != null) {
            assertEquals(archiveDocument1.getChristenings().size(), archiveDocument2.getChristenings().size());
            List<EasyChristening> christenings1 = archiveDocument1.getChristenings().stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
            List<genealogy.visualizer.entity.Christening> christenings2 = archiveDocument2.getChristenings().stream().sorted(Comparator.comparing(genealogy.visualizer.entity.Christening::getName)).toList();
            for (int i = 0; i < christenings1.size(); i++) {
                assertChristening(christenings1.get(i), christenings2.get(i));
            }
        }
        if (archiveDocument2.getMarriages() != null) {
            assertEquals(archiveDocument1.getMarriages().size(), archiveDocument2.getMarriages().size());
            List<EasyMarriage> marriages1 = archiveDocument1.getMarriages().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            List<genealogy.visualizer.entity.Marriage> marriages2 = archiveDocument2.getMarriages().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            for (int i = 0; i < marriages1.size(); i++) {
                assertMarriage(marriages1.get(i), marriages2.get(i));
            }
        }
        if (archiveDocument2.getDeaths() != null) {
            assertEquals(archiveDocument1.getDeaths().size(), archiveDocument2.getDeaths().size());
            List<EasyDeath> deaths1 = archiveDocument1.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.Death> deaths2 = archiveDocument2.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < deaths1.size(); i++) {
                assertDeath(deaths1.get(i), deaths2.get(i));
            }
        }
    }

    private genealogy.visualizer.entity.ArchiveDocument generateRandomExistArchiveDocument() {
        genealogy.visualizer.entity.Archive archiveSave = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveSave.setArchiveDocuments(null);
        genealogy.visualizer.entity.Archive archiveExist = archiveRepository.saveAndFlush(archiveSave);

        genealogy.visualizer.entity.ArchiveDocument nextRevisionSave = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        nextRevisionSave.setArchive(archiveExist);
        nextRevisionSave.setPreviousRevisions(Collections.emptyList());
        nextRevisionSave.setFamilyRevisions(Collections.emptyList());
        nextRevisionSave.setChristenings(Collections.emptyList());
        nextRevisionSave.setMarriages(Collections.emptyList());
        nextRevisionSave.setDeaths(Collections.emptyList());
        nextRevisionSave.setNextRevision(null);

        genealogy.visualizer.entity.ArchiveDocument archiveDocumentSave = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        archiveDocumentSave.setArchive(archiveExist);
        archiveDocumentSave.setPreviousRevisions(Collections.emptyList());
        archiveDocumentSave.setFamilyRevisions(Collections.emptyList());
        archiveDocumentSave.setChristenings(Collections.emptyList());
        archiveDocumentSave.setMarriages(Collections.emptyList());
        archiveDocumentSave.setDeaths(Collections.emptyList());
        archiveDocumentSave.setNextRevision(archiveDocumentRepository.saveAndFlush(nextRevisionSave));
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = archiveDocumentRepository.saveAndFlush(archiveDocumentSave);

        List<genealogy.visualizer.entity.ArchiveDocument> previousRevisionsSave = generator.objects(genealogy.visualizer.entity.ArchiveDocument.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.ArchiveDocument> previousRevisionsExist = new ArrayList<>(previousRevisionsSave.size());
        for (genealogy.visualizer.entity.ArchiveDocument entity : previousRevisionsSave) {
            entity.setArchive(archiveExist);
            entity.setNextRevision(archiveDocumentExist);
            previousRevisionsExist.add(archiveDocumentRepository.saveAndFlush(entity));
        }
        archiveDocumentExist.setPreviousRevisions(previousRevisionsExist);

        List<genealogy.visualizer.entity.FamilyRevision> familyRevisionsSave = generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.FamilyRevision> familyRevisionsExist = new ArrayList<>(familyRevisionsSave.size());
        for (genealogy.visualizer.entity.FamilyRevision entity : familyRevisionsSave) {
            entity.setArchiveDocument(archiveDocumentExist);
            familyRevisionsExist.add(familyRevisionRepository.saveAndFlush(entity));
        }
        archiveDocumentExist.setFamilyRevisions(familyRevisionsExist);

        List<genealogy.visualizer.entity.Christening> christeningsSave = generator.objects(genealogy.visualizer.entity.Christening.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Christening> christeningsExist = new ArrayList<>(christeningsSave.size());
        for (genealogy.visualizer.entity.Christening entity : christeningsSave) {
            entity.getGodParents().forEach(gp -> localityMapper.toDTO(localityExisting));
            entity.setArchiveDocument(archiveDocumentExist);
            entity.setLocality(localityExisting);
            christeningsExist.add(christeningRepository.saveAndFlush(entity));
        }
        archiveDocumentExist.setChristenings(christeningsExist);

        List<genealogy.visualizer.entity.Marriage> marriagesSave = generator.objects(genealogy.visualizer.entity.Marriage.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Marriage> marriagesExist = new ArrayList<>(marriagesSave.size());
        for (genealogy.visualizer.entity.Marriage entity : marriagesSave) {
            entity.setArchiveDocument(archiveDocumentExist);
            entity.setWifeLocality(localityExisting);
            entity.setHusbandLocality(localityExisting);
            marriagesExist.add(marriageRepository.saveAndFlush(entity));
        }
        archiveDocumentExist.setMarriages(marriagesExist);

        List<genealogy.visualizer.entity.Death> deathsSave = generator.objects(genealogy.visualizer.entity.Death.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Death> deathsExist = new ArrayList<>(deathsSave.size());
        for (genealogy.visualizer.entity.Death entity : deathsSave) {
            entity.setArchiveDocument(archiveDocumentExist);
            entity.setLocality(localityExisting);
            deathsExist.add(deathRepository.saveAndFlush(entity));
        }
        archiveDocumentExist.setDeaths(deathsExist);
        return archiveDocumentExist;
    }
}