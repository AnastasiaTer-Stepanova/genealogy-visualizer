package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.ArchiveWithFamilyMembers;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.FamilyFilter;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.api.model.FullNameFilter;
import genealogy.visualizer.api.model.Sex;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.model.exception.NotFoundException;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ArchiveDocumentControllerTest.assertArchiveDocument;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FamilyRevisionControllerTest extends IntegrationTest {

    @Autowired
    private FamilyRevisionMapper familyRevisionMapper;

    private static final String PATH = "/family-revision";

    @Test
    void saveTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        EasyArchiveDocument archiveDocumentSave = generator.nextObject(EasyArchiveDocument.class);
        revisionSave.setId(null);
        revisionSave.setArchiveDocument(archiveDocumentSave);
        EasyFamilyMember partnerSave = generator.nextObject(EasyFamilyMember.class);
        partnerSave.setId(null);
        revisionSave.setPartner(partnerSave);
        EasyPerson personSave = generator.nextObject(EasyPerson.class);
        personSave.setId(null);
        revisionSave.setPerson(personSave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(revisionSave));
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        FamilyMember responseGet = objectMapper.readValue(responseJsonGet, FamilyMember.class);
        assertNotNull(responseGet);
        assertFamilyRevision(responseGet, revisionSave);
    }

    @Test
    void saveWithPartnerTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        EasyFamilyMember revisionPartnerSave = generator.nextObject(EasyFamilyMember.class);
        revisionPartnerSave.setId(null);
        revisionSave.setArchiveDocument(easyArchiveDocumentMapper.toDTO(archiveDocumentExisting));
        revisionSave.setPartner(revisionPartnerSave);
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
        assertNotNull(revisionSave.getPartner());
        assertNotNull(response.getPartner());
        assertFamilyRevision(response.getPartner(), revisionSave.getPartner());
    }

    @Test
    void saveWithExistingArchiveTest() throws Exception {
        EasyArchiveDocument archiveDocumentSave = generator.nextObject(EasyArchiveDocument.class);
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        revisionSave.setArchiveDocument(archiveDocumentSave);
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void saveWithExistingArchiveDocumentTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setId(null);
        revisionSave.setArchiveDocument(easyArchiveDocumentMapper.toDTO(archiveDocumentExisting));
        String requestJson = objectMapper.writeValueAsString(revisionSave);
        String responseJson = postRequest(PATH, requestJson);
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAndFlush(revisionSave);
        String responseJson = getRequest(PATH + "/" + revisionExist.getId());
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertEquals(response.getId(), revisionExist.getId());
        assertFamilyRevision(response, revisionExist);
    }

    @Test
    void getByIdExceptionTest() throws Exception {
        String responseJson = getNotFoundRequest(PATH + "/" + generator.nextInt(10000, 20000));
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
        assertEquals(response.getMessage(), NotFoundException.MESSAGE);
    }

    @Test
    void updateParentExistingTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAndFlush(revisionSave);
        FamilyMember revisionUpdate = generator.nextObject(FamilyMember.class);
        revisionUpdate.setId(revisionExist.getId());
        EasyFamilyMember revisionPartnerUpdate = generator.nextObject(EasyFamilyMember.class);
        revisionUpdate.setPartner(revisionPartnerUpdate);
        revisionUpdate.setAnotherNames(generator.objects(String.class, 4).toList());
        EasyPerson personUpdate = generator.nextObject(EasyPerson.class);
        revisionUpdate.setPerson(personUpdate);
        EasyArchiveDocument archiveDocumentUpdate = generator.nextObject(EasyArchiveDocument.class);
        revisionUpdate.setArchiveDocument(archiveDocumentUpdate);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(revisionUpdate));
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertFamilyRevision(response, revisionUpdate);
        assertNotNull(revisionUpdate.getPartner());
        assertNotNull(response.getPartner());
        assertFamilyRevision(response.getPartner(), revisionUpdate.getPartner());
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        FamilyMember responseGet = objectMapper.readValue(responseJsonGet, FamilyMember.class);
        assertNotNull(responseGet);
        assertFamilyRevision(responseGet, revisionUpdate);
    }

    @Test
    void updateNotExistingTest() throws Exception {
        FamilyMember revisionSave = generator.nextObject(FamilyMember.class);
        revisionSave.setArchiveDocument(easyArchiveDocumentMapper.toDTO(archiveDocumentExisting));
        revisionSave.setId(generator.nextLong(20000, 30000));
        String objectString = objectMapper.writeValueAsString(revisionSave);
        String responseJson = putNotFoundRequest(PATH, objectString);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
        assertEquals(response.getMessage(), "Family revision for update not found");
    }

    @Test
    void updateAnotherNamesExistingTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        revisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        genealogy.visualizer.entity.FamilyRevision partnerRevisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        partnerRevisionSave.setArchiveDocument(archiveDocumentExisting);
        partnerRevisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        revisionSave.setPartner(partnerRevisionSave);
        partnerRevisionSave.setPartner(revisionSave);
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAllAndFlush(List.of(revisionSave, partnerRevisionSave)).getFirst();
        FamilyMember revisionUpdate = familyRevisionMapper.toDTO(revisionExist);
        revisionUpdate.setPartner(null);
        revisionUpdate.setAnotherNames(List.of(revisionUpdate.getAnotherNames().getFirst(), generator.nextObject(String.class)));
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(revisionUpdate));
        FamilyMember response = objectMapper.readValue(responseJson, FamilyMember.class);
        assertNotNull(response);
        assertFamilyRevision(response, revisionUpdate);
        assertNull(revisionUpdate.getPartner());
        assertFamilyRevision(response.getPartner(), revisionUpdate.getPartner());
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        FamilyMember responseGet = objectMapper.readValue(responseJsonGet, FamilyMember.class);
        assertNotNull(responseGet);
        assertFamilyRevision(responseGet, revisionUpdate);
    }

    @Test
    void deleteExistingTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        revisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        genealogy.visualizer.entity.FamilyRevision partnerRevisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        partnerRevisionSave.setArchiveDocument(archiveDocumentExisting);
        partnerRevisionSave.setAnotherNames(generator.objects(String.class, 4).toList());
        revisionSave.setPartner(partnerRevisionSave);
        partnerRevisionSave.setPartner(revisionSave);
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAllAndFlush(List.of(revisionSave, partnerRevisionSave)).getFirst();
        String responseJson = deleteRequest(PATH + "/" + revisionSave.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(familyRevisionRepository.findById(revisionExist.getId()).isEmpty());
        assertFalse(familyRevisionRepository.findById(revisionExist.getPartner().getId()).isEmpty());
        assertTrue(familyRevisionRepository.findAnotherNames(revisionExist.getId()).isEmpty());
        assertFalse(archiveDocumentRepository.findById(revisionExist.getArchiveDocument().getId()).isEmpty());
        if (revisionExist.getArchiveDocument().getArchive() != null) {
            assertFalse(archiveRepository.findById(revisionExist.getArchiveDocument().getArchive().getId()).isEmpty());
        }
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
                        familyRevision.setArchiveDocument(archiveDocumentExisting);
                        familyRevision.setFamilyRevisionNumber(familyRevisionNumber);
                    }
                    if (generator.nextBoolean()) {
                        familyRevision.setFamilyRevisionNumber(familyRevisionNumber);
                    }
                    if (generator.nextBoolean()) {
                        familyRevision.setFamilyRevisionNumber(familyRevisionNumber);
                    }
                })
                .toList();
        familyRevisionList = generateFamilyRevisionList(familyRevisionList);
        FamilyFilter filterRequest = new FamilyFilter((int) familyRevisionNumber, archiveDocumentExisting.getId(), false, true);
        String requestJson = objectMapper.writeValueAsString(filterRequest);
        String responseJson = getRequest(PATH + "/family", requestJson);
        List<FamilyMemberFullInfo> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, FamilyMemberFullInfo.class));
        assertNotNull(response);
        List<genealogy.visualizer.entity.FamilyRevision> existFamilyRevisionList = familyRevisionList.stream()
                .filter(familyRevision -> familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumber) &&
                        familyRevision.getArchiveDocument().getId().equals(archiveDocumentExisting.getId()))
                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId()))
                .toList();
        assertEquals(existFamilyRevisionList.size(), response.size());
        List<EasyFamilyMember> responseFamilyRevisions = response.stream()
                .map(FamilyMemberFullInfo::getFamilyMember)
                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId())).toList();
        assertEquals(existFamilyRevisionList.size(), responseFamilyRevisions.size());
        for (int i = 0; i < existFamilyRevisionList.size(); i++) {
            assertFamilyRevision(responseFamilyRevisions.get(i), existFamilyRevisionList.get(i));
        }
    }

    @Test
    void findFamilyRevisionNullTest() throws Exception {
        generateFamilyRevisionList(generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(10, 15)).toList());
        FamilyFilter filterRequest = new FamilyFilter((int) (short) generator.nextInt(10000, 20000), archiveDocumentExisting.getId(), false, true);
        String objectString = objectMapper.writeValueAsString(filterRequest);
        String responseJson = getNotFoundRequest(PATH + "/family", objectString);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
        assertEquals(response.getMessage(), NotFoundException.MESSAGE);
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
        genealogy.visualizer.entity.ArchiveDocument adParent = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adParent.setArchive(archiveExisting);
        adParent = archiveDocumentRepository.saveAndFlush(adParent);
        Long adParentId = adParent.getId();
        genealogy.visualizer.entity.ArchiveDocument adChildLevel1 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adChildLevel1.setNextRevision(adParent);
        adChildLevel1.setArchive(archiveExisting);
        adChildLevel1 = archiveDocumentRepository.saveAndFlush(adChildLevel1);
        Long adChildLevel1Id = adChildLevel1.getId();
        genealogy.visualizer.entity.ArchiveDocument adSecondChildLevel1 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adSecondChildLevel1.setNextRevision(adParent);
        adSecondChildLevel1.setArchive(archiveExisting);
        adSecondChildLevel1 = archiveDocumentRepository.saveAndFlush(adSecondChildLevel1);
        Long adSecondChildLevel1Id = adSecondChildLevel1.getId();
        genealogy.visualizer.entity.ArchiveDocument adChildLevel2 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adChildLevel2.setNextRevision(adChildLevel1);
        adChildLevel2.setArchive(archiveExisting);
        adChildLevel2 = archiveDocumentRepository.saveAndFlush(adChildLevel2);
        Long adChildLevel2Id = adChildLevel2.getId();
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
        FamilyFilter filterRequest = new FamilyFilter((int) familyRevisionNumberSecondChildLeve1, adSecondChildLevel1Id, true, isFindWithHavePerson);
        String requestJson = objectMapper.writeValueAsString(filterRequest);
        String responseJson = getRequest(PATH + "/family", requestJson);
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
                List<EasyFamilyMember> familyMembers = family.getFamilies()
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
                    assertFamilyRevision(existFamilyRevision.get(j), familyMembers.get(j));
                }
            }
        }
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        FullNameFilter fullNameFilter = new FullNameFilter()
                .name("Иван")
                .surname("Иванович")
                .lastName("Иванов");
        FamilyMemberFilter filter = new FamilyMemberFilter()
                .fullName(fullNameFilter)
                .archiveDocumentId(archiveDocumentExisting.getId())
                .familyRevisionNumber(40)
                .sex(Sex.MALE);
        List<genealogy.visualizer.entity.FamilyRevision> familyRevisionsSave = generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.FamilyRevision familyRevision : familyRevisionsSave) {
            familyRevision.setPerson(null);
            familyRevision.setPartner(null);
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
                familyRevision.setFullName(fullName);
                familyRevision.setSex(genealogy.visualizer.entity.enums.Sex.MALE);
                familyRevision.setFamilyRevisionNumber(filter.getFamilyRevisionNumber().shortValue());
                familyRevision.setArchiveDocument(archiveDocumentExisting);
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
                familyRevision.setArchiveDocument(archiveDocumentExist);
            }
        }
        List<genealogy.visualizer.entity.FamilyRevision> familyRevisionsExist = familyRevisionRepository.saveAllAndFlush(familyRevisionsSave);
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyFamilyMember> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyFamilyMember.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyFamilyMember::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.FamilyRevision familyRevision : familyRevisionsExist) {
            if (familyRevision.getFullName().getName().toLowerCase().contains(filter.getFullName().getName().toLowerCase()) &&
                    familyRevision.getFullName().getSurname().toLowerCase().contains(filter.getFullName().getSurname().toLowerCase()) &&
                    familyRevision.getFullName().getLastName().toLowerCase().contains(filter.getFullName().getLastName().toLowerCase()) &&
                    familyRevision.getArchiveDocument().getId().equals(filter.getArchiveDocumentId()) &&
                    familyRevision.getFamilyRevisionNumber().intValue() == filter.getFamilyRevisionNumber() &&
                    familyRevision.getSex().name().equals(filter.getSex().name())) {
                assertTrue(findIds.contains(familyRevision.getId()));
            }
        }
    }

    @Test
    void saveUnauthorizedTest() throws Exception {
        FamilyMember object = generator.nextObject(FamilyMember.class);
        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void updateUnauthorizedTest() throws Exception {
        FamilyMember object = generator.nextObject(FamilyMember.class);
        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        FamilyMember object = generator.nextObject(FamilyMember.class);
        deleteUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        familyRevisionRepository.deleteAll();
        personRepository.deleteAll();
        super.tearDown();
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
        if (familyRevision == null || easyFamilyMember == null) {
            assertNull(familyRevision);
            assertNull(easyFamilyMember);
            return;
        }
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
        assertEquals(familyRevision.getSex(), easyFamilyMember.getSex());
        assertEquals(familyRevision.getFullName(), easyFamilyMember.getFullName());
        assertEquals(familyRevision.getRelative(), easyFamilyMember.getRelative());
        assertEquals(familyRevision.getAge(), easyFamilyMember.getAge());
        assertEquals(familyRevision.getAgeInNextRevision(), easyFamilyMember.getAgeInNextRevision());
        assertEquals(familyRevision.getAnotherNames(), easyFamilyMember.getAnotherNames());
        assertNotNull(familyRevision.getId());
    }

    protected static void assertFamilyRevision(EasyFamilyMember familyRevision1, genealogy.visualizer.entity.FamilyRevision familyRevision2) {
        assertNotNull(familyRevision1);
        assertNotNull(familyRevision2);
        assertEquals(familyRevision1.getFamilyRevisionNumber(), familyRevision2.getFamilyRevisionNumber().intValue());
        assertEquals(familyRevision1.getNextFamilyRevisionNumber(), familyRevision2.getNextFamilyRevisionNumber().intValue());
        assertEquals(familyRevision1.getListNumber(), familyRevision2.getListNumber().intValue());
        assertEquals(familyRevision1.getDeparted(), familyRevision2.getDeparted());
        assertEquals(familyRevision1.getArrived(), familyRevision2.getArrived());
        assertEquals(familyRevision1.getIsHeadOfYard(), familyRevision2.getHeadOfYard());
        assertEquals(familyRevision1.getFamilyGeneration(), familyRevision2.getFamilyGeneration().intValue());
        assertEquals(familyRevision1.getComment(), familyRevision2.getComment());
        assertEquals(familyRevision1.getSex().name(), familyRevision2.getSex().name());
        assertFullName(familyRevision1.getFullName(), familyRevision2.getFullName());
        assertFullName(familyRevision1.getRelative(), familyRevision2.getRelative());
        assertAge(familyRevision1.getAge(), familyRevision2.getAge());
        assertAge(familyRevision1.getAgeInNextRevision(), familyRevision2.getAgeInNextRevision());
        assertEquals(familyRevision1.getAnotherNames(), familyRevision2.getAnotherNames());
    }

    protected static void assertFamilyRevision(genealogy.visualizer.entity.FamilyRevision familyRevision, EasyFamilyMember easyFamilyMember) {
        assertNotNull(familyRevision);
        assertNotNull(easyFamilyMember);
        assertEquals(familyRevision.getFamilyRevisionNumber().intValue(), easyFamilyMember.getFamilyRevisionNumber());
        assertEquals(familyRevision.getNextFamilyRevisionNumber().intValue(), easyFamilyMember.getNextFamilyRevisionNumber());
        assertEquals(familyRevision.getListNumber().intValue(), easyFamilyMember.getListNumber());
        assertEquals(familyRevision.getDeparted(), easyFamilyMember.getDeparted());
        assertEquals(familyRevision.getArrived(), easyFamilyMember.getArrived());
        assertEquals(familyRevision.getHeadOfYard(), easyFamilyMember.getIsHeadOfYard());
        assertEquals(familyRevision.getFamilyGeneration().intValue(), easyFamilyMember.getFamilyGeneration());
        assertEquals(familyRevision.getComment(), easyFamilyMember.getComment());
        assertEquals(familyRevision.getSex().name(), easyFamilyMember.getSex().name());
        assertFullName(easyFamilyMember.getFullName(), familyRevision.getFullName());
        assertFullName(easyFamilyMember.getRelative(), familyRevision.getRelative());
        assertAge(easyFamilyMember.getAge(), familyRevision.getAge());
        assertAge(easyFamilyMember.getAgeInNextRevision(), familyRevision.getAgeInNextRevision());
        assertEquals(familyRevision.getAnotherNames().size(), easyFamilyMember.getAnotherNames().size());
        if (!familyRevision.getAnotherNames().isEmpty()) {
            familyRevision.getAnotherNames().forEach(an ->
                    assertTrue(easyFamilyMember.getAnotherNames().contains(an))
            );
        }
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
        archiveRepository.saveAllAndFlush(
                familyRevisionList.stream()
                        .map(familyRevision -> familyRevision.getArchiveDocument().getArchive())
                        .filter(Objects::nonNull)
                        .toList());
        archiveDocumentRepository.saveAllAndFlush(
                familyRevisionList.stream()
                        .map(genealogy.visualizer.entity.FamilyRevision::getArchiveDocument)
                        .filter(Objects::nonNull)
                        .toList());
        familyRevisionList = familyRevisionRepository.saveAllAndFlush(familyRevisionList);
        return familyRevisionList;
    }
}