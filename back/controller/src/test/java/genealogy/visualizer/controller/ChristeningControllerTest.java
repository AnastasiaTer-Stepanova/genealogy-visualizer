package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.GodParent;
import genealogy.visualizer.api.model.Sex;
import genealogy.visualizer.mapper.GodParentMapper;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    private GodParentMapper godParentMapper;

    private static final String PATH = "/christening";

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = existingChristenings.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingChristenings.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        Christening response = objectMapper.readValue(getRequest(PATH + "/" + christeningExist.getId()), Christening.class);
        assertEquals(2, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertChristening(response, christeningExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        christeningFilter.isFindWithHavePerson(false);
        christeningFilter.setArchiveDocumentId(existingArchiveDocuments.stream().max(Comparator.comparingInt(ad -> ad.getChristenings().size()))
                .orElse(existingArchiveDocuments.getFirst()).getId());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        List<EasyChristening> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(christeningFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyChristening.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        List<Long> existIds = getIdsByFilter();
        Set<Long> findIds = response.stream().map(EasyChristening::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        christeningFilter.isFindWithHavePerson(true);
        christeningFilter.setArchiveDocumentId(existingArchiveDocuments.stream().max(Comparator.comparingInt(ad -> ad.getChristenings().size()))
                .orElse(existingArchiveDocuments.getFirst()).getId());
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        initialQueryExecutionCount = statistics.getQueryExecutionCount();
        response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(christeningFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyChristening.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        existIds = getIdsByFilter();
        findIds = response.stream().map(EasyChristening::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        christeningFilter.setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(christeningFilter));
    }

    @Test
    void saveTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = existingChristenings.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null
                        && e.getGodParents() != null && !e.getGodParents().isEmpty()).findAny().orElse(existingChristenings.getFirst());
        Christening christeningSave = getChristening(christeningExist);
        christeningSave.setId(null);

        Christening response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(christeningSave)), Christening.class);
        assertChristening(response, christeningSave);

        Christening responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Christening.class);
        assertChristening(responseGet, christeningSave);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(christeningSave));
    }

    @Test
    void updateTest() throws Exception {
        Christening christeningUpdate = getChristening(existingChristenings.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingChristenings.getFirst()));

        Christening response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(christeningUpdate)), Christening.class);
        assertChristening(response, christeningUpdate);

        Christening responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Christening.class);
        assertChristening(responseGet, christeningUpdate);

        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(christeningUpdate));

        christeningUpdate.setArchiveDocument(null);
        christeningUpdate.setPerson(null);
        christeningUpdate.setLocality(null);
        christeningUpdate.setGodParents(Collections.emptyList());

        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(christeningUpdate)), Christening.class);
        assertChristening(response, christeningUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Christening christeningExist = existingChristenings.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingChristenings.getFirst());
        String responseJson = deleteRequest(PATH + "/" + christeningExist.getId());
        existingChristenings.remove(christeningExist);

        assertTrue(responseJson.isEmpty());
        assertTrue(christeningRepository.findById(christeningExist.getId()).isEmpty());
        if (christeningExist.getArchiveDocument() != null) {
            assertFalse(archiveDocumentRepository.findById(christeningExist.getArchiveDocument().getId()).isEmpty());
        }
        if (christeningExist.getLocality() != null) {
            assertFalse(localityRepository.findById(christeningExist.getLocality().getId()).isEmpty());
        }
        if (christeningExist.getPerson() != null) {
            assertFalse(personRepository.findById(christeningExist.getPerson().getId()).isEmpty());
        }

        deleteUnauthorizedRequest(PATH + "/" + christeningExist.getId());
    }

    private List<Long> getIdsByFilter() {
        List<genealogy.visualizer.entity.Christening> filteredChristenings = existingChristenings.stream()
                .filter(this::matchesFilter)
                .toList();
        if (!christeningFilter.getIsFindWithHavePerson()) {
            filteredChristenings = filteredChristenings.stream()
                    .filter(christening -> christening.getPerson() == null)
                    .toList();
        }
        return filteredChristenings.stream().map(genealogy.visualizer.entity.Christening::getId).toList();
    }

    private boolean matchesFilter(genealogy.visualizer.entity.Christening christening) {
        return containsIgnoreCase(christening.getName(), christeningFilter.getName()) &&
                christening.getArchiveDocument() != null && christening.getArchiveDocument().getId().equals(christeningFilter.getArchiveDocumentId()) &&
                christening.getSex().name().equals(christeningFilter.getSex().name()) &&
                christening.getChristeningDate().getYear() == christeningFilter.getChristeningYear();
    }

    static void assertChristenings(List<EasyChristening> christening1, List<genealogy.visualizer.entity.Christening> christening2) {
        assertNotNull(christening2);
        assertChristening(christening1, christening2.stream().map(ChristeningControllerTest::toEasyChristening).toList());
    }

    static void assertChristening(List<EasyChristening> christening1, List<EasyChristening> christening2) {
        if (christening1 == null || christening2 == null) {
            assertNull(christening1);
            assertNull(christening2);
            return;
        }
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.size(), christening2.size());
        List<EasyChristening> christeningsSorted1 = christening1.stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
        List<EasyChristening> christeningsSorted2 = christening2.stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
        for (int i = 0; i < christeningsSorted1.size(); i++) {
            assertChristening(christeningsSorted1.get(i), christeningsSorted2.get(i));
        }
    }

    static void assertChristening(EasyChristening christening1, EasyChristening christening2) {
        if (christening1 == null || christening2 == null) {
            assertNull(christening1);
            assertNull(christening2);
            return;
        }
        assertNotNull(christening1);
        assertNotNull(christening2);
        if (christening1.getId() != null && christening2.getId() != null) {
            assertEquals(christening1.getId(), christening2.getId());
        }
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
        assertChristening(christening1, toEasyChristening(christening2));
    }

    static void assertChristening(Christening christening1, Christening christening2) {
        assertChristening(toEasyChristening(christening1), toEasyChristening(christening2));
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
        assertChristening(toEasyChristening(christening1), toEasyChristening(christening2));
        assertLocality(christening1.getLocality(), christening2.getLocality());
        if (christening2.getGodParents() != null) {
            assertEquals(christening1.getGodParents().size(), christening2.getGodParents().size());
            List<GodParent> godParents1 = christening1.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.GodParent> godParents2 = christening2.getGodParents().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
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

    private static void assertGodParent(GodParent godParent1, genealogy.visualizer.entity.GodParent godParent2) {
        assertNotNull(godParent1);
        assertNotNull(godParent2);
        assertFullName(godParent1.getFullName(), godParent2.getFullName());
        assertFullName(godParent1.getRelative(), godParent2.getRelative());
        assertLocality(godParent1.getLocality(), godParent2.getLocality());
    }

    private Christening getChristening(genealogy.visualizer.entity.Christening christeningExist) {
        Christening christening = generator.nextObject(Christening.class);
        christening.setId(christeningExist.getId());
        christening.setArchiveDocument(generator.nextObject(EasyArchiveDocument.class));
        christening.setPerson(generator.nextObject(EasyPerson.class));
        EasyLocality easyLocality = generator.nextObject(EasyLocality.class);
        easyLocality.setAnotherNames(generator.objects(String.class, 4).toList());
        christening.setLocality(easyLocality);
        List<GodParent> godParentsUpdate = new ArrayList<>(generator.objects(GodParent.class, generator.nextInt(2, 3)).toList());
        godParentsUpdate.forEach(gp -> gp.setLocality(generator.nextBoolean() ?
                easyLocalityMapper.toDTO(existingLocalities.get(generator.nextInt(existingLocalities.size()))) :
                generator.nextObject(EasyLocality.class)));
        christeningExist.getGodParents().forEach(gp -> {
            if (generator.nextBoolean()) {
                godParentsUpdate.add(godParentMapper.toDTO(gp));
            }
        });
        christening.setGodParents(godParentsUpdate);
        return christening;
    }

    private static EasyChristening toEasyChristening(genealogy.visualizer.entity.Christening christening) {
        if (christening == null) {
            return null;
        }
        return new EasyChristening()
                .id(christening.getId())
                .birthDate(christening.getBirthDate())
                .christeningDate(christening.getChristeningDate())
                .legitimacy(christening.getLegitimacy())
                .name(christening.getName())
                .sex(Sex.valueOf(christening.getSex().name()))
                .father(toFullName(christening.getFather()))
                .mother(toFullName(christening.getMother()))
                .comment(christening.getComment());
    }

    private static EasyChristening toEasyChristening(Christening christening) {
        if (christening == null) {
            return null;
        }
        return new EasyChristening()
                .id(christening.getId())
                .birthDate(christening.getBirthDate())
                .christeningDate(christening.getChristeningDate())
                .legitimacy(christening.getLegitimacy())
                .name(christening.getName())
                .sex(christening.getSex())
                .father(christening.getFather())
                .mother(christening.getMother())
                .comment(christening.getComment());
    }
}
