package genealogy.visualizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveWithFamilyMembers;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO Включить тесты при реализации ControllerAdvice
class FamilyRevisionControllerTest extends IntegrationTest {

    @Autowired
    private FamilyRevisionMapper familyRevisionMapper;

    private static final String PATH = "/family-revision";

    @Test
    void saveTest() throws Exception {
        Archive archiveSave = generator.nextObject(Archive.class);
        ArchiveDocument archiveDocumentSave = generator.nextObject(ArchiveDocument.class);
        archiveDocumentSave.setArchive(archiveSave);
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        revisionSave.setArchiveDocument(archiveDocumentSave);
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void saveWithPartnerTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        EasyFamilyMember revisionPartnerSave = generator.nextObject(EasyFamilyMember.class);
        revisionPartnerSave.setId(null);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        revisionPartnerSave.setArchiveDocument(archiveDocumentExisting);
        revisionSave.setPartner(revisionPartnerSave);
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
        assertNotNull(revisionSave.getPartner());
        assertNotNull(response.getPartner());
        assertFamilyRevision(response.getPartner(), revisionSave.getPartner());
    }

    @Test
    void saveWithExistingArchiveTest() throws Exception {
        ArchiveDocument archiveDocumentSave = generator.nextObject(ArchiveDocument.class);
        archiveDocumentSave.setArchive(archiveDocumentExisting.getArchive());
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        revisionSave.setArchiveDocument(archiveDocumentSave);
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void saveWithExistingArchiveDocumentTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAndFlush(revisionSave);
        String responseJson = getRequest(PATH + "/" + revisionExist.getId());
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertEquals(response.getId(), revisionExist.getId());
        assertFamilyRevision(response, revisionExist);
    }

    @Test
    @Disabled
    void getByIdExceptionTest() throws Exception {
        String responseJson = mockMvc.perform(
                        get("/family-revision/" + generator.nextInt(10000, 20000)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
        assertEquals(response.getMessage(), "Данные не найдены");
    }

    @Test
    void updateParentExistingTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAndFlush(revisionSave);
        FamilyMember revisionUpdate = familyRevisionMapper.toDTO(revisionExist);
        EasyFamilyMember revisionPartnerSave = generator.nextObject(EasyFamilyMember.class);
        revisionPartnerSave.setId(null);
        revisionPartnerSave.setArchiveDocument(archiveDocumentExisting);
        revisionUpdate.setPartner(revisionPartnerSave);
        revisionUpdate.setAnotherNames(generator.objects(String.class, 4).toList());
        String requestJson = objectMapper.writeValueAsString(revisionUpdate);
        String responseJson = putRequest(PATH, requestJson);
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionUpdate);
        assertNotNull(revisionUpdate.getPartner());
        assertNotNull(response.getPartner());
        assertFamilyRevision(response.getPartner(), revisionUpdate.getPartner());
    }

    @Test
    @Disabled
    void updateNotExistingTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        revisionSave.setId(generator.nextLong(20000, 30000));
        String objectString = objectMapper.writeValueAsString(revisionSave);
        String responseJson = mockMvc.perform(
                        put("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
        assertEquals(response.getMessage(), "Данные не найдены");
    }

    @Test
    void updateAnotherNamesExistingTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        revisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        genealogy.visualizer.entity.FamilyRevision partnerRevisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        partnerRevisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        partnerRevisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        revisionSave.setPartner(partnerRevisionSave);
        partnerRevisionSave.setPartner(revisionSave);
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAllAndFlush(List.of(revisionSave, partnerRevisionSave)).getFirst();
        familyRevisionIds.add(revisionExist.getPartner().getId());
        FamilyMember revisionUpdate = familyRevisionMapper.toDTO(revisionExist);
        revisionUpdate.setPartner(null);
        revisionUpdate.setAnotherNames(List.of(revisionUpdate.getAnotherNames().getFirst(), generator.nextObject(String.class)));
        String requestJson = objectMapper.writeValueAsString(revisionUpdate);
        String responseJson = putRequest(PATH, requestJson);
        FamilyMember response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionUpdate);
        assertNull(revisionUpdate.getPartner());
    }

    @Test
    void deleteExistingTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        revisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        genealogy.visualizer.entity.FamilyRevision partnerRevisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        partnerRevisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        partnerRevisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        revisionSave.setPartner(partnerRevisionSave);
        partnerRevisionSave.setPartner(revisionSave);
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAllAndFlush(List.of(revisionSave, partnerRevisionSave)).getFirst();
        String responseJson = deleteRequest(PATH + "/" + revisionSave.getId());
        familyRevisionIds.add(revisionExist.getPartner().getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(familyRevisionRepository.findById(revisionExist.getId()).isEmpty());
        assertFalse(familyRevisionRepository.findById(revisionExist.getPartner().getId()).isEmpty());
        assertTrue(familyRevisionRepository.getAnotherNames(revisionExist.getId()).isEmpty());
        assertFalse(archiveDocumentRepository.findById(revisionExist.getArchiveDocument().getId()).isEmpty());
        assertFalse(archiveRepository.findById(revisionExist.getArchiveDocument().getArchive().getId()).isEmpty());
    }

    @Test
    void deleteNotExistingTest() throws Exception {
        String responseJson = deleteRequest(PATH + "/" + generator.nextInt(10000, 20000));
        assertTrue(responseJson.isEmpty());
    }

    @Test
    void findFamilyRevisionTest() throws Exception {
        List<genealogy.visualizer.entity.FamilyRevision> familyRevisionList = generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(10, 15)).toList();
        short familyRevisionNumber = (short) generator.nextInt(10000, 20000);
        familyRevisionList = familyRevisionList.stream().peek(familyRevision -> {
                    if (generator.nextBoolean()) {
                        familyRevision.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
                        if (generator.nextBoolean()) {
                            familyRevision.setFamilyRevisionNumber(familyRevisionNumber);
                        }
                    }
                    if (generator.nextBoolean()) {
                        familyRevision.setFamilyRevisionNumber(familyRevisionNumber);
                    }
                })
                .toList();
        familyRevisionList = generateFamilyRevisionList(familyRevisionList);
        FamilyMemberFilter filterRequest = new FamilyMemberFilter((int) familyRevisionNumber, archiveDocumentExisting.getId(), false, true);
        String requestJson = objectMapper.writeValueAsString(filterRequest);
        String responseJson = postRequest(PATH + "/family", requestJson);
        List<FamilyMemberFullInfo> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, FamilyMemberFullInfo.class));
        assertNotNull(response);
        List<genealogy.visualizer.entity.FamilyRevision> existFamilyRevisionList = familyRevisionList.stream()
                .filter(familyRevision -> familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumber) &&
                        familyRevision.getArchiveDocument().getId().equals(archiveDocumentExisting.getId()))
                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId()))
                .toList();
        assertEquals(existFamilyRevisionList.size(), response.size());
        List<FamilyMember> responseFamilyRevisions = response.stream()
                .map(FamilyMemberFullInfo::getFamilyMember)
                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId())).toList();
        assertEquals(existFamilyRevisionList.size(), responseFamilyRevisions.size());
        for (int i = 0; i < existFamilyRevisionList.size(); i++) {
            assertFamilyRevision(responseFamilyRevisions.get(i), existFamilyRevisionList.get(i));
        }
    }

    @Test
    @Disabled
    void findFamilyRevisionNullTest() throws Exception {
        generateFamilyRevisionList(generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(10, 15)).toList());
        FamilyMemberFilter filterRequest = new FamilyMemberFilter((int) (short) generator.nextInt(10000, 20000), archiveDocumentExisting.getId(), false, true);
        String objectString = objectMapper.writeValueAsString(filterRequest);
        String responseJson = mockMvc.perform(
                        post("/family-revision/family")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
        assertEquals(response.getMessage(), "Данные не найдены");
    }

    @Test
    void findFamilyRevisionsAllInfoWithPersonTest() throws Exception {
        findFamilyRevisionsAllInfoTest(true);
    }

    @Test
    void findFamilyRevisionsAllInfoWithoutPersonTest() throws Exception {
        findFamilyRevisionsAllInfoTest(false);
    }

    void findFamilyRevisionsAllInfoTest(boolean isFindWithHavePerson) throws Exception {
        genealogy.visualizer.entity.Person person = generator.nextObject(genealogy.visualizer.entity.Person.class);
        person.setBirthLocality(localityExisting);
        person.setDeathLocality(localityExisting);
        person.setChristening(null);
        person.setDeath(null);
        person.setMarriages(null);
        person.setRevisions(null);
        person.setParents(null);
        person.setPartners(null);
        person.setChildren(null);
        person.setId(null);
        person = personRepository.saveAndFlush(person);
        personIds.add(person.getId());
        genealogy.visualizer.entity.ArchiveDocument adParent = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adParent.setArchive(archiveExisting);
        adParent = archiveDocumentRepository.saveAndFlush(adParent);
        Long adParentId = adParent.getId();
        archiveDocumentIds.add(adParentId);
        genealogy.visualizer.entity.ArchiveDocument adChildLevel1 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adChildLevel1.setNextRevision(adParent);
        adChildLevel1.setArchive(archiveExisting);
        adChildLevel1 = archiveDocumentRepository.saveAndFlush(adChildLevel1);
        Long adChildLevel1Id = adChildLevel1.getId();
        archiveDocumentIds.add(adChildLevel1Id);
        genealogy.visualizer.entity.ArchiveDocument adSecondChildLevel1 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adSecondChildLevel1.setNextRevision(adParent);
        adSecondChildLevel1.setArchive(archiveExisting);
        adSecondChildLevel1 = archiveDocumentRepository.saveAndFlush(adSecondChildLevel1);
        Long adSecondChildLevel1Id = adSecondChildLevel1.getId();
        archiveDocumentIds.add(adSecondChildLevel1Id);
        genealogy.visualizer.entity.ArchiveDocument adChildLevel2 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adChildLevel2.setNextRevision(adChildLevel1);
        adChildLevel2.setArchive(archiveExisting);
        adChildLevel2 = archiveDocumentRepository.saveAndFlush(adChildLevel2);
        Long adChildLevel2Id = adChildLevel2.getId();
        archiveDocumentIds.add(adChildLevel2Id);
        List<genealogy.visualizer.entity.FamilyRevision> familyRevisionList = generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(20, 30)).toList();
        short familyRevisionNumberParent = (short) generator.nextInt(25000, 30000);
        short familyRevisionNumberChildLevel1 = (short) generator.nextInt(20000, 25000);
        short familyRevisionNumberSecondChildLeve1 = (short) generator.nextInt(15000, 20000);
        short familyRevisionNumberChildLeve2 = (short) generator.nextInt(10000, 15000);
        byte adParentCount = 0;
        byte adChildLevel1Count = 0;
        byte adSecondChildLevel1Count = 0;
        byte adChildLevel2Count = 0;
        for (int i = 0; i < familyRevisionList.size(); i++) {
            FamilyRevision familyRevision = familyRevisionList.get(i);
            if ((i % 5) == 0) {
                familyRevision.setArchiveDocument(adParent);
                familyRevision.setFamilyRevisionNumber(familyRevisionNumberParent);
                if (generator.nextBoolean()) {
                    familyRevision.setPerson(person);
                    if (!isFindWithHavePerson) {
                        continue;
                    }
                }
                adParentCount++;
                continue;
            }
            if ((i % 4) == 0) {
                familyRevision.setArchiveDocument(adChildLevel1);
                familyRevision.setFamilyRevisionNumber(familyRevisionNumberChildLevel1);
                familyRevision.setNextFamilyRevisionNumber(familyRevisionNumberParent);
                if (generator.nextBoolean()) {
                    familyRevision.setPerson(person);
                    if (!isFindWithHavePerson) {
                        continue;
                    }
                }
                adChildLevel1Count++;
                continue;
            }
            if ((i % 3) == 0) {
                familyRevision.setArchiveDocument(adSecondChildLevel1);
                familyRevision.setFamilyRevisionNumber(familyRevisionNumberSecondChildLeve1);
                familyRevision.setNextFamilyRevisionNumber(familyRevisionNumberParent);
                if (generator.nextBoolean()) {
                    familyRevision.setPerson(person);
                    if (!isFindWithHavePerson) {
                        continue;
                    }
                }
                adSecondChildLevel1Count++;
                continue;
            }
            if ((i % 2) == 0) {
                familyRevision.setArchiveDocument(adChildLevel2);
                familyRevision.setFamilyRevisionNumber(familyRevisionNumberChildLeve2);
                familyRevision.setNextFamilyRevisionNumber(familyRevisionNumberChildLevel1);
                if (generator.nextBoolean()) {
                    familyRevision.setPerson(person);
                    if (!isFindWithHavePerson) {
                        continue;
                    }
                }
                adChildLevel2Count++;
                continue;
            }
            if (generator.nextBoolean()) {
                familyRevision.setArchiveDocument(adSecondChildLevel1);
            }
            if (generator.nextBoolean()) {
                familyRevision.setPerson(person);
            }
        }
        familyRevisionList = generateFamilyRevisionList(familyRevisionList);
        FamilyMemberFilter filterRequest = new FamilyMemberFilter((int) familyRevisionNumberSecondChildLeve1, adSecondChildLevel1Id, true, isFindWithHavePerson);
        String requestJson = objectMapper.writeValueAsString(filterRequest);
        String responseJson = postRequest(PATH + "/family", requestJson);
        List<FamilyMemberFullInfo> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, FamilyMemberFullInfo.class));
        response.sort((fr1, fr2) -> fr2.getFamilyMember().getId().compareTo(fr1.getFamilyMember().getId()));
        assertNotNull(response);
        Map<Long, List<genealogy.visualizer.entity.FamilyRevision>> existFamilyRevisionMap =
                familyRevisionList.stream().collect(Collectors.groupingBy(familyRevision -> familyRevision.getArchiveDocument().getId()));
        List<genealogy.visualizer.entity.FamilyRevision> existFamilyRevisionList = existFamilyRevisionMap
                .get(adSecondChildLevel1Id).stream()
                .filter(familyRevision -> familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumberSecondChildLeve1))
                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId()))
                .toList();
        assertEquals(existFamilyRevisionList.size(), response.size());
        for (int i = 0; i < existFamilyRevisionList.size(); i++) {
            FamilyMemberFullInfo fullInfo = response.get(i);
            assertFamilyRevision(fullInfo.getFamilyMember(), existFamilyRevisionList.get(i));
            List<ArchiveWithFamilyMembers> anotherFamilies = fullInfo.getAnotherFamilies();
            for (ArchiveWithFamilyMembers family : anotherFamilies) {
                List<genealogy.visualizer.entity.FamilyRevision> existFamilyRevision =
                        existFamilyRevisionMap.get(family.getArchive().getId())
                                .stream()
                                .filter(familyRevision -> {
                                    if (!isFindWithHavePerson && familyRevision.getPerson() != null) {
                                        return false;
                                    }
                                    if (familyRevision.getArchiveDocument().getId().equals(adParentId)) {
                                        return familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumberParent);
                                    }
                                    if (familyRevision.getArchiveDocument().getId().equals(adChildLevel1Id)) {
                                        return familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumberChildLevel1);
                                    }
                                    if (familyRevision.getArchiveDocument().getId().equals(adSecondChildLevel1Id)) {
                                        return familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumberSecondChildLeve1);
                                    }
                                    if (familyRevision.getArchiveDocument().getId().equals(adChildLevel2Id)) {
                                        return familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumberChildLeve2);
                                    }
                                    return false;
                                })
                                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId()))
                                .toList();
                List<FamilyMember> familyMembers = family.getFamilies()
                        .stream()
                        .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId()))
                        .toList();
                if (family.getArchive().getId().equals(adParentId)) {
                    assertEquals(adParentCount, familyMembers.size());
                }
                if (family.getArchive().getId().equals(adChildLevel1Id)) {
                    assertEquals(adChildLevel1Count, familyMembers.size());
                }
                if (family.getArchive().getId().equals(adSecondChildLevel1Id)) {
                    assertEquals(adSecondChildLevel1Count, familyMembers.size());
                }
                if (family.getArchive().getId().equals(adChildLevel2Id)) {
                    assertEquals(adChildLevel2Count, familyMembers.size());
                }
                assertEquals(familyMembers.size(), existFamilyRevision.size());
                for (int j = 0; j < familyMembers.size(); j++) {
                    assertFamilyRevision(familyMembers.get(j), existFamilyRevision.get(j));
                }
            }
        }
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        familyRevisionRepository.deleteAllById(familyRevisionIds);
        personRepository.deleteAllById(personIds);
        super.tearDown();
    }

    private FamilyMember getFamilyRevisionFromJson(String responseJson) throws JsonProcessingException {
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        if (response != null) {
            if (response.getPartner() != null) {
                familyRevisionIds.add(response.getPartner().getId());
            }
            familyRevisionIds.add(response.getId());
            archiveIds.add(response.getArchiveDocument().getArchive().getId());
            archiveDocumentIds.add(response.getArchiveDocument().getId());
        }
        return response;
    }

    protected static void assertFamilyRevision(FamilyMember familyRevision, EasyFamilyMember easyFamilyMember) {
        assertNotNull(familyRevision);
        assertNotNull(easyFamilyMember);
        assertEquals(familyRevision.getFamilyRevisionNumber(), easyFamilyMember.getFamilyRevisionNumber());
        assertEquals(familyRevision.getNextFamilyRevisionNumber(), easyFamilyMember.getNextFamilyRevisionNumber());
        assertEquals(familyRevision.getListNumber(), easyFamilyMember.getListNumber());
        assertEquals(familyRevision.getDeparted(), easyFamilyMember.getDeparted());
        assertEquals(familyRevision.getArrived(), easyFamilyMember.getArrived());
        assertEquals(familyRevision.getIsHeadOfYard(), easyFamilyMember.getIsHeadOfYard());
        assertEquals(familyRevision.getFamilyGeneration(), easyFamilyMember.getFamilyGeneration());
        assertEquals(familyRevision.getComment(), easyFamilyMember.getComment());
        assertArchiveDocument(familyRevision.getArchiveDocument(), easyFamilyMember.getArchiveDocument());
        assertEquals(familyRevision.getSex(), easyFamilyMember.getSex());
        assertEquals(familyRevision.getFullName(), easyFamilyMember.getFullName());
        assertEquals(familyRevision.getRelative(), easyFamilyMember.getRelative());
        assertEquals(familyRevision.getAge(), easyFamilyMember.getAge());
        assertEquals(familyRevision.getAgeInNextRevision(), easyFamilyMember.getAgeInNextRevision());
        assertEquals(familyRevision.getAnotherNames(), easyFamilyMember.getAnotherNames());
        assertNull(familyRevision.getPartner());
        assertNotNull(familyRevision.getId());
    }

    protected static void assertFamilyRevision(EasyFamilyMember familyRevision, EasyFamilyMember easyFamilyMember) {
        assertNotNull(familyRevision);
        assertNotNull(easyFamilyMember);
        assertEquals(familyRevision.getFamilyRevisionNumber(), easyFamilyMember.getFamilyRevisionNumber());
        assertEquals(familyRevision.getNextFamilyRevisionNumber(), easyFamilyMember.getNextFamilyRevisionNumber());
        assertEquals(familyRevision.getListNumber(), easyFamilyMember.getListNumber());
        assertEquals(familyRevision.getDeparted(), easyFamilyMember.getDeparted());
        assertEquals(familyRevision.getArrived(), easyFamilyMember.getArrived());
        assertEquals(familyRevision.getIsHeadOfYard(), easyFamilyMember.getIsHeadOfYard());
        assertEquals(familyRevision.getFamilyGeneration(), easyFamilyMember.getFamilyGeneration());
        assertEquals(familyRevision.getComment(), easyFamilyMember.getComment());
        assertArchiveDocument(familyRevision.getArchiveDocument(), easyFamilyMember.getArchiveDocument());
        assertEquals(familyRevision.getSex(), easyFamilyMember.getSex());
        assertEquals(familyRevision.getFullName(), easyFamilyMember.getFullName());
        assertEquals(familyRevision.getRelative(), easyFamilyMember.getRelative());
        assertEquals(familyRevision.getAge(), easyFamilyMember.getAge());
        assertEquals(familyRevision.getAgeInNextRevision(), easyFamilyMember.getAgeInNextRevision());
        assertEquals(familyRevision.getAnotherNames(), easyFamilyMember.getAnotherNames());
        assertNotNull(familyRevision.getId());
    }

    protected static void assertFamilyRevision(FamilyMember familyRevision1, FamilyMember familyRevision2) {
        assertNotNull(familyRevision1);
        assertNotNull(familyRevision2);
        assertEquals(familyRevision1.getFamilyRevisionNumber(), familyRevision2.getFamilyRevisionNumber());
        assertEquals(familyRevision1.getNextFamilyRevisionNumber(), familyRevision2.getNextFamilyRevisionNumber());
        assertEquals(familyRevision1.getListNumber(), familyRevision2.getListNumber());
        assertEquals(familyRevision1.getDeparted(), familyRevision2.getDeparted());
        assertEquals(familyRevision1.getArrived(), familyRevision2.getArrived());
        assertEquals(familyRevision1.getIsHeadOfYard(), familyRevision2.getIsHeadOfYard());
        assertEquals(familyRevision1.getFamilyGeneration(), familyRevision2.getFamilyGeneration());
        assertEquals(familyRevision1.getComment(), familyRevision2.getComment());
        assertArchiveDocument(familyRevision1.getArchiveDocument(), familyRevision2.getArchiveDocument());
        assertEquals(familyRevision1.getSex(), familyRevision2.getSex());
        assertEquals(familyRevision1.getFullName(), familyRevision2.getFullName());
        assertEquals(familyRevision1.getRelative(), familyRevision2.getRelative());
        assertAge(familyRevision1.getAge(), familyRevision2.getAge());
        assertAge(familyRevision1.getAgeInNextRevision(), familyRevision2.getAgeInNextRevision());
        if (familyRevision1.getAnotherNames() != null && !familyRevision1.getAnotherNames().isEmpty()) {
            assertEquals(familyRevision1.getAnotherNames().stream().sorted().toList(),
                    familyRevision2.getAnotherNames().stream().sorted().toList());
        } else {
            assertTrue(familyRevision1.getAnotherNames().isEmpty());
            assertTrue(familyRevision2.getAnotherNames().isEmpty());
        }
        if (familyRevision1.getPartner() == null) {
            assertNull(familyRevision2.getPartner());
        }
    }

    protected static void assertFamilyRevision(FamilyMember familyRevision1, genealogy.visualizer.entity.FamilyRevision familyRevision2) {
        assertNotNull(familyRevision1);
        assertNotNull(familyRevision2);
        assertEquals(familyRevision1.getFamilyRevisionNumber(), (int) familyRevision2.getFamilyRevisionNumber());
        assertEquals(familyRevision1.getNextFamilyRevisionNumber(), (int) familyRevision2.getNextFamilyRevisionNumber());
        assertEquals(familyRevision1.getListNumber(), (int) familyRevision2.getListNumber());
        assertEquals(familyRevision1.getDeparted(), familyRevision2.getDeparted());
        assertEquals(familyRevision1.getArrived(), familyRevision2.getArrived());
        assertEquals(familyRevision1.getIsHeadOfYard(), familyRevision2.getHeadOfYard());
        assertEquals(familyRevision1.getFamilyGeneration(), (int) familyRevision2.getFamilyGeneration());
        assertEquals(familyRevision1.getComment(), familyRevision2.getComment());
        assertArchiveDocument(familyRevision1.getArchiveDocument(), familyRevision2.getArchiveDocument());
        assertEquals(familyRevision1.getSex().name(), familyRevision2.getSex().name());
        assertFullName(familyRevision1.getFullName(), familyRevision2.getFullName());
        assertFullName(familyRevision1.getRelative(), familyRevision2.getRelative());
        assertAge(familyRevision1.getAge(), familyRevision2.getAge());
        assertAge(familyRevision1.getAgeInNextRevision(), familyRevision2.getAgeInNextRevision());
        assertEquals(familyRevision1.getAnotherNames(), familyRevision2.getAnotherNames());
        if (familyRevision1.getPartner() == null) {
            assertNull(familyRevision2.getPartner());
        }
    }

    private List<genealogy.visualizer.entity.FamilyRevision> generateFamilyRevisionList(List<genealogy.visualizer.entity.FamilyRevision> familyRevisionList) {
        List<genealogy.visualizer.entity.Archive> archives = archiveRepository.saveAllAndFlush(
                familyRevisionList.stream()
                        .map(familyRevision -> familyRevision.getArchiveDocument().getArchive())
                        .toList());
        archiveIds.addAll(archives.stream()
                .map(genealogy.visualizer.entity.Archive::getId)
                .toList());
        List<genealogy.visualizer.entity.ArchiveDocument> archiveDocuments = archiveDocumentRepository.saveAllAndFlush(
                familyRevisionList.stream()
                        .map(genealogy.visualizer.entity.FamilyRevision::getArchiveDocument)
                        .toList());
        archiveDocumentIds.addAll(archiveDocuments.stream()
                .map(genealogy.visualizer.entity.ArchiveDocument::getId)
                .toList());
        familyRevisionList = familyRevisionRepository.saveAllAndFlush(familyRevisionList);
        familyRevisionIds.addAll(familyRevisionList.stream()
                .map(genealogy.visualizer.entity.FamilyRevision::getId)
                .toList());
        return familyRevisionList;
    }
}