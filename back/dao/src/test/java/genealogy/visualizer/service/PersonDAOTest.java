package genealogy.visualizer.service;

import genealogy.visualizer.JpaAbstractTest;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.entity.model.DateInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonDAOTest extends JpaAbstractTest {

    @Autowired
    private PersonDAO personDAO;

    @Test
    void saveBatchTest() {
        List<Person> persons = generator.objects(Person.class, generator.nextInt(10, 15)).toList();
        persons.forEach(person -> {
            person.setBirthLocality(locality);
            person.setDeathLocality(locality);
            person.setChristening(null);
            person.setDeath(null);
            person.setMarriages(Collections.emptyList());
            person.setRevisions(Collections.emptyList());
        });
        persons.forEach(person -> entityManager.persist(person));
        persons.get(0).setPartners(List.of(persons.get(1)));
        persons.get(2).setParents(List.of(persons.get(0), persons.get(1)));
        entityManager.flush();
        persons.get(1).setPartners(List.of(persons.get(0)));
        persons.get(0).setChildren(List.of(persons.get(2)));
        persons.get(1).setChildren(List.of(persons.get(2)));
        entityManager.flush();
        entityManager.clear();
        List<Person> resultPersons = personDAO.getAllEasyPersons().stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .toList();
        persons = persons.stream().sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).toList();
        for (int i = 0; i < resultPersons.size(); i++) {
            assertPerson(resultPersons.get(i), persons.get(i));
        }
    }

    private void assertPerson(Person person1, Person person2) {
        assertEquals(person1.getId(), person2.getId());
        assertFullName(person1.getFullName(), person2.getFullName());
        assertDateInfo(person1.getBirthDate(), person2.getBirthDate());
        assertDateInfo(person1.getDeathDate(), person2.getDeathDate());
        assertLocality(person1.getBirthLocality(), person2.getBirthLocality());
        assertLocality(person1.getDeathLocality(), person2.getDeathLocality());
        if (person1.getChristening() != null) {
            assertEquals(person1.getChristening().getId(), person2.getChristening().getId());
        }
        if (person1.getDeath() != null) {
            assertEquals(person1.getDeath().getId(), person2.getDeath().getId());
        }
        assertEquals(person1.getMarriages().size(), person2.getMarriages().size());
        if (!person1.getMarriages().isEmpty()) {
            List<Long> person2MarriageIds = person2.getMarriages().stream().map(Marriage::getId).toList();
            person1.getMarriages().forEach(parent -> assertTrue(person2MarriageIds.contains(parent.getId())));
        }
        assertEquals(person1.getRevisions().size(), person2.getRevisions().size());
        if (!person1.getRevisions().isEmpty()) {
            List<Long> person2RevisionIds = person2.getRevisions().stream().map(FamilyRevision::getId).toList();
            person1.getRevisions().forEach(parent -> assertTrue(person2RevisionIds.contains(parent.getId())));
        }
        assertEquals(person1.getParents().size(), person2.getParents().size());
        if (!person1.getParents().isEmpty()) {
            List<Long> person2ParentIds = person2.getParents().stream().map(Person::getId).toList();
            person1.getParents().forEach(parent -> assertTrue(person2ParentIds.contains(parent.getId())));
        }
        assertEquals(person1.getChildren().size(), person2.getChildren().size());
        if (!person1.getChildren().isEmpty()) {
            List<Long> person2ChildrenIds = person2.getChildren().stream().map(Person::getId).toList();
            person1.getChildren().forEach(parent -> assertTrue(person2ChildrenIds.contains(parent.getId())));
        }
        assertEquals(person1.getPartners().size(), person2.getPartners().size());
        if (!person1.getPartners().isEmpty()) {
            List<Long> person2PartnerIds = person2.getPartners().stream().map(Person::getId).toList();
            person1.getPartners().forEach(parent -> assertTrue(person2PartnerIds.contains(parent.getId())));
        }
    }

    private void assertDateInfo(DateInfo dateInfo1, DateInfo dateInfo2) {
        assertEquals(dateInfo1.getDate(), dateInfo2.getDate());
        assertEquals(dateInfo1.getDateRangeType(), dateInfo2.getDateRangeType());
    }

}
