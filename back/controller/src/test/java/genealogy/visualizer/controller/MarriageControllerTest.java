package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.Witness;
import genealogy.visualizer.mapper.WitnessMapper;
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
import static genealogy.visualizer.controller.PersonControllerTest.assertPersons;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarriageControllerTest extends IntegrationTest {

    @Autowired
    private WitnessMapper witnessMapper;

    private static final String PATH = "/marriage";

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = existingMarriages.stream()
                .filter(e -> e.getPersons() != null && !e.getPersons().isEmpty()).findAny().orElse(existingMarriages.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        Marriage response = objectMapper.readValue(getRequest(PATH + "/" + marriageExist.getId()), Marriage.class);
        assertEquals(5, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertMarriage(response, marriageExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        marriageFilter.isFindWithHavePerson(false);
        marriageFilter.setArchiveDocumentId(existingArchiveDocuments.stream().max(Comparator.comparingInt(ad -> ad.getMarriages().size()))
                .orElse(existingArchiveDocuments.getFirst()).getId());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        List<EasyMarriage> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(marriageFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyMarriage.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        List<Long> existIds = getIdsByFilter();
        Set<Long> findIds = response.stream().map(EasyMarriage::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        marriageFilter.isFindWithHavePerson(true);
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        initialQueryExecutionCount = statistics.getQueryExecutionCount();
        response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(marriageFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyMarriage.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        existIds = getIdsByFilter();
        findIds = response.stream().map(EasyMarriage::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        marriageFilter.getWifeFullName().setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(marriageFilter));
    }

    @Test
    void saveTest() throws Exception {
        Marriage marriageSave = getMarriage(existingMarriages.stream()
                .filter(e -> e.getPersons() != null && !e.getPersons().isEmpty()).findAny().orElse(existingMarriages.getFirst()));
        marriageSave.setId(null);
        Marriage response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(marriageSave)), Marriage.class);
        assertMarriage(response, marriageSave);

        Marriage responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Marriage.class);
        assertMarriage(responseGet, marriageSave);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(marriageSave));
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = existingMarriages.stream()
                .filter(e -> e.getPersons() != null && !e.getPersons().isEmpty()).findAny().orElse(existingMarriages.getFirst());
        Marriage marriageUpdate = getMarriage(marriageExist);

        Marriage response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(marriageUpdate)), Marriage.class);
        assertMarriage(response, marriageUpdate);

        Marriage responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Marriage.class);
        assertMarriage(responseGet, marriageUpdate);

        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(marriageUpdate));

        marriageUpdate.setArchiveDocument(null);
        marriageUpdate.setWifeLocality(null);
        marriageUpdate.setHusbandLocality(null);
        marriageUpdate.setPersons(Collections.emptyList());
        marriageUpdate.setWitnesses(Collections.emptyList());
        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(marriageUpdate)), Marriage.class);
        assertMarriage(response, marriageUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Marriage marriageExist = existingMarriages.stream()
                .filter(e -> e.getPersons() != null && !e.getPersons().isEmpty() && e.getArchiveDocument() != null)
                .findAny().orElse(existingMarriages.getFirst());
        assertTrue(deleteRequest(PATH + "/" + marriageExist.getId()).isEmpty());
        existingMarriages.remove(marriageExist);

        assertTrue(marriageRepository.findById(marriageExist.getId()).isEmpty());
        if (marriageExist.getArchiveDocument() != null) {
            assertFalse(archiveDocumentRepository.findById(marriageExist.getArchiveDocument().getId()).isEmpty());
        }
        if (marriageExist.getHusbandLocality() != null) {
            assertFalse(localityRepository.findById(marriageExist.getHusbandLocality().getId()).isEmpty());
        }
        if (marriageExist.getWifeLocality() != null) {
            assertFalse(localityRepository.findById(marriageExist.getWifeLocality().getId()).isEmpty());
        }
        if (marriageExist.getPersons() != null) {
            marriageExist.getPersons().forEach(person -> assertFalse(personRepository.findById(person.getId()).isEmpty()));
        }

        deleteUnauthorizedRequest(PATH + "/" + marriageExist.getId());
    }

    private List<Long> getIdsByFilter() {
        List<genealogy.visualizer.entity.Marriage> filteredMarriages = existingMarriages.stream()
                .filter(this::matchesFilter)
                .toList();
        if (!marriageFilter.getIsFindWithHavePerson()) {
            filteredMarriages = filteredMarriages.stream()
                    .filter(marriage -> marriage.getPersons() == null || marriage.getPersons().isEmpty())
                    .toList();
        }
        return filteredMarriages.stream().map(genealogy.visualizer.entity.Marriage::getId).toList();
    }

    private boolean matchesFilter(genealogy.visualizer.entity.Marriage marriage) {
        return containsIgnoreCase(marriage.getHusband().getName(), marriageFilter.getHusbandFullName().getName()) &&
                containsIgnoreCase(marriage.getHusband().getSurname(), marriageFilter.getHusbandFullName().getSurname()) &&
                containsIgnoreCase(marriage.getHusband().getLastName(), marriageFilter.getHusbandFullName().getLastName()) &&
                containsIgnoreCase(marriage.getWife().getName(), marriageFilter.getWifeFullName().getName()) &&
                containsIgnoreCase(marriage.getWife().getSurname(), marriageFilter.getWifeFullName().getSurname()) &&
                containsIgnoreCase(marriage.getWife().getLastName(), marriageFilter.getWifeFullName().getLastName()) &&
                marriage.getArchiveDocument() != null && marriage.getArchiveDocument().getId().equals(marriageFilter.getArchiveDocumentId()) &&
                marriage.getDate().getYear() == marriageFilter.getMarriageYear();
    }

    static void assertMarriage(Marriage marriage1, Marriage marriage2) {
        assertMarriage(toEasyMarriage(marriage1), toEasyMarriage(marriage2));
        assertArchiveDocument(marriage1.getArchiveDocument(), marriage2.getArchiveDocument());
        if (marriage2.getWitnesses() != null) {
            assertEquals(marriage1.getWitnesses().size(), marriage2.getWitnesses().size());
            List<Witness> witnesses1 = marriage1.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<Witness> witnesses2 = marriage2.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < witnesses1.size(); i++) {
                assertWitness(witnesses1.get(i), witnesses2.get(i));
            }
        }
        assertPerson(marriage1.getPersons(), marriage2.getPersons());
    }

    static void assertMarriage(Marriage marriage1, genealogy.visualizer.entity.Marriage marriage2) {
        assertMarriage(toEasyMarriage(marriage1), toEasyMarriage(marriage2));
        assertArchiveDocument(marriage1.getArchiveDocument(), marriage2.getArchiveDocument());
        if (marriage2.getWitnesses() != null) {
            assertEquals(marriage1.getWitnesses().size(), marriage2.getWitnesses().size());
            List<Witness> witnesses1 = marriage1.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.Witness> witnesses2 = marriage2.getWitnesses().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < witnesses1.size(); i++) {
                assertWitness(witnesses1.get(i), witnesses2.get(i));
            }
        }
        assertPersons(marriage1.getPersons(), marriage2.getPersons());
    }

    static void assertMarriages(List<EasyMarriage> marriage1, List<genealogy.visualizer.entity.Marriage> marriage2) {
        assertNotNull(marriage2);
        assertMarriage(marriage1, marriage2.stream().map(MarriageControllerTest::toEasyMarriage).toList());
    }

    static void assertMarriage(List<EasyMarriage> marriage1, List<EasyMarriage> marriage2) {
        if (marriage1 == null || marriage2 == null) {
            assertNull(marriage1);
            assertNull(marriage2);
            return;
        }
        assertNotNull(marriage1);
        assertNotNull(marriage2);
        assertEquals(marriage1.size(), marriage2.size());
        List<EasyMarriage> marriagesSorted1 = marriage1.stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
        List<EasyMarriage> marriagesSorted2 = marriage2.stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
        for (int i = 0; i < marriagesSorted1.size(); i++) {
            assertMarriage(marriagesSorted1.get(i), marriagesSorted2.get(i));
        }
    }

    static void assertMarriage(EasyMarriage marriage1, EasyMarriage marriage2) {
        if (marriage1 == null || marriage2 == null) {
            assertNull(marriage1);
            assertNull(marriage2);
            return;
        }
        assertNotNull(marriage1);
        assertNotNull(marriage2);
        if (marriage1.getId() != null && marriage2.getId() != null) {
            assertEquals(marriage1.getId(), marriage2.getId());
        }
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
        assertMarriage(marriage1, toEasyMarriage(marriage2));
    }

    static void assertWitness(Witness witness1, Witness witness2) {
        assertNotNull(witness1);
        assertNotNull(witness2);
        assertFullName(witness1.getFullName(), witness2.getFullName());
        assertEquals(witness1.getWitnessType(), witness2.getWitnessType());
        assertLocality(witness1.getLocality(), witness2.getLocality());
    }

    static void assertWitness(Witness witness1, genealogy.visualizer.entity.Witness witness2) {
        assertNotNull(witness1);
        assertNotNull(witness2);
        assertFullName(witness1.getFullName(), witness2.getFullName());
        assertEquals(witness1.getWitnessType().getValue(), witness2.getWitnessType().getName());
        assertLocality(witness1.getLocality(), witness2.getLocality());
    }

    private static EasyMarriage toEasyMarriage(genealogy.visualizer.entity.Marriage marriage) {
        if (marriage == null) {
            return null;
        }
        return new EasyMarriage()
                .id(marriage.getId())
                .date(marriage.getDate())
                .husbandMarriageNumber(marriage.getHusbandMarriageNumber().intValue())
                .wifeMarriageNumber(marriage.getWifeMarriageNumber().intValue())
                .wife(toFullName(marriage.getWife()))
                .wifesFather(toFullName(marriage.getWifesFather()))
                .wifeAge(toAge(marriage.getWifeAge()))
                .husband(toFullName(marriage.getHusband()))
                .husbandsFather(toFullName(marriage.getHusbandsFather()))
                .husbandAge(toAge(marriage.getHusbandAge()))
                .comment(marriage.getComment());
    }

    private static EasyMarriage toEasyMarriage(Marriage marriage) {
        if (marriage == null) {
            return null;
        }
        return new EasyMarriage()
                .id(marriage.getId())
                .date(marriage.getDate())
                .husbandMarriageNumber(marriage.getHusbandMarriageNumber())
                .wifeMarriageNumber(marriage.getWifeMarriageNumber())
                .wife(marriage.getWife())
                .wifesFather(marriage.getWifesFather())
                .wifeAge(marriage.getWifeAge())
                .husband(marriage.getHusband())
                .husbandsFather(marriage.getHusbandsFather())
                .husbandAge(marriage.getHusbandAge())
                .comment(marriage.getComment());
    }

    private Marriage getMarriage(genealogy.visualizer.entity.Marriage marriageExist) {
        Marriage marriage = generator.nextObject(Marriage.class);
        marriage.setId(marriageExist.getId());
        marriage.setArchiveDocument(generator.nextObject(EasyArchiveDocument.class));
        List<EasyPerson> persons = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList());
        marriageExist.getPersons().forEach(p -> {
            if (generator.nextBoolean()) {
                persons.add(easyPersonMapper.toDTO(p));
            }
        });
        marriage.setPersons(persons);
        marriage.getWife().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        marriage.getWifesFather().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        marriage.getHusband().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        marriage.getHusbandsFather().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        EasyLocality easyLocality = generator.nextObject(EasyLocality.class);
        easyLocality.setAnotherNames(generator.objects(String.class, 4).toList());
        marriage.setHusbandLocality(easyLocality);
        marriage.setWifeLocality(easyLocalityMapper.toDTO(existingLocalities.get(generator.nextInt(existingLocalities.size()))));
        List<Witness> witnessesUpdate = new ArrayList<>(generator.objects(Witness.class, generator.nextInt(5, 10)).toList());
        witnessesUpdate.forEach(w -> {
            w.getFullName().setStatuses(generator.objects(String.class, generator.nextInt(1, 3)).toList());
            w.setLocality(generator.nextBoolean() ? easyLocalityMapper.toDTO(existingLocalities.get(generator.nextInt(existingLocalities.size()))) : easyLocality);
        });
        marriageExist.getWitnesses().forEach(w -> {
            if (generator.nextBoolean()) {
                witnessesUpdate.add(witnessMapper.toDTO(w));
            }
        });
        marriage.setWitnesses(witnessesUpdate);
        return marriage;
    }
}
