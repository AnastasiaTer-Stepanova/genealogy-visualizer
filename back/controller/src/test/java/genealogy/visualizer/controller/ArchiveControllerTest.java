package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
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
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Archive archiveExist = existingArchives.stream()
                .filter(e -> e.getArchiveDocuments() != null && !e.getArchiveDocuments().isEmpty()).findAny().orElse(existingArchives.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        Archive response = objectMapper.readValue(getRequest(PATH + "/" + archiveExist.getId()), Archive.class);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertArchive(response, archiveExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        List<EasyArchive> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(archiveFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyArchive.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        List<Long> existIds = existingArchives.stream()
                .filter(a -> containsIgnoreCase(a.getAbbreviation(), archiveFilter.getAbbreviation()) &&
                        containsIgnoreCase(a.getName(), archiveFilter.getName()))
                .map(genealogy.visualizer.entity.Archive::getId)
                .toList();
        Set<Long> findIds = response.stream().map(EasyArchive::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        archiveFilter.setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(archiveFilter));
    }

    @Test
    void saveTest() throws Exception {
        Archive archiveSave = generator.nextObject(Archive.class);
        archiveSave.setArchiveDocuments(generator.objects(EasyArchiveDocument.class, generator.nextInt(5, 10)).toList());
        Archive response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(archiveSave)), Archive.class);
        assertArchive(response, archiveSave);

        Archive responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Archive.class);
        assertArchive(responseGet, archiveSave);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(archiveSave));
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Archive archiveExist = existingArchives.stream()
                .filter(e -> e.getArchiveDocuments() != null && !e.getArchiveDocuments().isEmpty()).findAny().orElse(existingArchives.getFirst());
        Archive archiveUpdate = generator.nextObject(Archive.class);
        archiveUpdate.setId(archiveExist.getId());
        List<EasyArchiveDocument> archiveDocumentsUpdates = new ArrayList<>(generator.objects(EasyArchiveDocument.class, generator.nextInt(3, 7)).toList());
        for (genealogy.visualizer.entity.ArchiveDocument ad : archiveExist.getArchiveDocuments()) {
            if (generator.nextBoolean()) {
                archiveDocumentsUpdates.add(easyArchiveDocumentMapper.toDTO(ad));
            }
        }
        archiveUpdate.setArchiveDocuments(archiveDocumentsUpdates);

        Archive response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(archiveUpdate)), Archive.class);
        assertArchive(response, archiveUpdate);

        Archive responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Archive.class);
        assertArchive(responseGet, archiveUpdate);

        archiveUpdate.setArchiveDocuments(Collections.emptyList());
        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(archiveUpdate)), Archive.class);
        assertArchive(response, archiveUpdate);

        responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Archive.class);
        assertArchive(responseGet, archiveUpdate);

        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(archiveUpdate));
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Archive archiveExist = existingArchives.stream()
                .filter(e -> e.getArchiveDocuments() != null && !e.getArchiveDocuments().isEmpty()).findAny().orElse(existingArchives.getFirst());

        String responseJson = deleteRequest(PATH + "/" + archiveExist.getId());
        existingArchives.remove(archiveExist);

        assertTrue(responseJson.isEmpty());
        assertTrue(archiveRepository.findById(archiveExist.getId()).isEmpty());
        if (archiveExist.getArchiveDocuments() != null) {
            archiveExist.getArchiveDocuments().forEach(ad -> assertFalse(archiveDocumentRepository.findById(ad.getId()).isEmpty()));
        }

        deleteUnauthorizedRequest(PATH + "/" + archiveExist.getId());
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

    protected static void assertArchive(Archive archive1, Archive archive2) {
        assertArchive(toEasyArchive(archive1), toEasyArchive(archive2));
        assertEquals(archive1.getArchiveDocuments().size(), archive2.getArchiveDocuments().size());
        archive1.getArchiveDocuments().sort(Comparator.comparing(EasyArchiveDocument::getName));
        List<EasyArchiveDocument> sortedArchive2 = archive2.getArchiveDocuments().stream()
                .sorted(Comparator.comparing(EasyArchiveDocument::getName))
                .toList();
        for (int i = 0; i < archive1.getArchiveDocuments().size(); i++) {
            assertArchiveDocument(archive1.getArchiveDocuments().get(i), sortedArchive2.get(i));
        }
    }

    protected static void assertArchive(Archive archive1, genealogy.visualizer.entity.Archive archive2) {
        assertArchive(toEasyArchive(archive1), toEasyArchive(archive2));
        assertEquals(archive1.getArchiveDocuments().size(), archive2.getArchiveDocuments().size());
        archive1.getArchiveDocuments().sort(Comparator.comparing(EasyArchiveDocument::getName));
        List<genealogy.visualizer.entity.ArchiveDocument> sortedArchive2 = archive2.getArchiveDocuments().stream()
                .sorted(Comparator.comparing(genealogy.visualizer.entity.ArchiveDocument::getName))
                .toList();
        for (int i = 0; i < archive1.getArchiveDocuments().size(); i++) {
            assertArchiveDocument(archive1.getArchiveDocuments().get(i), sortedArchive2.get(i));
        }
    }

    private static EasyArchive toEasyArchive(genealogy.visualizer.entity.Archive archive) {
        if (archive == null) {
            return null;
        }
        return new EasyArchive()
                .id(archive.getId())
                .name(archive.getName())
                .abbreviation(archive.getAbbreviation())
                .comment(archive.getComment())
                .address(archive.getAddress());
    }

    private static EasyArchive toEasyArchive(Archive archive) {
        if (archive == null) {
            return null;
        }
        return new EasyArchive()
                .id(archive.getId())
                .name(archive.getName())
                .abbreviation(archive.getAbbreviation())
                .comment(archive.getComment())
                .address(archive.getAddress());
    }
}
