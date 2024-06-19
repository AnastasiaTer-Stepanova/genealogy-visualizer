package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.ArchiveWithFamilyMembers;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.FamilyFilter;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.api.model.Sex;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static genealogy.visualizer.controller.ArchiveDocumentControllerTest.assertArchiveDocument;
import static genealogy.visualizer.controller.PersonControllerTest.assertPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FamilyRevisionControllerTest extends IntegrationTest {

    private static final String PATH = "/family-revision";

    private FamilyFilter familyFilter;

    @Test
    void getByIdTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionExist = existingFamilyRevisions.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null && e.getPartner() != null).findAny().orElse(existingFamilyRevisions.getFirst());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();

        FamilyMember response = objectMapper.readValue(getRequest(PATH + "/" + revisionExist.getId()), FamilyMember.class);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        assertFamilyRevision(response, revisionExist);

        getNotFoundRequest(PATH + "/" + generator.nextLong());
    }

    @Test
    void findByFilterTest() throws Exception {
        familyMemberFilter.isFindWithHavePerson(false);
        familyMemberFilter.setArchiveDocumentId(existingArchiveDocuments.stream().max(Comparator.comparingInt(ad -> ad.getFamilyRevisions().size()))
                .orElse(existingArchiveDocuments.getFirst()).getId());
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        List<EasyFamilyMember> response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(familyMemberFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyFamilyMember.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        List<Long> existIds = getIdsByFilter(this::matchesFilter, familyMemberFilter.getIsFindWithHavePerson());
        Set<Long> findIds = response.stream().map(EasyFamilyMember::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        familyMemberFilter.isFindWithHavePerson(true);
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        initialQueryExecutionCount = statistics.getQueryExecutionCount();
        response = objectMapper.readValue(getRequest(PATH + "/filter", objectMapper.writeValueAsString(familyMemberFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EasyFamilyMember.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        existIds = getIdsByFilter(this::matchesFilter, familyMemberFilter.getIsFindWithHavePerson());
        findIds = response.stream().map(EasyFamilyMember::getId).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        familyMemberFilter.getFullName().setName("Абракадабра");
        getNotFoundRequest(PATH + "/filter", objectMapper.writeValueAsString(familyMemberFilter));
    }

    @Test
    void findByFamilyFilterTest() throws Exception {
        familyFilter = new FamilyFilter()
                .familyRevisionNumber(familyMemberFilter.getFamilyRevisionNumber())
                .isFindInAllRevision(false)
                .isFindWithHavePerson(false);
        ArchiveDocument archiveDocument = existingArchiveDocuments.stream()
                .max(Comparator.comparingInt(ad -> ad.getFamilyRevisions().size()))
                .orElse(existingArchiveDocuments.getFirst());
        familyFilter.setArchiveDocumentId(archiveDocument.getId());
        existingFamilyRevisions = existingFamilyRevisions.stream()
                .map(fr -> {
                    if (fr.getArchiveDocument() != null && archiveDocument.getId().equals(fr.getArchiveDocument().getId()) && generator.nextBoolean()) {
                        fr.setFamilyRevisionNumber(familyMemberFilter.getFamilyRevisionNumber().shortValue());
                        if (fr.getPerson() != null && generator.nextBoolean()) {
                            fr.setPerson(null);
                        }
                        return familyRevisionDAO.update(fr);
                    }
                    return fr;
                }).toList();
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        long initialQueryExecutionCount = statistics.getQueryExecutionCount();
        List<FamilyMemberFullInfo> response = objectMapper.readValue(getRequest(PATH + "/family", objectMapper.writeValueAsString(familyFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FamilyMemberFullInfo.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        List<Long> existIds = getIdsByFilter(this::matchesFamilyFilter, familyFilter.getIsFindWithHavePerson());
        Set<Long> findIds = response.stream().map(fm -> fm.getFamilyMember().getId()).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));

        familyFilter.setIsFindWithHavePerson(true);
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        initialQueryExecutionCount = statistics.getQueryExecutionCount();
        response = objectMapper.readValue(getRequest(PATH + "/family", objectMapper.writeValueAsString(familyFilter)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FamilyMemberFullInfo.class));
        assertNotNull(response);
        assertEquals(1, statistics.getQueryExecutionCount() - initialQueryExecutionCount);
        existIds = getIdsByFilter(this::matchesFamilyFilter, familyFilter.getIsFindWithHavePerson());
        findIds = response.stream().map(fm -> fm.getFamilyMember().getId()).collect(Collectors.toSet());
        assertEquals(existIds.size(), findIds.size());
        assertTrue(existIds.containsAll(findIds));
    }

    @Test
    void findFamilyRevisionsAllInfoWithPersonTest() throws Exception {
        findFamilyRevisionsAllInfoTest(true);
    }

    @Test
    void findFamilyRevisionsAllInfoWithoutPersonTest() throws Exception {
        findFamilyRevisionsAllInfoTest(false);
    }

    @Test
    void saveTest() throws Exception {
        FamilyMember familyMemberSave = getFamilyMember(existingFamilyRevisions.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null && e.getPartner() != null).findAny().orElse(existingFamilyRevisions.getFirst()));
        familyMemberSave.setId(null);

        FamilyMember response = objectMapper.readValue(postRequest(PATH, objectMapper.writeValueAsString(familyMemberSave)), FamilyMember.class);
        assertFamilyRevision(response, familyMemberSave);

        FamilyMember responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), FamilyMember.class);
        assertFamilyRevision(responseGet, responseGet);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(familyMemberSave));
    }

    @Test
    void updateTest() throws Exception {
        FamilyMember familyMemberUpdate = getFamilyMember(existingFamilyRevisions.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null && e.getPartner() != null).findAny().orElse(existingFamilyRevisions.getFirst()));

        FamilyMember response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(familyMemberUpdate)), FamilyMember.class);
        assertFamilyRevision(response, familyMemberUpdate);

        FamilyMember responseGet = objectMapper.readValue(getRequest(PATH + "/" + response.getId()), FamilyMember.class);
        assertFamilyRevision(responseGet, familyMemberUpdate);

        postUnauthorizedRequest(PATH, objectMapper.writeValueAsString(familyMemberUpdate));

        familyMemberUpdate.setArchiveDocument(null);
        familyMemberUpdate.setPerson(null);
        familyMemberUpdate.setPartner(null);
        familyMemberUpdate.setAnotherNames(Collections.emptyList());

        response = objectMapper.readValue(putRequest(PATH, objectMapper.writeValueAsString(familyMemberUpdate)), FamilyMember.class);
        assertFamilyRevision(response, familyMemberUpdate);
    }

    @Test
    void deleteTest() throws Exception {
        genealogy.visualizer.entity.FamilyRevision revisionExist = existingFamilyRevisions.stream()
                .filter(e -> e.getPerson() != null && e.getArchiveDocument() != null && e.getPartner() != null).findAny().orElse(existingFamilyRevisions.getFirst());
        String responseJson = deleteRequest(PATH + "/" + revisionExist.getId());
        existingFamilyRevisions.remove(revisionExist);

        assertTrue(responseJson.isEmpty());
        assertTrue(familyRevisionRepository.findById(revisionExist.getId()).isEmpty());
        assertTrue(familyRevisionRepository.findAnotherNames(revisionExist.getId()).isEmpty());
        if (revisionExist.getPartner() != null) {
            assertFalse(familyRevisionRepository.findById(revisionExist.getPartner().getId()).isEmpty());
        }
        if (revisionExist.getArchiveDocument() != null) {
            assertFalse(archiveDocumentRepository.findById(revisionExist.getArchiveDocument().getId()).isEmpty());
        }
        if (revisionExist.getArchiveDocument().getArchive() != null) {
            assertFalse(archiveRepository.findById(revisionExist.getArchiveDocument().getArchive().getId()).isEmpty());
        }

        deleteUnauthorizedRequest(PATH + "/" + revisionExist.getId());
    }

    private List<Long> getIdsByFilter(Function<genealogy.visualizer.entity.FamilyRevision, Boolean> matchesFilter, Boolean isFindWithHavePerson) {
        List<genealogy.visualizer.entity.FamilyRevision> filteredFamilyRevision = existingFamilyRevisions.stream()
                .filter(matchesFilter::apply)
                .toList();
        if (!isFindWithHavePerson) {
            filteredFamilyRevision = filteredFamilyRevision.stream()
                    .filter(familyRevision -> familyRevision.getPerson() == null)
                    .toList();
        }
        return filteredFamilyRevision.stream().map(genealogy.visualizer.entity.FamilyRevision::getId).toList();
    }

    private boolean matchesFilter(genealogy.visualizer.entity.FamilyRevision familyRevision) {
        return containsIgnoreCase(familyRevision.getFullName().getName(), familyMemberFilter.getFullName().getName()) &&
                containsIgnoreCase(familyRevision.getFullName().getSurname(), familyMemberFilter.getFullName().getSurname()) &&
                containsIgnoreCase(familyRevision.getFullName().getLastName(), familyMemberFilter.getFullName().getLastName()) &&
                familyRevision.getArchiveDocument() != null && familyRevision.getArchiveDocument().getId().equals(familyMemberFilter.getArchiveDocumentId()) &&
                familyRevision.getFamilyRevisionNumber().intValue() == familyMemberFilter.getFamilyRevisionNumber() &&
                familyRevision.getSex().name().equals(familyMemberFilter.getSex().name());
    }

    private boolean matchesFamilyFilter(genealogy.visualizer.entity.FamilyRevision familyRevision) {
        return familyRevision.getArchiveDocument() != null && familyRevision.getArchiveDocument().getId().equals(familyFilter.getArchiveDocumentId()) &&
                familyRevision.getFamilyRevisionNumber().intValue() == familyFilter.getFamilyRevisionNumber();
    }

    private void findFamilyRevisionsAllInfoTest(boolean isFindWithHavePerson) throws Exception {
        genealogy.visualizer.entity.ArchiveDocument adParent = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adParent.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
        adParent = archiveDocumentRepository.saveAndFlush(adParent);
        Long adParentId = adParent.getId();
        genealogy.visualizer.entity.ArchiveDocument adChildLevel1 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adChildLevel1.setNextRevision(adParent);
        adChildLevel1.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
        adChildLevel1 = archiveDocumentRepository.saveAndFlush(adChildLevel1);
        Long adChildLevel1Id = adChildLevel1.getId();
        genealogy.visualizer.entity.ArchiveDocument adSecondChildLevel1 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adSecondChildLevel1.setNextRevision(adParent);
        adSecondChildLevel1.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
        adSecondChildLevel1 = archiveDocumentRepository.saveAndFlush(adSecondChildLevel1);
        Long adSecondChildLevel1Id = adSecondChildLevel1.getId();
        genealogy.visualizer.entity.ArchiveDocument adChildLevel2 = generator.nextObject(genealogy.visualizer.entity.ArchiveDocument.class);
        adChildLevel2.setNextRevision(adChildLevel1);
        adChildLevel2.setArchive(existingArchives.get(generator.nextInt(existingArchives.size())));
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
                    familyRevision.setPerson(getEmptySavedPerson());
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
                    familyRevision.setPerson(getEmptySavedPerson());
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
                    familyRevision.setPerson(getEmptySavedPerson());
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
                    familyRevision.setPerson(getEmptySavedPerson());
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
                familyRevision.setPerson(getEmptySavedPerson());
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
                .filter(familyRevision -> !(!isFindWithHavePerson && familyRevision.getPerson() != null) &&
                        familyRevision.getFamilyRevisionNumber().equals(familyRevisionNumberSecondChildLeve1))
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
                assertFamilyRevisions(familyMembers, existFamilyRevision);
            }
        }
    }

    protected static void assertFamilyRevision(EasyFamilyMember familyRevision1, FamilyMember familyRevision2) {
        assertFamilyRevision(familyRevision1, toEasyFamilyMember(familyRevision2));
    }

    protected static void assertFamilyRevisions(List<EasyFamilyMember> familyRevisions1, List<FamilyRevision> familyRevisions2) {
        assertNotNull(familyRevisions2);
        assertFamilyRevision(familyRevisions1, familyRevisions2.stream().map(FamilyRevisionControllerTest::toEasyFamilyMember).toList());
    }

    protected static void assertFamilyRevision(List<EasyFamilyMember> familyRevisions1, List<EasyFamilyMember> familyRevisions2) {
        if (familyRevisions1 == null || familyRevisions2 == null) {
            assertNull(familyRevisions1);
            assertNull(familyRevisions2);
            return;
        }
        assertNotNull(familyRevisions1);
        assertNotNull(familyRevisions2);
        assertEquals(familyRevisions1.size(), familyRevisions2.size());
        List<EasyFamilyMember> familyRevisionsSorted1 = familyRevisions1.stream().sorted(Comparator.comparing(fr -> fr.getFullName().getName())).toList();
        List<EasyFamilyMember> familyRevisionsSorted2 = familyRevisions2.stream().sorted(Comparator.comparing(fr -> fr.getFullName().getName())).toList();
        for (int i = 0; i < familyRevisionsSorted1.size(); i++) {
            assertFamilyRevision(familyRevisionsSorted1.get(i), familyRevisionsSorted2.get(i));
        }
    }

    protected static void assertFamilyRevision(EasyFamilyMember member1, EasyFamilyMember member2) {
        if (member1 == null || member2 == null) {
            assertNull(member1);
            assertNull(member2);
            return;
        }
        assertNotNull(member1);
        assertNotNull(member2);
        if (member1.getId() != null && member2.getId() != null) {
            assertEquals(member1.getId(), member2.getId());
        }
        assertEquals(member1.getFamilyRevisionNumber(), member2.getFamilyRevisionNumber());
        assertEquals(member1.getNextFamilyRevisionNumber(), member2.getNextFamilyRevisionNumber());
        assertEquals(member1.getListNumber(), member2.getListNumber());
        assertEquals(member1.getDeparted(), member2.getDeparted());
        assertEquals(member1.getArrived(), member2.getArrived());
        assertEquals(member1.getIsHeadOfYard(), member2.getIsHeadOfYard());
        assertEquals(member1.getFamilyGeneration(), member2.getFamilyGeneration());
        assertEquals(member1.getComment(), member2.getComment());
        assertEquals(member1.getSex(), member2.getSex());
        assertEquals(member1.getFullName(), member2.getFullName());
        assertEquals(member1.getRelative(), member2.getRelative());
        assertEquals(member1.getAge(), member2.getAge());
        assertEquals(member1.getAgeInNextRevision(), member2.getAgeInNextRevision());
        assertAnotherNames(member1.getAnotherNames(), member2.getAnotherNames().stream().toList());
    }

    protected static void assertFamilyRevision(EasyFamilyMember familyRevision1, FamilyRevision familyRevision2) {
        assertFamilyRevision(familyRevision1, toEasyFamilyMember(familyRevision2));
    }

    protected static void assertFamilyRevision(FamilyMember familyRevision1, FamilyMember familyRevision2) {
        assertFamilyRevision(toEasyFamilyMember(familyRevision1), toEasyFamilyMember(familyRevision2));
        assertArchiveDocument(familyRevision1.getArchiveDocument(), familyRevision2.getArchiveDocument());
        assertFamilyRevision(familyRevision1.getPartner(), familyRevision2.getPartner());
        assertPerson(familyRevision1.getPerson(), familyRevision2.getPerson());
    }

    protected static void assertFamilyRevision(FamilyMember familyRevision1, FamilyRevision familyRevision2) {
        assertFamilyRevision(toEasyFamilyMember(familyRevision1), toEasyFamilyMember(familyRevision2));
        assertArchiveDocument(familyRevision1.getArchiveDocument(), familyRevision2.getArchiveDocument());
        assertFamilyRevision(familyRevision1.getPartner(), familyRevision2.getPartner());
        assertPerson(familyRevision1.getPerson(), familyRevision2.getPerson());
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

    private genealogy.visualizer.entity.Person getEmptySavedPerson() {
        genealogy.visualizer.entity.Person personSave = generator.nextObject(genealogy.visualizer.entity.Person.class);
        personSave.setChristening(null);
        personSave.setPartners(Collections.emptyList());
        personSave.setChildren(Collections.emptyList());
        personSave.setRevisions(Collections.emptyList());
        personSave.setMarriages(Collections.emptyList());
        personSave.setParents(Collections.emptyList());
        personSave.setDeath(null);
        personSave.setDeathLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
        personSave.setBirthLocality(existingLocalities.get(generator.nextInt(existingLocalities.size())));
        return personRepository.saveAndFlush(personSave);
    }

    private static FamilyMember getFamilyMember(FamilyRevision familyRevision) {
        FamilyMember familyMember = generator.nextObject(FamilyMember.class);
        familyMember.setId(familyRevision.getId());
        familyMember.setArchiveDocument(generator.nextObject(EasyArchiveDocument.class));
        familyMember.setPerson(generator.nextObject(EasyPerson.class));
        EasyFamilyMember partner = generator.nextObject(EasyFamilyMember.class);
        partner.setAnotherNames(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        familyMember.setPartner(partner);
        familyMember.setAnotherNames(generator.objects(String.class, generator.nextInt(1, 3)).toList());
        return familyMember;
    }

    private static EasyFamilyMember toEasyFamilyMember(FamilyRevision familyRevision) {
        if (familyRevision == null) {
            return null;
        }
        return new EasyFamilyMember()
                .id(familyRevision.getId())
                .familyRevisionNumber(familyRevision.getFamilyRevisionNumber().intValue())
                .nextFamilyRevisionNumber(familyRevision.getNextFamilyRevisionNumber().intValue())
                .listNumber(familyRevision.getListNumber().intValue())
                .departed(familyRevision.getDeparted())
                .arrived(familyRevision.getArrived())
                .isHeadOfYard(familyRevision.getHeadOfYard())
                .isLastNameClearlyStated(familyRevision.getLastNameClearlyStated())
                .familyGeneration(familyRevision.getFamilyGeneration().intValue())
                .comment(familyRevision.getComment())
                .sex(Sex.valueOf(familyRevision.getSex().name()))
                .fullName(toFullName(familyRevision.getFullName()))
                .relative(toFullName(familyRevision.getRelative()))
                .age(toAge(familyRevision.getAge()))
                .ageInNextRevision(toAge(familyRevision.getAgeInNextRevision()))
                .anotherNames(familyRevision.getAnotherNames().stream().toList());
    }

    private static EasyFamilyMember toEasyFamilyMember(FamilyMember familyRevision) {
        if (familyRevision == null) {
            return null;
        }
        return new EasyFamilyMember()
                .id(familyRevision.getId())
                .familyRevisionNumber(familyRevision.getFamilyRevisionNumber())
                .nextFamilyRevisionNumber(familyRevision.getNextFamilyRevisionNumber())
                .listNumber(familyRevision.getListNumber())
                .departed(familyRevision.getDeparted())
                .arrived(familyRevision.getArrived())
                .isHeadOfYard(familyRevision.getIsHeadOfYard())
                .isLastNameClearlyStated(familyRevision.getIsLastNameClearlyStated())
                .familyGeneration(familyRevision.getFamilyGeneration())
                .comment(familyRevision.getComment())
                .sex(familyRevision.getSex())
                .fullName(familyRevision.getFullName())
                .relative(familyRevision.getRelative())
                .age(familyRevision.getAge())
                .ageInNextRevision(familyRevision.getAgeInNextRevision())
                .anotherNames(familyRevision.getAnotherNames());
    }
}