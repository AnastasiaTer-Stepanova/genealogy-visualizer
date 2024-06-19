package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentType;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.EasyMarriage;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristenings;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeaths;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevision;
import static genealogy.visualizer.controller.FamilyRevisionControllerTest.assertFamilyRevisions;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriage;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiveDocumentControllerTest extends IntegrationTest {

    private static final String PATH = "/archive-document";

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocument = existingArchiveDocuments.stream()
                .filter(e -> e.getMarriages() != null && !e.getMarriages().isEmpty() &&
                        e.getFamilyRevisions() != null && !e.getFamilyRevisions().isEmpty() &&
                        e.getPreviousRevisions() != null && !e.getPreviousRevisions().isEmpty() &&
                        e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getArchive() != null && e.getNextRevision() != null)
                .findAny().orElse(existingArchiveDocuments.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        ArchiveDocument response = objectMapper.readValue(getRequest(PATH + "/" + archiveDocument.getId()), ArchiveDocument.class);
        assertEquals(5, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertArchiveDocument(response, archiveDocument);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        archiveDocumentFilter.setArchiveId(existingArchives.stream().max(Comparator.comparingInt(a -> a.getArchiveDocuments().size()))
                .orElse(existingArchives.getFirst()).getId());
        List<EasyArchiveDocument> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(archiveDocumentFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyArchiveDocument.class));
        assertNotNull(response);
        Set<Long> findIds = response.stream().map(EasyArchiveDocument::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.ArchiveDocument archiveDocument : existingArchiveDocuments) {
            if (containsIgnoreCase(archiveDocument.getAbbreviation(), archiveDocumentFilter.getAbbreviation()) &&
                    containsIgnoreCase(archiveDocument.getName(), archiveDocumentFilter.getName()) &&
                    archiveDocument.getArchive() != null && archiveDocument.getArchive().getId().equals(archiveDocumentFilter.getArchiveId()) &&
                    archiveDocument.getYear() == archiveDocumentFilter.getYear().shortValue() &&
                    archiveDocument.getType().name().equals(archiveDocumentFilter.getType().name())) {
                assertTrue(findIds.contains(archiveDocument.getId()));
            }
        }

        archiveDocumentFilter.setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(archiveDocumentFilter));
    }

    @Test
    void saveTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = existingArchiveDocuments.stream()
                .filter(e -> e.getMarriages() != null && !e.getMarriages().isEmpty() &&
                        e.getFamilyRevisions() != null && !e.getFamilyRevisions().isEmpty() &&
                        e.getPreviousRevisions() != null && !e.getPreviousRevisions().isEmpty() &&
                        e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getArchive() != null && e.getNextRevision() != null)
                .findAny().orElse(existingArchiveDocuments.getFirst());
        ArchiveDocument archiveDocumentSave = getArchiveDocument(archiveDocumentExist);
        archiveDocumentSave.setId(null);

        ArchiveDocument response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(archiveDocumentSave)), ArchiveDocument.class);
        assertArchiveDocument(response, archiveDocumentSave);

        ArchiveDocument responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), ArchiveDocument.class);
        assertArchiveDocument(responseGet, archiveDocumentSave);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(archiveDocumentSave));
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist = existingArchiveDocuments.stream()
                .filter(e -> e.getMarriages() != null && !e.getMarriages().isEmpty() &&
                        e.getFamilyRevisions() != null && !e.getFamilyRevisions().isEmpty() &&
                        e.getPreviousRevisions() != null && !e.getPreviousRevisions().isEmpty() &&
                        e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getArchive() != null && e.getNextRevision() != null)
                .findAny().orElse(existingArchiveDocuments.getFirst());
        ArchiveDocument archiveDocumentUpdate = getArchiveDocument(archiveDocumentExist);

        ArchiveDocument response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(archiveDocumentUpdate)), ArchiveDocument.class);
        assertArchiveDocument(response, archiveDocumentUpdate);

        ArchiveDocument responseGet = objectMapper.readValue(getRequest(PATH + "/" + archiveDocumentUpdate.getId()), ArchiveDocument.class);
        assertArchiveDocument(responseGet, archiveDocumentUpdate);

        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(archiveDocumentUpdate));

        archiveDocumentUpdate.setNextRevision(null);
        archiveDocumentUpdate.setArchive(null);
        archiveDocumentUpdate.setMarriages(Collections.emptyList());
        archiveDocumentUpdate.setFamilyRevisions(Collections.emptyList());
        archiveDocumentUpdate.setPreviousRevisions(Collections.emptyList());
        archiveDocumentUpdate.setChristenings(Collections.emptyList());
        archiveDocumentUpdate.setDeaths(Collections.emptyList());
        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(archiveDocumentUpdate)), ArchiveDocument.class);
        assertArchiveDocument(response, archiveDocumentUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.ArchiveDocument archiveDocument = existingArchiveDocuments.stream()
                .filter(e -> e.getMarriages() != null && !e.getMarriages().isEmpty() &&
                        e.getFamilyRevisions() != null && !e.getFamilyRevisions().isEmpty() &&
                        e.getPreviousRevisions() != null && !e.getPreviousRevisions().isEmpty() &&
                        e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getArchive() != null && e.getNextRevision() != null)
                .findAny().orElse(existingArchiveDocuments.getFirst());
        String responseJson = deleteRequest(PATH + "/" + archiveDocument.getId());
        existingArchiveDocuments.remove(archiveDocument);

        assertTrue(responseJson.isEmpty());
        assertTrue(archiveDocumentRepository.findById(archiveDocument.getId()).isEmpty());

        if (archiveDocument.getDeaths() != null) {
            archiveDocument.getDeaths().forEach(p -> assertFalse(deathRepository.findById(p.getId()).isEmpty()));
        }
        if (archiveDocument.getChristenings() != null) {
            archiveDocument.getChristenings().forEach(p -> assertFalse(christeningRepository.findById(p.getId()).isEmpty()));
        }
        if (archiveDocument.getFamilyRevisions() != null) {
            archiveDocument.getFamilyRevisions().forEach(p -> assertFalse(familyRevisionRepository.findById(p.getId()).isEmpty()));
        }
        if (archiveDocument.getPreviousRevisions() != null) {
            archiveDocument.getPreviousRevisions().forEach(r -> assertFalse(archiveDocumentRepository.findById(r.getId()).isEmpty()));
        }
        if (archiveDocument.getMarriages() != null) {
            archiveDocument.getMarriages().forEach(m -> assertFalse(marriageRepository.findById(m.getId()).isEmpty()));
        }
        if (archiveDocument.getNextRevision() != null) {
            assertFalse(archiveDocumentRepository.findById(archiveDocument.getNextRevision().getId()).isEmpty());
        }
        if (archiveDocument.getArchive() != null) {
            assertFalse(archiveRepository.findById(archiveDocument.getArchive().getId()).isEmpty());
        }

        deleteUnauthorizedRequest(PATH + "/" + archiveDocument.getId());
    }

    private ArchiveDocument getArchiveDocument(genealogy.visualizer.entity.ArchiveDocument archiveDocumentExist) {
        ArchiveDocument archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setId(archiveDocumentExist.getId());
        archiveDocument.setArchive(generator.nextObject(EasyArchive.class));
        archiveDocument.setNextRevision(generator.nextObject(EasyArchiveDocument.class));
        List<EasyArchiveDocument> previousRevisions = new ArrayList<>(generator.objects(EasyArchiveDocument.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getPreviousRevisions().forEach(pr -> {
            if (generator.nextBoolean()) {
                previousRevisions.add(easyArchiveDocumentMapper.toDTO(pr));
            }
        });
        archiveDocument.setPreviousRevisions(previousRevisions);
        List<EasyFamilyMember> familyRevisions = new ArrayList<>(generator.objects(EasyFamilyMember.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getFamilyRevisions().forEach(fr -> {
            if (generator.nextBoolean()) {
                familyRevisions.add(easyFamilyRevisionMapper.toDTO(fr));
            }
        });
        archiveDocument.setFamilyRevisions(familyRevisions);
        List<EasyChristening> christenings = new ArrayList<>(generator.objects(EasyChristening.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getChristenings().forEach(c -> {
            if (generator.nextBoolean()) {
                christenings.add(easyChristeningMapper.toDTO(c));
            }
        });
        archiveDocument.setChristenings(christenings);
        List<EasyDeath> deaths = new ArrayList<>(generator.objects(EasyDeath.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getDeaths().forEach(d -> {
            if (generator.nextBoolean()) {
                deaths.add(easyDeathMapper.toDTO(d));
            }
        });
        archiveDocument.setDeaths(deaths);
        List<EasyMarriage> marriages = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(2, 5)).toList());
        archiveDocumentExist.getMarriages().forEach(m -> {
            if (generator.nextBoolean()) {
                marriages.add(easyMarriageMapper.toDTO(m));
            }
        });
        archiveDocument.setMarriages(marriages);
        return archiveDocument;
    }

    protected static void assertArchiveDocument(List<EasyArchiveDocument> archiveDocument1, List<EasyArchiveDocument> archiveDocument2) {
        if (archiveDocument1 == null || archiveDocument2 == null) {
            assertNull(archiveDocument1);
            assertNull(archiveDocument2);
            return;
        }
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        assertEquals(archiveDocument1.size(), archiveDocument2.size());
        List<EasyArchiveDocument> previousRevisionSorted1 = archiveDocument1.stream().sorted(Comparator.comparing(EasyArchiveDocument::getName)).toList();
        List<EasyArchiveDocument> previousRevisionSorted2 = archiveDocument2.stream().sorted(Comparator.comparing(EasyArchiveDocument::getName)).toList();
        for (int i = 0; i < previousRevisionSorted1.size(); i++) {
            assertArchiveDocument(previousRevisionSorted1.get(i), previousRevisionSorted2.get(i));
        }
    }

    protected static void assertArchiveDocuments(List<EasyArchiveDocument> archiveDocument1, List<genealogy.visualizer.entity.ArchiveDocument> archiveDocument2) {
        assertNotNull(archiveDocument2);
        assertArchiveDocument(archiveDocument1, archiveDocument2.stream().map(ArchiveDocumentControllerTest::toEasyArchiveDocument).toList());
    }

    protected static void assertArchiveDocument(ArchiveDocument archiveDocument1, ArchiveDocument archiveDocument2) {
        assertArchiveDocument(toEasyArchiveDocument(archiveDocument1), toEasyArchiveDocument(archiveDocument2));
        assertArchiveDocument(archiveDocument1.getNextRevision(), archiveDocument2.getNextRevision());
        assertArchiveDocument(archiveDocument1.getPreviousRevisions(), archiveDocument2.getPreviousRevisions());
        assertFamilyRevision(archiveDocument1.getFamilyRevisions(), archiveDocument2.getFamilyRevisions());
        assertChristening(archiveDocument1.getChristenings(), archiveDocument2.getChristenings());
        assertMarriage(archiveDocument1.getMarriages(), archiveDocument2.getMarriages());
        assertDeath(archiveDocument1.getDeaths(), archiveDocument2.getDeaths());
    }

    protected static void assertArchiveDocument(EasyArchiveDocument archiveDocument1, EasyArchiveDocument archiveDocument2) {
        if (archiveDocument1 == null || archiveDocument2 == null) {
            assertNull(archiveDocument1);
            assertNull(archiveDocument2);
            return;
        }
        assertNotNull(archiveDocument1);
        assertNotNull(archiveDocument2);
        if (archiveDocument1.getId() != null && archiveDocument2.getId() != null) {
            assertEquals(archiveDocument1.getId(), archiveDocument2.getId());
        }
        assertEquals(archiveDocument1.getFund(), archiveDocument2.getFund());
        assertEquals(archiveDocument1.getCatalog(), archiveDocument2.getCatalog());
        assertEquals(archiveDocument1.getInstance(), archiveDocument2.getInstance());
        assertEquals(archiveDocument1.getBunch(), archiveDocument2.getBunch());
        assertEquals(archiveDocument1.getType(), archiveDocument2.getType());
        assertEquals(archiveDocument1.getYear(), archiveDocument2.getYear());
    }

    protected static void assertArchiveDocument(EasyArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
        assertArchiveDocument(archiveDocument1, toEasyArchiveDocument(archiveDocument2));
    }

    protected void assertArchiveDocument(ArchiveDocument archiveDocument1, genealogy.visualizer.entity.ArchiveDocument archiveDocument2) {
        assertArchiveDocument(toEasyArchiveDocument(archiveDocument1), toEasyArchiveDocument(archiveDocument2));
        assertArchiveDocument(archiveDocument1.getNextRevision(), archiveDocument2.getNextRevision());
        assertArchiveDocument(archiveDocument1.getNextRevision(), archiveDocument2.getNextRevision());
        assertArchiveDocuments(archiveDocument1.getPreviousRevisions(), archiveDocument2.getPreviousRevisions());
        assertFamilyRevisions(archiveDocument1.getFamilyRevisions(), archiveDocument2.getFamilyRevisions());
        assertChristenings(archiveDocument1.getChristenings(), archiveDocument2.getChristenings());
        assertMarriages(archiveDocument1.getMarriages(), archiveDocument2.getMarriages());
        assertDeaths(archiveDocument1.getDeaths(), archiveDocument2.getDeaths());
    }

    private static EasyArchiveDocument toEasyArchiveDocument(genealogy.visualizer.entity.ArchiveDocument archiveDocument) {
        if (archiveDocument == null) {
            return null;
        }
        return new EasyArchiveDocument()
                .id(archiveDocument.getId())
                .name(archiveDocument.getName())
                .abbreviation(archiveDocument.getAbbreviation())
                .fund(archiveDocument.getFund())
                .catalog(archiveDocument.getCatalog())
                .instance(archiveDocument.getInstance())
                .bunch(archiveDocument.getBunch())
                .type(ArchiveDocumentType.valueOf(archiveDocument.getType().name()))
                .year(archiveDocument.getYear().intValue());
    }

    private static EasyArchiveDocument toEasyArchiveDocument(ArchiveDocument archiveDocument) {
        if (archiveDocument == null) {
            return null;
        }
        return new EasyArchiveDocument()
                .id(archiveDocument.getId())
                .name(archiveDocument.getName())
                .abbreviation(archiveDocument.getAbbreviation())
                .fund(archiveDocument.getFund())
                .catalog(archiveDocument.getCatalog())
                .instance(archiveDocument.getInstance())
                .bunch(archiveDocument.getBunch())
                .type(archiveDocument.getType())
                .year(archiveDocument.getYear());
    }
}