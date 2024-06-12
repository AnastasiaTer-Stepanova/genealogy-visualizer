package genealogy.visualizer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ArchiveDocumentControllerTest.assertArchiveDocument;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveControllerTest extends IntegrationTest {

    private static final String PATH = "/archive";

    @Test
    void saveTest() throws Exception {
        Archive archiveSave = generator.nextObject(Archive.class);
        List<EasyArchiveDocument> archiveDocumentsSave = generator.objects(EasyArchiveDocument.class, generator.nextInt(5, 10)).toList();
        archiveSave.setArchiveDocuments(archiveDocumentsSave);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(archiveSave));
        Archive response = getArchiveFromJson(responseJson);
        assertNotNull(response);
        assertArchive(response, archiveSave);
        String responseGetJson = getRequest(PATH + "/" + response.getId());
        Archive responseGet = getArchiveFromJson(responseGetJson);
        assertNotNull(responseGet);
        assertArchive(responseGet, archiveSave);
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Archive archiveExist = generateRandomExistArchive();
        String responseJson = getRequest(PATH + "/" + archiveExist.getId());
        Archive response = getArchiveFromJson(responseJson);
        assertNotNull(response);
        assertEquals(response.getId(), archiveExist.getId());
        assertArchive(response, archiveExist);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Archive archiveExist = generateRandomExistArchive();
        Archive archiveUpdate = generator.nextObject(Archive.class);
        archiveUpdate.setId(archiveExist.getId());
        List<EasyArchiveDocument> archiveDocumentsUpdates = new ArrayList<>(generator.objects(EasyArchiveDocument.class, generator.nextInt(3, 7)).toList());
        for (genealogy.visualizer.entity.ArchiveDocument ad : archiveExist.getArchiveDocuments()) {
            if (generator.nextBoolean()) {
                archiveDocumentsUpdates.add(easyArchiveDocumentMapper.toDTO(ad));
            }
        }
        archiveUpdate.setArchiveDocuments(archiveDocumentsUpdates);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(archiveUpdate));
        Archive response = getArchiveFromJson(responseJson);
        assertNotNull(response);
        assertArchive(response, archiveUpdate);
        String responseGetJson = getRequest(PATH + "/" + response.getId());
        Archive responseGet = getArchiveFromJson(responseGetJson);
        assertNotNull(responseGet);
        assertArchive(responseGet, archiveUpdate);
    }

    @Test
    void deleteExistingTest() throws Exception {
        genealogy.visualizer.entity.Archive archiveExist = generateRandomExistArchive();
        String responseJson = deleteRequest(PATH + "/" + archiveExist.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(archiveRepository.findById(archiveExist.getId()).isEmpty());
        for (genealogy.visualizer.entity.ArchiveDocument ad : archiveExist.getArchiveDocuments()) {
            assertFalse(archiveDocumentRepository.findById(ad.getId()).isEmpty());
        }
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(5);
        ArchiveFilter filter = new ArchiveFilter().abbreviation("ГАРО").name("гос архив");
        List<genealogy.visualizer.entity.Archive> archivesSave = generator.objects(genealogy.visualizer.entity.Archive.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Archive archive : archivesSave) {
            byte localCount = 0;
            archive.setId(null);
            archive.setArchiveDocuments(null);
            if (generator.nextBoolean()) {
                archive.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getName() : filter.getName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                localCount++;
            }
            if (generator.nextBoolean()) {
                archive.setAbbreviation(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getAbbreviation() : filter.getAbbreviation().toLowerCase()) +
                        stringRandomizer.getRandomValue());
                localCount++;
            }
            if (localCount == 2) {
                count++;
            }
        }
        List<genealogy.visualizer.entity.Archive> archivesExist = archiveRepository.saveAllAndFlush(archivesSave);
        archivesExist.forEach(archive -> archiveIds.add(archive.getId()));
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyArchive> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyArchive.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyArchive::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Archive archive : archivesExist) {
            if (archive.getAbbreviation().toLowerCase().contains(filter.getAbbreviation().toLowerCase()) &&
                    archive.getName().toLowerCase().contains(filter.getName().toLowerCase())) {
                assertTrue(findIds.contains(archive.getId()));
            }
        }
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        super.tearDown();
    }

    private Archive getArchiveFromJson(String responseJson) throws JsonProcessingException {
        Archive response = objectMapper.readValue(responseJson, Archive.class);
        if (response != null) {
            if (response.getArchiveDocuments() != null) {
                response.getArchiveDocuments().forEach(archiveDocument -> archiveDocumentIds.add(archiveDocument.getId()));
            }
            archiveIds.add(response.getId());
        }
        return response;
    }

    protected static void assertArchive(Archive archive1, Archive archive2) {
        assertNotNull(archive1);
        assertNotNull(archive2);
        if (archive1.getId() != null && archive2.getId() != null) {
            assertEquals(archive1.getId(), archive2.getId());
        }
        assertEquals(archive1.getName(), archive2.getName());
        assertEquals(archive1.getAbbreviation(), archive2.getAbbreviation());
        assertEquals(archive1.getAddress(), archive2.getAddress());
        assertEquals(archive1.getComment(), archive2.getComment());
        assertEquals(archive1.getArchiveDocuments().size(), archive2.getArchiveDocuments().size());
        archive1.getArchiveDocuments().sort(Comparator.comparing(EasyArchiveDocument::getName));
        List<EasyArchiveDocument> sortedArchive2 = archive2.getArchiveDocuments().stream()
                .sorted(Comparator.comparing(EasyArchiveDocument::getName))
                .toList();
        for (int i = 0; i < archive1.getArchiveDocuments().size(); i++) {
            assertArchiveDocument(archive1.getArchiveDocuments().get(i), sortedArchive2.get(i));
        }
    }

    protected static void assertArchive(EasyArchive archive1, EasyArchive archive2) {
        if (archive1 == null || archive2 == null) {
            assertNull(archive1);
            assertNull(archive2);
            return;
        }
        assertNotNull(archive1);
        assertNotNull(archive2);
        if (archive1.getId() != null && archive2.getId() != null) {
            assertEquals(archive1.getId(), archive2.getId());
        }
        assertEquals(archive1.getName(), archive2.getName());
        assertEquals(archive1.getAbbreviation(), archive2.getAbbreviation());
        assertEquals(archive1.getAddress(), archive2.getAddress());
        assertEquals(archive1.getComment(), archive2.getComment());
    }

    protected static void assertArchive(Archive archive1, genealogy.visualizer.entity.Archive archive2) {
        assertNotNull(archive1);
        assertNotNull(archive2);
        if (archive1.getId() != null && archive2.getId() != null) {
            assertEquals(archive1.getId(), archive2.getId());
        }
        assertEquals(archive1.getName(), archive2.getName());
        assertEquals(archive1.getAbbreviation(), archive2.getAbbreviation());
        assertEquals(archive1.getAddress(), archive2.getAddress());
        assertEquals(archive1.getComment(), archive2.getComment());
        assertEquals(archive1.getArchiveDocuments().size(), archive2.getArchiveDocuments().size());
        archive1.getArchiveDocuments().sort(Comparator.comparing(EasyArchiveDocument::getName));
        List<genealogy.visualizer.entity.ArchiveDocument> sortedArchive2 = archive2.getArchiveDocuments().stream()
                .sorted(Comparator.comparing(genealogy.visualizer.entity.ArchiveDocument::getName))
                .toList();
        for (int i = 0; i < archive1.getArchiveDocuments().size(); i++) {
            assertArchiveDocument(archive1.getArchiveDocuments().get(i), sortedArchive2.get(i));
        }
    }

    protected static void assertArchive(EasyArchive archive1, genealogy.visualizer.entity.Archive archive2) {
        assertNotNull(archive1);
        assertNotNull(archive2);
        if (archive1.getId() != null && archive2.getId() != null) {
            assertEquals(archive1.getId(), archive2.getId());
        }
        assertEquals(archive1.getName(), archive2.getName());
        assertEquals(archive1.getAbbreviation(), archive2.getAbbreviation());
        assertEquals(archive1.getAddress(), archive2.getAddress());
        assertEquals(archive1.getComment(), archive2.getComment());
    }

    private genealogy.visualizer.entity.Archive generateRandomExistArchive() {
        genealogy.visualizer.entity.Archive archiveSave = generator.nextObject(genealogy.visualizer.entity.Archive.class);
        archiveSave.setId(null);
        archiveSave.setArchiveDocuments(null);
        genealogy.visualizer.entity.Archive archiveExist = archiveRepository.saveAndFlush(archiveSave);
        archiveIds.add(archiveExist.getId());
        List<genealogy.visualizer.entity.ArchiveDocument> archiveDocumentsSave = generator.objects(genealogy.visualizer.entity.ArchiveDocument.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.ArchiveDocument> archiveDocumentsExist = new ArrayList<>(archiveDocumentsSave.size());
        for (genealogy.visualizer.entity.ArchiveDocument ad : archiveDocumentsSave) {
            ad.setArchive(archiveSave);
            ad.setId(null);
            genealogy.visualizer.entity.ArchiveDocument result = archiveDocumentRepository.saveAndFlush(ad);
            archiveDocumentIds.add(result.getId());
            archiveDocumentsExist.add(result);
        }
        archiveExist.setArchiveDocuments(archiveDocumentsExist);
        return archiveExist;
    }
}
