package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyPerson;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

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

class DeathControllerTest extends IntegrationTest {

    private static final String PATH = "/death";

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Death deathExist = existingDeaths.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingDeaths.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        Death response = objectMapper.readValue(getRequest(PATH + "/" + deathExist.getId()), Death.class);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertDeath(response, deathExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        deathFilter.isFindWithHavePerson(false);
        deathFilter.setArchiveDocumentId(existingArchiveDocuments.stream().max(Comparator.comparingInt(ad -> ad.getDeaths().size()))
                .orElse(existingArchiveDocuments.getFirst()).getId());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        List<EasyDeath> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(deathFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyDeath.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        List<Long> existIds = getIdsByFilter();
        Set<Long> findIds = response.stream().map(EasyDeath::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        deathFilter.isFindWithHavePerson(true);
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        initialQueryExecutionCount = statistics.getQueryExecutionCount();
        response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(deathFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyDeath.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        existIds = getIdsByFilter();
        findIds = response.stream().map(EasyDeath::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        deathFilter.getFullName().setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(deathFilter));
    }

    @Test
    void saveTest() throws Exception {
        Death deathSave = getDeath(existingDeaths.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingDeaths.getFirst()));
        deathSave.setId(null);
        Death response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(deathSave)), Death.class);
        assertDeath(response, deathSave);

        Death responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Death.class);
        assertDeath(responseGet, deathSave);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(deathSave));
    }

    @Test
    void updateTest() throws Exception {
        Death deathUpdate = getDeath(existingDeaths.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingDeaths.getFirst()));

        Death response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(deathUpdate)), Death.class);
        assertDeath(response, deathUpdate);

        Death responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Death.class);
        assertDeath(responseGet, deathUpdate);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(deathUpdate));

        deathUpdate.setArchiveDocument(null);
        deathUpdate.setPerson(null);
        deathUpdate.setLocality(null);

        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(deathUpdate)), Death.class);
        assertDeath(response, deathUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Death deathExist = existingDeaths.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null).findAny().orElse(existingDeaths.getFirst());
        String responseJson = deleteRequest(PATH + "/" + deathExist.getId());
        existingDeaths.remove(deathExist);

        assertTrue(responseJson.isEmpty());
        assertTrue(deathRepository.findById(deathExist.getId()).isEmpty());
        if (deathExist.getArchiveDocument() != null) {
            assertFalse(archiveDocumentRepository.findById(deathExist.getArchiveDocument().getId()).isEmpty());
        }
        if (deathExist.getLocality() != null) {
            assertFalse(localityRepository.findById(deathExist.getLocality().getId()).isEmpty());
        }
        if (deathExist.getPerson() != null) {
            assertFalse(personRepository.findById(deathExist.getPerson().getId()).isEmpty());
        }

        deleteUnauthorizedRequest(PATH + "/" + deathExist.getId());
    }

    private List<Long> getIdsByFilter() {
        List<genealogy.visualizer.entity.Death> filteredDeath = existingDeaths.stream()
                .filter(this::matchesFilter)
                .toList();
        if (!deathFilter.getIsFindWithHavePerson()) {
            filteredDeath = filteredDeath.stream()
                    .filter(death -> death.getPerson() == null)
                    .toList();
        }
        return filteredDeath.stream().map(genealogy.visualizer.entity.Death::getId).toList();
    }

    private boolean matchesFilter(genealogy.visualizer.entity.Death death) {
        return containsIgnoreCase(death.getFullName().getName(), deathFilter.getFullName().getName()) &&
                containsIgnoreCase(death.getFullName().getSurname(), deathFilter.getFullName().getSurname()) &&
                containsIgnoreCase(death.getFullName().getLastName(), deathFilter.getFullName().getLastName()) &&
                death.getArchiveDocument() != null && death.getArchiveDocument().getId().equals(deathFilter.getArchiveDocumentId()) &&
                death.getDate().getYear() == deathFilter.getDeathYear();
    }

    static void assertDeath(EasyDeath death1, genealogy.visualizer.entity.Death death2) {
        assertDeath(death1, toEasyDeath(death2));
    }

    static void assertDeath(Death death1, Death death2) {
        assertDeath(toEasyDeath(death1), toEasyDeath(death2));
        assertLocality(death1.getLocality(), death2.getLocality());
        assertPerson(death1.getPerson(), death2.getPerson());
        assertArchiveDocument(death1.getArchiveDocument(), death2.getArchiveDocument());
    }

    static void assertDeath(Death death1, genealogy.visualizer.entity.Death death2) {
        assertDeath(toEasyDeath(death1), toEasyDeath(death2));
        assertLocality(death1.getLocality(), death2.getLocality());
        assertPerson(death1.getPerson(), death2.getPerson());
        assertArchiveDocument(death1.getArchiveDocument(), death2.getArchiveDocument());
    }

    static void assertDeaths(List<EasyDeath> death1, List<genealogy.visualizer.entity.Death> death2) {
        assertNotNull(death2);
        assertDeath(death1, death2.stream().map(DeathControllerTest::toEasyDeath).toList());
    }

    static void assertDeath(List<EasyDeath> death1, List<EasyDeath> death2) {
        if (death1 == null || death2 == null) {
            assertNull(death1);
            assertNull(death2);
            return;
        }
        assertNotNull(death1);
        assertNotNull(death2);
        assertEquals(death1.size(), death2.size());
        List<EasyDeath> deathsSorted1 = death1.stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
        List<EasyDeath> deathsSorted2 = death2.stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
        for (int i = 0; i < deathsSorted1.size(); i++) {
            assertDeath(deathsSorted1.get(i), deathsSorted2.get(i));
        }
    }

    static void assertDeath(EasyDeath death1, EasyDeath death2) {
        if (death1 == null || death2 == null) {
            assertNull(death1);
            assertNull(death2);
            return;
        }
        assertNotNull(death1);
        assertNotNull(death2);
        if (death1.getId() != null && death2.getId() != null) {
            assertEquals(death1.getId(), death2.getId());
        }
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
        assertFullName(death1.getRelative(), death2.getRelative());
        assertAge(death1.getAge(), death2.getAge());
        assertEquals(death1.getCause(), death2.getCause());
        assertEquals(death1.getComment(), death2.getComment());
        assertEquals(death1.getBurialPlace(), death2.getBurialPlace());
    }

    private static Death getDeath(genealogy.visualizer.entity.Death deathExist) {
        Death death = generator.nextObject(Death.class);
        death.setId(deathExist.getId());
        death.setArchiveDocument(generator.nextObject(EasyArchiveDocument.class));
        death.setPerson(generator.nextObject(EasyPerson.class));
        death.setLocality(generator.nextObject(EasyLocality.class));
        return death;
    }

    private static EasyDeath toEasyDeath(genealogy.visualizer.entity.Death death) {
        if (death == null) {
            return null;
        }
        return new EasyDeath()
                .id(death.getId())
                .date(death.getDate())
                .fullName(toFullName(death.getFullName()))
                .relative(toFullName(death.getRelative()))
                .age(toAge(death.getAge()))
                .cause(death.getCause())
                .comment(death.getComment())
                .burialPlace(death.getBurialPlace());
    }

    private static EasyDeath toEasyDeath(Death death) {
        if (death == null) {
            return null;
        }
        return new EasyDeath()
                .id(death.getId())
                .date(death.getDate())
                .fullName(death.getFullName())
                .relative(death.getRelative())
                .age(death.getAge())
                .cause(death.getCause())
                .comment(death.getComment())
                .burialPlace(death.getBurialPlace());
    }
}
