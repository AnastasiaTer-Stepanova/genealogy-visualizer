package genealogy.visualizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevision;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevisionList;
import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionFilter;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FamilyRevisionControllerTest extends IntegrationTest {

    @Autowired
    private FamilyRevisionRepository familyRevisionRepository;

    @Autowired
    private FamilyRevisionMapper familyRevisionMapper;

    private final Set<Long> familyRevisionIds = new HashSet<>();

    @Test
    void saveTest() throws Exception {
        Archive archiveSave = generator.nextObject(Archive.class);
        ArchiveDocument archiveDocumentSave = generator.nextObject(ArchiveDocument.class);
        archiveDocumentSave.setArchive(archiveSave);
        FamilyRevisionSave revisionSave = generator.nextObject(FamilyRevisionSave.class);
        revisionSave.setArchiveDocument(archiveDocumentSave);
        String objectString = objectMapper.writeValueAsString(revisionSave);
        String responseJson = mockMvc.perform(
                        post("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void saveWithPartnerTest() throws Exception {
        FamilyRevisionSave revisionSave = generator.nextObject(FamilyRevisionSave.class);
        FamilyRevision revisionPartnerSave = generator.nextObject(FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        revisionPartnerSave.setArchiveDocument(archiveDocumentExisting);
        revisionSave.setPartner(revisionPartnerSave);
        String objectString = objectMapper.writeValueAsString(revisionSave);
        String responseJson = mockMvc.perform(
                        post("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
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
        FamilyRevisionSave revisionSave = generator.nextObject(FamilyRevisionSave.class);
        revisionSave.setArchiveDocument(archiveDocumentSave);
        String objectString = objectMapper.writeValueAsString(revisionSave);
        String responseJson = mockMvc.perform(
                        post("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void saveWithExistingArchiveDocumentTest() throws Exception {
        FamilyRevisionSave revisionSave = generator.nextObject(FamilyRevisionSave.class);
        revisionSave.setArchiveDocument(archiveDocumentExisting);
        String objectString = objectMapper.writeValueAsString(revisionSave);
        String responseJson = mockMvc.perform(
                        post("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionSave);
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionSave = generator.nextObject(genealogy.visualizer.entity.FamilyRevision.class);
        revisionSave.setArchiveDocument(archiveDocumentMapper.toEntity(archiveDocumentExisting));
        genealogy.visualizer.entity.FamilyRevision revisionExist = familyRevisionRepository.saveAndFlush(revisionSave);
        String responseJson = mockMvc.perform(
                        get("/family-revision/" + revisionExist.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertEquals(response.getId(), revisionExist.getId());
        assertFamilyRevision(response, revisionExist);
    }

    @Test
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
        FamilyRevision revisionUpdate = familyRevisionMapper.toDTO(revisionExist, new CycleAvoidingMappingContext());
        FamilyRevision revisionPartnerSave = generator.nextObject(FamilyRevision.class);
        revisionPartnerSave.setArchiveDocument(archiveDocumentExisting);
        revisionUpdate.setPartner(revisionPartnerSave);
        revisionUpdate.setAnotherNames(generator.objects(String.class, 4).toList());
        String objectString = objectMapper.writeValueAsString(revisionUpdate);
        String responseJson = mockMvc.perform(
                        put("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
        assertNotNull(response);
        assertFamilyRevision(response, revisionUpdate);
        assertNotNull(revisionUpdate.getPartner());
        assertNotNull(response.getPartner());
        assertFamilyRevision(response.getPartner(), revisionUpdate.getPartner());
    }

    @Test
    void updateNotExistingTest() throws Exception {
        FamilyRevision revisionSave = generator.nextObject(FamilyRevision.class);
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
        FamilyRevision revisionUpdate = familyRevisionMapper.toDTO(revisionExist, new CycleAvoidingMappingContext());
        revisionUpdate.setPartner(null);
        revisionUpdate.setAnotherNames(List.of(revisionUpdate.getAnotherNames().getFirst(), generator.nextObject(String.class)));
        String objectString = objectMapper.writeValueAsString(revisionUpdate);
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        put("/family-revision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        FamilyRevision response = getFamilyRevisionFromJson(responseJson);
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
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        delete("/family-revision/" + revisionExist.getId()))
                .andExpectAll(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
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
        System.out.println("----------------------Start request------------------------");
        String responseJson = mockMvc.perform(
                        delete("/family-revision/" + generator.nextInt(10000, 20000)))
                .andExpectAll(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        System.out.println("----------------------End request------------------------");
        assertTrue(responseJson.isEmpty());
    }

    @Test
    void findArchiveWithFamilyRevisionTest() throws Exception {
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
        FamilyRevisionFilter filterRequest = new FamilyRevisionFilter((int) familyRevisionNumber, archiveDocumentExisting, false);
        String objectString = objectMapper.writeValueAsString(filterRequest);
        String responseJson = mockMvc.perform(
                        post("/family-revision/family")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ArchiveWithFamilyRevisionList response = objectMapper.readValue(responseJson, ArchiveWithFamilyRevisionList.class);
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        ArchiveWithFamilyRevision archiveWithFamilyRevision = response.getData().getFirst();
        assertNotNull(archiveWithFamilyRevision.getArchive());
        assertNotNull(archiveWithFamilyRevision.getFamilies());
        assertArchiveDocument(archiveDocumentExisting, archiveWithFamilyRevision.getArchive());
        List<genealogy.visualizer.entity.FamilyRevision> excistFamilyRevisionList = familyRevisionList.stream()
                .filter(familyRevision -> familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumber) &&
                        familyRevision.getArchiveDocument().getId().equals(archiveDocumentExisting.getId()))
                .sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId()))
                .toList();
        List<FamilyRevision> responseFamilyRevisions = archiveWithFamilyRevision.getFamilies().stream().sorted((fr1, fr2) -> fr2.getId().compareTo(fr1.getId())).toList();
        assertEquals(excistFamilyRevisionList.size(), responseFamilyRevisions.size());
        for (int i = 0; i < excistFamilyRevisionList.size(); i++) {
            assertFamilyRevision(responseFamilyRevisions.get(i), excistFamilyRevisionList.get(i));
        }
    }

    @Test
    void findArchiveWithFamilyRevisionNullTest() throws Exception {
        generateFamilyRevisionList(generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(10, 15)).toList());
        FamilyRevisionFilter filterRequest = new FamilyRevisionFilter((int) (short) generator.nextInt(10000, 20000), archiveDocumentExisting, false);
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
    void findArchiveWithFamilyRevisionWithFindAllTest() throws Exception {
        generateFamilyRevisionList(generator.objects(genealogy.visualizer.entity.FamilyRevision.class, generator.nextInt(10, 15)).toList());
        FamilyRevisionFilter filterRequest = new FamilyRevisionFilter((int) (short) generator.nextInt(10000, 20000), archiveDocumentExisting, true);
        String objectString = objectMapper.writeValueAsString(filterRequest);
        String responseJson = mockMvc.perform(
                        post("/family-revision/family")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectString))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getCode(), HttpStatus.BAD_REQUEST.value());
        assertEquals(response.getMessage(), "Переданы некорректные данные");
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        familyRevisionRepository.deleteAllById(familyRevisionIds);
        super.tearDown();
    }

    private FamilyRevision getFamilyRevisionFromJson(String responseJson) throws JsonProcessingException {
        FamilyRevision response = objectMapper.readValue(responseJson, FamilyRevision.class);
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

    private void assertFamilyRevision(FamilyRevision familyRevision, FamilyRevisionSave familyRevisionSave) {
        assertNotNull(familyRevision);
        assertNotNull(familyRevisionSave);
        assertEquals(familyRevision.getFamilyRevisionNumber(), familyRevisionSave.getFamilyRevisionNumber());
        assertEquals(familyRevision.getPreviousFamilyRevisionNumber(), familyRevisionSave.getPreviousFamilyRevisionNumber());
        assertEquals(familyRevision.getNextFamilyRevisionNumber(), familyRevisionSave.getNextFamilyRevisionNumber());
        assertEquals(familyRevision.getListNumber(), familyRevisionSave.getListNumber());
        assertEquals(familyRevision.getDeparted(), familyRevisionSave.getDeparted());
        assertEquals(familyRevision.getArrived(), familyRevisionSave.getArrived());
        assertEquals(familyRevision.getIsHeadOfYard(), familyRevisionSave.getIsHeadOfYard());
        assertEquals(familyRevision.getFamilyGeneration(), familyRevisionSave.getFamilyGeneration());
        assertEquals(familyRevision.getComment(), familyRevisionSave.getComment());
        assertArchiveDocument(familyRevision.getArchiveDocument(), familyRevisionSave.getArchiveDocument());
        assertEquals(familyRevision.getSex(), familyRevisionSave.getSex());
        assertEquals(familyRevision.getFullName(), familyRevisionSave.getFullName());
        assertEquals(familyRevision.getRelative(), familyRevisionSave.getRelative());
        assertEquals(familyRevision.getAge(), familyRevisionSave.getAge());
        assertEquals(familyRevision.getAgeInPreviousRevision(), familyRevisionSave.getAgeInPreviousRevision());
        assertEquals(familyRevision.getAgeInNextRevision(), familyRevisionSave.getAgeInNextRevision());
        assertEquals(familyRevision.getAnotherNames(), familyRevisionSave.getAnotherNames());
        if (familyRevisionSave.getPartner() == null) {
            assertNull(familyRevision.getPartner());
        }
        assertNotNull(familyRevision.getId());
    }

    private void assertFamilyRevision(FamilyRevision familyRevision1, FamilyRevision familyRevision2) {
        assertNotNull(familyRevision1);
        assertNotNull(familyRevision2);
        assertEquals(familyRevision1.getFamilyRevisionNumber(), familyRevision2.getFamilyRevisionNumber());
        assertEquals(familyRevision1.getPreviousFamilyRevisionNumber(), familyRevision2.getPreviousFamilyRevisionNumber());
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
        assertAge(familyRevision1.getAgeInPreviousRevision(), familyRevision2.getAgeInPreviousRevision());
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

    private void assertFamilyRevision(FamilyRevision familyRevision1, genealogy.visualizer.entity.FamilyRevision familyRevision2) {
        assertNotNull(familyRevision1);
        assertNotNull(familyRevision2);
        assertEquals(familyRevision1.getFamilyRevisionNumber(), (int) familyRevision2.getFamilyRevisionNumber());
        assertEquals(familyRevision1.getPreviousFamilyRevisionNumber(), (int) familyRevision2.getPreviousFamilyRevisionNumber());
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
        assertAge(familyRevision1.getAgeInPreviousRevision(), familyRevision2.getAgeInPreviousRevision());
        assertAge(familyRevision1.getAgeInNextRevision(), familyRevision2.getAgeInNextRevision());
        assertEquals(familyRevision1.getAnotherNames(), familyRevision2.getAnotherNames());
        if (familyRevision1.getPartner() == null) {
            assertNull(familyRevision2.getPartner());
        }
    }

    private List<genealogy.visualizer.entity.FamilyRevision> generateFamilyRevisionList(List<genealogy.visualizer.entity.FamilyRevision> familyRevisionList) {
        List<genealogy.visualizer.entity.Archive> archives = archiveRepository.saveAllAndFlush(
                familyRevisionList.stream().map(familyRevision -> familyRevision.getArchiveDocument().getArchive()).toList());
        archiveIds.addAll(archives.stream().map(genealogy.visualizer.entity.Archive::getId).toList());
        List<genealogy.visualizer.entity.ArchiveDocument> archiveDocuments = archiveDocumentRepository.saveAllAndFlush(
                familyRevisionList.stream().map(genealogy.visualizer.entity.FamilyRevision::getArchiveDocument).toList());
        archiveDocumentIds.addAll(archiveDocuments.stream().map(genealogy.visualizer.entity.ArchiveDocument::getId).toList());
        familyRevisionList = familyRevisionRepository.saveAllAndFlush(familyRevisionList);
        familyRevisionIds.addAll(familyRevisionList.stream().map(genealogy.visualizer.entity.FamilyRevision::getId).toList());
        return familyRevisionList;
    }
}