package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityType;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristenings;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeaths;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriage;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriages;
import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static genealogy.visualizer.controller.PersonControllerTest.assertPersons;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalityControllerTest extends IntegrationTest {

    private static final String PATH = "/locality";

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Locality localityExist = existingLocalities.stream()
                .filter(e -> e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getPersonsWithBirthLocality() != null && !e.getPersonsWithBirthLocality().isEmpty() &&
                        e.getPersonsWithDeathLocality() != null && !e.getPersonsWithDeathLocality().isEmpty() &&
                        e.getMarriagesWithWifeLocality() != null && !e.getMarriagesWithWifeLocality().isEmpty() &&
                        e.getMarriagesWithHusbandLocality() != null && !e.getMarriagesWithHusbandLocality().isEmpty())
                .findAny().orElse(existingLocalities.getFirst());

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        Locality response = objectMapper.readValue(getRequest(PATH + "/" + localityExist.getId()), Locality.class);
        assertEquals(9, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertLocality(response, localityExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        List<EasyLocality> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(localityFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyLocality.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        Set<Long> findIds = response.stream().map(EasyLocality::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Locality locality : existingLocalities) {
            if (containsIgnoreCase(locality.getAddress(), localityFilter.getAddress()) &&
                    containsIgnoreCase(locality.getName(), localityFilter.getName()) &&
                    locality.getType().name().equals(localityFilter.getType().name())) {
                assertTrue(findIds.contains(locality.getId()));
            }
        }

        localityFilter.setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(localityFilter));
    }

    @Test
    void saveTest() throws Exception {
        genealogy.visualizer.entity.Locality localityExist = existingLocalities.stream()
                .filter(e -> e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getPersonsWithBirthLocality() != null && !e.getPersonsWithBirthLocality().isEmpty() &&
                        e.getPersonsWithDeathLocality() != null && !e.getPersonsWithDeathLocality().isEmpty() &&
                        e.getMarriagesWithWifeLocality() != null && !e.getMarriagesWithWifeLocality().isEmpty() &&
                        e.getMarriagesWithHusbandLocality() != null && !e.getMarriagesWithHusbandLocality().isEmpty())
                .findAny().orElse(existingLocalities.getFirst());
        Locality localitySave = getLocality(localityExist);
        localitySave.setId(null);

        Locality response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(localitySave)), Locality.class);
        assertLocality(response, localitySave);

        Locality responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Locality.class);
        assertLocality(responseGet, responseGet);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(localitySave));
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Locality localityExist = existingLocalities.stream()
                .filter(e -> e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getPersonsWithBirthLocality() != null && !e.getPersonsWithBirthLocality().isEmpty() &&
                        e.getPersonsWithDeathLocality() != null && !e.getPersonsWithDeathLocality().isEmpty() &&
                        e.getMarriagesWithWifeLocality() != null && !e.getMarriagesWithWifeLocality().isEmpty() &&
                        e.getMarriagesWithHusbandLocality() != null && !e.getMarriagesWithHusbandLocality().isEmpty())
                .findAny().orElse(existingLocalities.getFirst());
        Locality localityUpdate = getLocality(localityExist);

        Locality response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(localityUpdate)), Locality.class);
        assertLocality(response, localityUpdate);

        Locality responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), Locality.class);
        assertLocality(responseGet, localityUpdate);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(localityUpdate));

        localityUpdate.setDeaths(Collections.emptyList());
        localityUpdate.setChristenings(Collections.emptyList());
        localityUpdate.setPersonsWithBirthLocality(Collections.emptyList());
        localityUpdate.setPersonsWithDeathLocality(Collections.emptyList());
        localityUpdate.setMarriagesWithWifeLocality(Collections.emptyList());
        localityUpdate.setMarriagesWithHusbandLocality(Collections.emptyList());
        localityUpdate.setAnotherNames(Collections.emptyList());

        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(localityUpdate)), Locality.class);
        assertLocality(response, localityUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Locality localityExist = existingLocalities.stream()
                .filter(e -> e.getChristenings() != null && !e.getChristenings().isEmpty() &&
                        e.getDeaths() != null && !e.getDeaths().isEmpty() &&
                        e.getPersonsWithBirthLocality() != null && !e.getPersonsWithBirthLocality().isEmpty() &&
                        e.getPersonsWithDeathLocality() != null && !e.getPersonsWithDeathLocality().isEmpty() &&
                        e.getMarriagesWithWifeLocality() != null && !e.getMarriagesWithWifeLocality().isEmpty() &&
                        e.getMarriagesWithHusbandLocality() != null && !e.getMarriagesWithHusbandLocality().isEmpty())
                .findAny().orElse(existingLocalities.getFirst());
        String responseJson = deleteRequest(PATH + "/" + localityExist.getId());
        existingLocalities.remove(localityExist);

        assertTrue(responseJson.isEmpty());
        assertTrue(localityRepository.findById(localityExist.getId()).isEmpty());
        if (localityExist.getDeaths() != null) {
            localityExist.getDeaths().forEach(p -> assertFalse(deathRepository.findById(p.getId()).isEmpty()));
        }
        if (localityExist.getChristenings() != null) {
            localityExist.getChristenings().forEach(p -> assertFalse(christeningRepository.findById(p.getId()).isEmpty()));
        }
        if (localityExist.getMarriagesWithHusbandLocality() != null) {
            localityExist.getMarriagesWithHusbandLocality().forEach(p -> assertFalse(marriageRepository.findById(p.getId()).isEmpty()));
        }
        if (localityExist.getMarriagesWithWifeLocality() != null) {
            localityExist.getMarriagesWithWifeLocality().forEach(p -> assertFalse(marriageRepository.findById(p.getId()).isEmpty()));
        }
        if (localityExist.getPersonsWithDeathLocality() != null) {
            localityExist.getPersonsWithDeathLocality().forEach(r -> assertFalse(personRepository.findById(r.getId()).isEmpty()));
        }
        if (localityExist.getPersonsWithBirthLocality() != null) {
            localityExist.getPersonsWithBirthLocality().forEach(m -> assertFalse(personRepository.findById(m.getId()).isEmpty()));
        }
        if (localityExist.getWitnesses() != null) {
            localityExist.getWitnesses().forEach(m -> assertFalse(witnessRepository.findById(m.getId()).isEmpty()));
        }
        if (localityExist.getGodParents() != null) {
            localityExist.getGodParents().forEach(m -> assertFalse(godParentRepository.findById(m.getId()).isEmpty()));
        }

        deleteUnauthorizedRequest(PATH + "/" + localityExist.getId());
    }

    protected static void assertLocality(Locality locality1, Locality locality2) {
        assertLocality(toEasyLocality(locality1), toEasyLocality(locality2));
        assertChristening(locality1.getChristenings(), locality2.getChristenings());
        assertDeath(locality1.getDeaths(), locality2.getDeaths());
        assertMarriage(locality1.getMarriagesWithWifeLocality(), locality2.getMarriagesWithWifeLocality());
        assertMarriage(locality1.getMarriagesWithHusbandLocality(), locality2.getMarriagesWithHusbandLocality());
        assertPerson(locality1.getPersonsWithDeathLocality(), locality2.getPersonsWithDeathLocality());
        assertPerson(locality1.getPersonsWithBirthLocality(), locality2.getPersonsWithBirthLocality());
    }

    protected static void assertLocality(EasyLocality locality1, EasyLocality locality2) {
        if (locality1 == null || locality2 == null) {
            assertNull(locality1);
            assertNull(locality2);
            return;
        }
        assertNotNull(locality1);
        assertNotNull(locality2);
        if (locality1.getId() != null && locality2.getId() != null) {
            assertEquals(locality1.getId(), locality2.getId());
        }
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType(), locality2.getType());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        locality1.getAnotherNames().
                forEach(anotherName -> assertTrue(locality2.getAnotherNames().contains(anotherName)));
    }

    protected static void assertLocality(EasyLocality locality1, genealogy.visualizer.entity.Locality locality2) {
        assertLocality(locality1, toEasyLocality(locality2));
    }

    protected static void assertLocality(Locality locality1, genealogy.visualizer.entity.Locality locality2) {
        assertLocality(toEasyLocality(locality1), toEasyLocality(locality2));
        assertChristenings(locality1.getChristenings(), locality2.getChristenings());
        assertDeaths(locality1.getDeaths(), locality2.getDeaths());
        assertMarriages(locality1.getMarriagesWithWifeLocality(), locality2.getMarriagesWithWifeLocality());
        assertMarriages(locality1.getMarriagesWithHusbandLocality(), locality2.getMarriagesWithHusbandLocality());
        assertPersons(locality1.getPersonsWithDeathLocality(), locality2.getPersonsWithDeathLocality());
        assertPersons(locality1.getPersonsWithBirthLocality(), locality2.getPersonsWithBirthLocality());
    }

    private static EasyLocality toEasyLocality(genealogy.visualizer.entity.Locality locality) {
        if (locality == null) {
            return null;
        }
        return new EasyLocality()
                .id(locality.getId())
                .name(locality.getName())
                .type(LocalityType.valueOf(locality.getType().name()))
                .address(locality.getAddress())
                .anotherNames(locality.getAnotherNames().stream().toList());
    }

    private static EasyLocality toEasyLocality(Locality locality) {
        if (locality == null) {
            return null;
        }
        return new EasyLocality()
                .id(locality.getId())
                .name(locality.getName())
                .type(locality.getType())
                .address(locality.getAddress())
                .anotherNames(locality.getAnotherNames());
    }

    private Locality getLocality(genealogy.visualizer.entity.Locality localityExist) {
        Locality locality = generator.nextObject(Locality.class);
        List<String> anotherNames = generator.objects(String.class, generator.nextInt(5, 10)).toList();
        locality.setAnotherNames(anotherNames);
        locality.setId(localityExist.getId());
        List<EasyChristening> christenings = new ArrayList<>(generator.objects(EasyChristening.class, generator.nextInt(2, 5)).toList());
        localityExist.getChristenings().forEach(c -> {
            if (generator.nextBoolean()) {
                christenings.add(easyChristeningMapper.toDTO(c));
            }
        });
        locality.setChristenings(christenings);
        List<EasyDeath> deaths = new ArrayList<>(generator.objects(EasyDeath.class, generator.nextInt(2, 5)).toList());
        localityExist.getDeaths().forEach(d -> {
            if (generator.nextBoolean()) {
                deaths.add(easyDeathMapper.toDTO(d));
            }
        });
        locality.setDeaths(deaths);
        List<EasyMarriage> marriagesWithWifeLocality = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(2, 5)).toList());
        localityExist.getMarriagesWithHusbandLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                marriagesWithWifeLocality.add(easyMarriageMapper.toDTO(m));
            }
        });
        locality.setMarriagesWithWifeLocality(marriagesWithWifeLocality);
        List<EasyMarriage> marriagesWithHusbandLocality = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(2, 5)).toList());
        localityExist.getMarriagesWithHusbandLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                marriagesWithHusbandLocality.add(easyMarriageMapper.toDTO(m));
            }
        });
        locality.setMarriagesWithHusbandLocality(marriagesWithHusbandLocality);
        List<EasyPerson> personsWithBirthLocality = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(2, 5)).toList());
        localityExist.getPersonsWithBirthLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                personsWithBirthLocality.add(easyPersonMapper.toDTO(m));
            }
        });
        locality.setPersonsWithBirthLocality(personsWithBirthLocality);
        List<EasyPerson> personsWithDeathLocality = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(2, 5)).toList());
        localityExist.getPersonsWithDeathLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                personsWithDeathLocality.add(easyPersonMapper.toDTO(m));
            }
        });
        locality.setPersonsWithDeathLocality(personsWithDeathLocality);
        return locality;
    }

}