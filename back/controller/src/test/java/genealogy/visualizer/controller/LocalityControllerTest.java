package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.api.model.LocalityType;
import genealogy.visualizer.service.LocalityDAO;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ChristeningControllerTest.assertChristening;
import static genealogy.visualizer.controller.DeathControllerTest.assertDeath;
import static genealogy.visualizer.controller.MarriageControllerTest.assertMarriage;
import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalityControllerTest extends IntegrationTest {

    @Autowired
    LocalityDAO localityDAO;

    private static final String PATH = "/locality";

    @Test
    void saveTest() throws Exception {
        Locality localitySave = generator.nextObject(Locality.class);
        List<String> anotherNames = generator.objects(String.class, generator.nextInt(5, 10)).toList();
        localitySave.setAnotherNames(anotherNames);
        List<EasyChristening> christeningsSave = generator.objects(EasyChristening.class, generator.nextInt(5, 10)).toList();
        localitySave.setChristenings(christeningsSave);
        List<EasyDeath> deathsSave = generator.objects(EasyDeath.class, generator.nextInt(5, 10)).toList();
        localitySave.setDeaths(deathsSave);
        List<EasyMarriage> marriagesWithHusbandLocality = generator.objects(EasyMarriage.class, generator.nextInt(5, 10)).toList();
        localitySave.setMarriagesWithHusbandLocality(marriagesWithHusbandLocality);
        List<EasyMarriage> marriagesWithWifeLocality = generator.objects(EasyMarriage.class, generator.nextInt(5, 10)).toList();
        localitySave.setMarriagesWithWifeLocality(marriagesWithWifeLocality);
        List<EasyPerson> personsWithBirthLocality = generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList();
        localitySave.setPersonsWithBirthLocality(personsWithBirthLocality);
        List<EasyPerson> personsWithDeathLocality = generator.objects(EasyPerson.class, generator.nextInt(5, 10)).toList();
        localitySave.setPersonsWithDeathLocality(personsWithDeathLocality);
        String responseJson = postRequest(PATH, objectMapper.writeValueAsString(localitySave));
        Locality response = objectMapper.readValue(responseJson, Locality.class);
        assertNotNull(response);
        assertLocality(response, localitySave);
        String responseJsonGet = getRequest(PATH + "/" + response.getId());
        Locality responseGet = objectMapper.readValue(responseJsonGet, Locality.class);
        assertNotNull(responseGet);
        assertLocality(responseGet, localitySave);
    }

    @Test
    void updateTest() throws Exception {
        genealogy.visualizer.entity.Locality localityExist = generateRandomExistLocality();
        Locality localityUpdate = generator.nextObject(Locality.class);
        List<String> anotherNames = generator.objects(String.class, generator.nextInt(5, 10)).toList();
        localityUpdate.setAnotherNames(anotherNames);
        localityUpdate.setId(localityExist.getId());
        List<EasyChristening> christeningsUpdate = new ArrayList<>(generator.objects(EasyChristening.class, generator.nextInt(2, 5)).toList());
        localityExist.getChristenings().forEach(c -> {
            if (generator.nextBoolean()) {
                christeningsUpdate.add(easyChristeningMapper.toDTO(c));
            }
        });
        localityUpdate.setChristenings(christeningsUpdate);
        List<EasyDeath> deathsUpdate = new ArrayList<>(generator.objects(EasyDeath.class, generator.nextInt(2, 5)).toList());
        localityExist.getDeaths().forEach(d -> {
            if (generator.nextBoolean()) {
                deathsUpdate.add(easyDeathMapper.toDTO(d));
            }
        });
        localityUpdate.setDeaths(deathsUpdate);
        List<EasyMarriage> marriagesWithWifeLocalityUpdate = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(2, 5)).toList());
        localityExist.getMarriagesWithHusbandLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                marriagesWithWifeLocalityUpdate.add(easyMarriageMapper.toDTO(m));
            }
        });
        localityUpdate.setMarriagesWithWifeLocality(marriagesWithWifeLocalityUpdate);
        List<EasyMarriage> marriagesWithHusbandLocalityUpdate = new ArrayList<>(generator.objects(EasyMarriage.class, generator.nextInt(2, 5)).toList());
        localityExist.getMarriagesWithHusbandLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                marriagesWithHusbandLocalityUpdate.add(easyMarriageMapper.toDTO(m));
            }
        });
        localityUpdate.setMarriagesWithHusbandLocality(marriagesWithHusbandLocalityUpdate);
        List<EasyPerson> personsWithBirthLocalityUpdate = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(2, 5)).toList());
        localityExist.getPersonsWithBirthLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                personsWithBirthLocalityUpdate.add(easyPersonMapper.toDTO(m));
            }
        });
        localityUpdate.setPersonsWithBirthLocality(personsWithBirthLocalityUpdate);
        List<EasyPerson> personsWithDeathLocalityUpdate = new ArrayList<>(generator.objects(EasyPerson.class, generator.nextInt(2, 5)).toList());
        localityExist.getPersonsWithDeathLocality().forEach(m -> {
            if (generator.nextBoolean()) {
                personsWithDeathLocalityUpdate.add(easyPersonMapper.toDTO(m));
            }
        });
        localityUpdate.setPersonsWithDeathLocality(personsWithDeathLocalityUpdate);
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(localityUpdate));
        Locality response = objectMapper.readValue(responseJson, Locality.class);
        assertNotNull(response);
        assertLocality(response, localityUpdate);
        String responseJsonGet = getRequest(PATH + "/" + localityUpdate.getId());
        Locality responseGet = objectMapper.readValue(responseJsonGet, Locality.class);
        assertNotNull(responseGet);
        assertLocality(responseGet, localityUpdate);
    }

    @Test
    void updateWithNullFieldTest() throws Exception {
        genealogy.visualizer.entity.Locality localityExist = generateRandomExistLocality();
        Locality localityUpdate = generator.nextObject(Locality.class);
        localityUpdate.setId(localityExist.getId());
        localityUpdate.setChristenings(Collections.emptyList());
        localityUpdate.setDeaths(Collections.emptyList());
        localityUpdate.setMarriagesWithHusbandLocality(Collections.emptyList());
        localityUpdate.setMarriagesWithWifeLocality(Collections.emptyList());
        localityUpdate.setPersonsWithDeathLocality(Collections.emptyList());
        localityUpdate.setPersonsWithBirthLocality(Collections.emptyList());
        String responseJson = putRequest(PATH, objectMapper.writeValueAsString(localityUpdate));
        Locality response = objectMapper.readValue(responseJson, Locality.class);
        assertNotNull(response);
        assertLocality(response, localityUpdate);
        String responseJsonGet = getRequest(PATH + "/" + localityUpdate.getId());
        Locality responseGet = objectMapper.readValue(responseJsonGet, Locality.class);
        assertNotNull(responseGet);
        assertLocality(responseGet, localityUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.Locality locality = generateRandomExistLocality();
        String responseJson = deleteRequest(PATH + "/" + locality.getId());
        assertTrue(responseJson.isEmpty());
        assertTrue(localityRepository.findById(locality.getId()).isEmpty());
        locality.getDeaths().forEach(p -> assertFalse(deathRepository.findById(p.getId()).isEmpty()));
        locality.getChristenings().forEach(p -> assertFalse(christeningRepository.findById(p.getId()).isEmpty()));
        locality.getMarriagesWithHusbandLocality().forEach(p -> assertFalse(marriageRepository.findById(p.getId()).isEmpty()));
        locality.getMarriagesWithWifeLocality().forEach(p -> assertFalse(marriageRepository.findById(p.getId()).isEmpty()));
        locality.getPersonsWithDeathLocality().forEach(r -> assertFalse(personRepository.findById(r.getId()).isEmpty()));
        locality.getPersonsWithBirthLocality().forEach(m -> assertFalse(personRepository.findById(m.getId()).isEmpty()));
    }

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.Locality locality = generateRandomExistLocality();
        String responseJson = getRequest(PATH + "/" + locality.getId());
        Locality response = objectMapper.readValue(responseJson, Locality.class);
        assertNotNull(response);
        assertEquals(response.getId(), locality.getId());
        assertLocality(response, locality);
    }

    @Test
    void findByFilterTest() throws Exception {
        StringRandomizer stringRandomizer = new StringRandomizer(3);
        LocalityFilter filter = new LocalityFilter()
                .address("Россия")
                .name("Хреновое")
                .type(LocalityType.TOWN);
        List<genealogy.visualizer.entity.Locality> localitiesSave = generator.objects(genealogy.visualizer.entity.Locality.class, generator.nextInt(5, 10)).toList();
        byte count = 0;
        for (genealogy.visualizer.entity.Locality locality : localitiesSave) {
            locality.setDeaths(Collections.emptyList());
            locality.setChristenings(Collections.emptyList());
            locality.setMarriagesWithHusbandLocality(Collections.emptyList());
            locality.setMarriagesWithWifeLocality(Collections.emptyList());
            locality.setPersonsWithBirthLocality(Collections.emptyList());
            locality.setPersonsWithDeathLocality(Collections.emptyList());
            if (generator.nextBoolean()) {
                locality.setName(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getName() : filter.getName().toUpperCase()) +
                        stringRandomizer.getRandomValue());
                locality.setAddress(stringRandomizer.getRandomValue() +
                        (generator.nextBoolean() ? filter.getAddress() : filter.getAddress().toLowerCase()) +
                        stringRandomizer.getRandomValue());
                locality.setType(genealogy.visualizer.entity.enums.LocalityType.TOWN);
                count++;
            }
        }
        List<genealogy.visualizer.entity.Locality> localitiesExist = localityRepository.saveAllAndFlush(localitiesSave);
        String responseJson = getRequest(PATH + "/filter", objectMapper.writeValueAsString(filter));
        List<EasyLocality> response = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, EasyLocality.class));
        assertNotNull(response);
        assertEquals(response.size(), count);
        Set<Long> findIds = response.stream().map(EasyLocality::getId).collect(Collectors.toSet());
        for (genealogy.visualizer.entity.Locality locality : localitiesExist) {
            if (locality.getAddress().toLowerCase().contains(filter.getAddress().toLowerCase()) &&
                    locality.getName().toLowerCase().contains(filter.getName().toLowerCase()) &&
                    locality.getType().name().equals(filter.getType().name())) {
                assertTrue(findIds.contains(locality.getId()));
            }
        }
    }

    @Test
    void saveUnauthorizedTest() throws Exception {
        Locality object = generator.nextObject(Locality.class);
        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void updateUnauthorizedTest() throws Exception {
        Locality object = generator.nextObject(Locality.class);
        putUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @Test
    void deleteUnauthorizedTest() throws Exception {
        Locality object = generator.nextObject(Locality.class);
        deleteUnauthorizedRequest(PATH, objectMapper.writeValueAsString(object));
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        christeningRepository.deleteAll();
        deathRepository.deleteAll();
        personRepository.deleteAll();
        marriageRepository.deleteAll();
        super.tearDown();
    }

    protected static void assertLocality(Locality locality1, Locality locality2) {
        if (locality1 == null || locality2 == null) {
            assertNull(locality1);
            assertNull(locality2);
            return;
        }
        assertNotNull(locality1);
        assertNotNull(locality2);
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType(), locality2.getType());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        locality1.getAnotherNames().
                forEach(anotherName -> assertTrue(locality2.getAnotherNames().contains(anotherName)));
        if (locality2.getChristenings() != null) {
            assertEquals(locality1.getChristenings().size(), locality2.getChristenings().size());
            List<EasyChristening> christenings1 = locality1.getChristenings().stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
            List<EasyChristening> christenings2 = locality2.getChristenings().stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
            for (int i = 0; i < christenings1.size(); i++) {
                assertChristening(christenings1.get(i), christenings2.get(i));
            }
        }
        if (locality2.getDeaths() != null) {
            assertEquals(locality1.getDeaths().size(), locality2.getDeaths().size());
            List<EasyDeath> deaths1 = locality1.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<EasyDeath> deaths2 = locality2.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < deaths1.size(); i++) {
                assertDeath(deaths1.get(i), deaths2.get(i));
            }
        }
        if (locality2.getMarriagesWithWifeLocality() != null) {
            assertEquals(locality1.getMarriagesWithWifeLocality().size(), locality2.getMarriagesWithWifeLocality().size());
            List<EasyMarriage> marriages1 = locality1.getMarriagesWithWifeLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            List<EasyMarriage> marriages2 = locality2.getMarriagesWithWifeLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            for (int i = 0; i < marriages1.size(); i++) {
                assertMarriage(marriages1.get(i), marriages2.get(i));
            }
        }
        if (locality2.getMarriagesWithHusbandLocality() != null) {
            assertEquals(locality1.getMarriagesWithHusbandLocality().size(), locality2.getMarriagesWithHusbandLocality().size());
            List<EasyMarriage> marriages1 = locality1.getMarriagesWithHusbandLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            List<EasyMarriage> marriages2 = locality2.getMarriagesWithHusbandLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            for (int i = 0; i < marriages1.size(); i++) {
                assertMarriage(marriages1.get(i), marriages2.get(i));
            }
        }
        if (locality2.getPersonsWithDeathLocality() != null) {
            assertEquals(locality1.getPersonsWithDeathLocality().size(), locality2.getPersonsWithDeathLocality().size());
            List<EasyPerson> persons1 = locality1.getPersonsWithDeathLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            List<EasyPerson> persons2 = locality2.getPersonsWithDeathLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            for (int i = 0; i < persons2.size(); i++) {
                assertPerson(persons1.get(i), persons2.get(i));
            }
        }
        if (locality2.getPersonsWithBirthLocality() != null) {
            assertEquals(locality1.getPersonsWithBirthLocality().size(), locality2.getPersonsWithBirthLocality().size());
            List<EasyPerson> persons1 = locality1.getPersonsWithBirthLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            List<EasyPerson> persons2 = locality2.getPersonsWithBirthLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            for (int i = 0; i < persons2.size(); i++) {
                assertPerson(persons1.get(i), persons2.get(i));
            }
        }
    }

    protected static void assertLocality(EasyLocality locality1, EasyLocality locality2) {
        if (locality1 == null || locality2 == null) {
            assertNull(locality1);
            assertNull(locality2);
            return;
        }
        assertNotNull(locality1);
        assertNotNull(locality2);
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType(), locality2.getType());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        locality1.getAnotherNames().
                forEach(anotherName -> assertTrue(locality2.getAnotherNames().contains(anotherName)));
    }

    protected static void assertLocality(EasyLocality locality1, genealogy.visualizer.entity.Locality locality2) {
        assertNotNull(locality1);
        assertNotNull(locality2);
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType().name(), locality2.getType().name());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        locality1.getAnotherNames().
                forEach(anotherName -> assertTrue(locality2.getAnotherNames().contains(anotherName)));
    }

    protected static void assertLocality(Locality locality1, genealogy.visualizer.entity.Locality locality2) {
        if (locality1 == null || locality2 == null) {
            assertNull(locality1);
            assertNull(locality2);
            return;
        }
        assertNotNull(locality1);
        assertNotNull(locality2);
        assertEquals(locality1.getName(), locality2.getName());
        assertEquals(locality1.getAddress(), locality2.getAddress());
        assertEquals(locality1.getType().getValue(), locality2.getType().getName());
        assertEquals(locality1.getAnotherNames().size(), locality2.getAnotherNames().size());
        locality1.getAnotherNames().
                forEach(anotherName -> assertTrue(locality2.getAnotherNames().contains(anotherName)));
        if (locality2.getChristenings() != null) {
            assertEquals(locality1.getChristenings().size(), locality2.getChristenings().size());
            List<EasyChristening> christenings1 = locality1.getChristenings().stream().sorted(Comparator.comparing(EasyChristening::getName)).toList();
            List<genealogy.visualizer.entity.Christening> christenings2 = locality2.getChristenings().stream().sorted(Comparator.comparing(genealogy.visualizer.entity.Christening::getName)).toList();
            for (int i = 0; i < christenings1.size(); i++) {
                assertChristening(christenings1.get(i), christenings2.get(i));
            }
        }
        if (locality2.getDeaths() != null) {
            assertEquals(locality1.getDeaths().size(), locality2.getDeaths().size());
            List<EasyDeath> deaths1 = locality1.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.Death> deaths2 = locality2.getDeaths().stream().sorted(Comparator.comparing(d -> d.getFullName().getName())).toList();
            for (int i = 0; i < deaths1.size(); i++) {
                assertDeath(deaths1.get(i), deaths2.get(i));
            }
        }
        if (locality2.getMarriagesWithWifeLocality() != null) {
            assertEquals(locality1.getMarriagesWithWifeLocality().size(), locality2.getMarriagesWithWifeLocality().size());
            List<EasyMarriage> marriages1 = locality1.getMarriagesWithWifeLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            List<genealogy.visualizer.entity.Marriage> marriages2 = locality2.getMarriagesWithWifeLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            for (int i = 0; i < marriages1.size(); i++) {
                assertMarriage(marriages1.get(i), marriages2.get(i));
            }
        }
        if (locality2.getMarriagesWithHusbandLocality() != null) {
            assertEquals(locality1.getMarriagesWithHusbandLocality().size(), locality2.getMarriagesWithHusbandLocality().size());
            List<EasyMarriage> marriages1 = locality1.getMarriagesWithHusbandLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            List<genealogy.visualizer.entity.Marriage> marriages2 = locality2.getMarriagesWithHusbandLocality().stream().sorted(Comparator.comparing(m -> m.getWife().getName())).toList();
            for (int i = 0; i < marriages1.size(); i++) {
                assertMarriage(marriages1.get(i), marriages2.get(i));
            }
        }
        if (locality2.getPersonsWithDeathLocality() != null) {
            assertEquals(locality1.getPersonsWithDeathLocality().size(), locality2.getPersonsWithDeathLocality().size());
            List<EasyPerson> persons1 = locality1.getPersonsWithDeathLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.Person> persons2 = locality2.getPersonsWithDeathLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            for (int i = 0; i < persons2.size(); i++) {
                assertPerson(persons1.get(i), persons2.get(i));
            }
        }
        if (locality2.getPersonsWithBirthLocality() != null) {
            assertEquals(locality1.getPersonsWithBirthLocality().size(), locality2.getPersonsWithBirthLocality().size());
            List<EasyPerson> persons1 = locality1.getPersonsWithBirthLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            List<genealogy.visualizer.entity.Person> persons2 = locality2.getPersonsWithBirthLocality().stream().sorted(Comparator.comparing(p -> p.getFullName().getName())).toList();
            for (int i = 0; i < persons2.size(); i++) {
                assertPerson(persons1.get(i), persons2.get(i));
            }
        }
    }

    private genealogy.visualizer.entity.Locality generateRandomExistLocality() {
        genealogy.visualizer.entity.Locality localitySave = generator.nextObject(genealogy.visualizer.entity.Locality.class);
        localitySave.setDeaths(Collections.emptyList());
        localitySave.setChristenings(Collections.emptyList());
        localitySave.setMarriagesWithWifeLocality(Collections.emptyList());
        localitySave.setMarriagesWithHusbandLocality(Collections.emptyList());
        localitySave.setPersonsWithDeathLocality(Collections.emptyList());
        localitySave.setPersonsWithBirthLocality(Collections.emptyList());
        localitySave.setAnotherNames(generator.objects(String.class, generator.nextInt(5, 10)).collect(Collectors.toSet()));
        genealogy.visualizer.entity.Locality localityExist = localityRepository.saveAndFlush(localitySave);

        List<genealogy.visualizer.entity.Christening> christeningsSave = generator.objects(genealogy.visualizer.entity.Christening.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Christening> christeningsExist = new ArrayList<>(christeningsSave.size());
        for (genealogy.visualizer.entity.Christening entity : christeningsSave) {
            entity.getGodParents().forEach(gp -> localityMapper.toDTO(localityExisting));
            entity.setLocality(localityExist);
            entity.setArchiveDocument(null);
            christeningsExist.add(christeningRepository.saveAndFlush(entity));
        }
        localityExist.setChristenings(christeningsExist);

        List<genealogy.visualizer.entity.Death> deathsSave = generator.objects(genealogy.visualizer.entity.Death.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Death> deathsExist = new ArrayList<>(deathsSave.size());
        for (genealogy.visualizer.entity.Death entity : deathsSave) {
            entity.setLocality(localityExist);
            entity.setArchiveDocument(null);
            deathsExist.add(deathRepository.saveAndFlush(entity));
        }
        localityExist.setDeaths(deathsExist);

        List<genealogy.visualizer.entity.Marriage> marriagesWithHusbandLocalitySave = generator.objects(genealogy.visualizer.entity.Marriage.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Marriage> marriagesWithHusbandLocalityExist = new ArrayList<>(marriagesWithHusbandLocalitySave.size());
        for (genealogy.visualizer.entity.Marriage entity : marriagesWithHusbandLocalitySave) {
            entity.setWifeLocality(localityExisting);
            entity.setHusbandLocality(localityExist);
            entity.setArchiveDocument(null);
            marriagesWithHusbandLocalityExist.add(marriageRepository.saveAndFlush(entity));
        }
        localityExist.setMarriagesWithHusbandLocality(marriagesWithHusbandLocalityExist);

        List<genealogy.visualizer.entity.Marriage> marriagesWithWifeLocalitySave = generator.objects(genealogy.visualizer.entity.Marriage.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Marriage> marriagesWithWifeLocalityExist = new ArrayList<>(marriagesWithWifeLocalitySave.size());
        for (genealogy.visualizer.entity.Marriage entity : marriagesWithWifeLocalitySave) {
            entity.setHusbandLocality(localityExisting);
            entity.setWifeLocality(localityExist);
            entity.setArchiveDocument(null);
            marriagesWithWifeLocalityExist.add(marriageRepository.saveAndFlush(entity));
        }
        localityExist.setMarriagesWithWifeLocality(marriagesWithWifeLocalityExist);

        List<genealogy.visualizer.entity.Person> personsWithBirthLocalitySave = generator.objects(genealogy.visualizer.entity.Person.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Person> personsWithBirthLocalityExist = new ArrayList<>(personsWithBirthLocalitySave.size());
        for (genealogy.visualizer.entity.Person entity : personsWithBirthLocalitySave) {
            entity.setChristening(null);
            entity.setPartners(Collections.emptyList());
            entity.setChildren(Collections.emptyList());
            entity.setRevisions(Collections.emptyList());
            entity.setMarriages(Collections.emptyList());
            entity.setParents(Collections.emptyList());
            entity.setDeath(null);
            entity.setDeathLocality(localityExisting);
            entity.setBirthLocality(localityExist);
            personsWithBirthLocalityExist.add(personRepository.saveAndFlush(entity));
        }
        localityExist.setPersonsWithBirthLocality(personsWithBirthLocalityExist);

        List<genealogy.visualizer.entity.Person> personsWithDeathLocalitySave = generator.objects(genealogy.visualizer.entity.Person.class, generator.nextInt(5, 10)).toList();
        List<genealogy.visualizer.entity.Person> personsWithDeathLocalityExist = new ArrayList<>(personsWithDeathLocalitySave.size());
        for (genealogy.visualizer.entity.Person entity : personsWithDeathLocalitySave) {
            entity.setChristening(null);
            entity.setPartners(Collections.emptyList());
            entity.setChildren(Collections.emptyList());
            entity.setRevisions(Collections.emptyList());
            entity.setMarriages(Collections.emptyList());
            entity.setParents(Collections.emptyList());
            entity.setDeath(null);
            entity.setBirthLocality(localityExisting);
            entity.setDeathLocality(localityExist);
            personsWithDeathLocalityExist.add(personRepository.saveAndFlush(entity));
        }
        localityExist.setPersonsWithDeathLocality(personsWithDeathLocalityExist);
        return localityExist;
    }

}