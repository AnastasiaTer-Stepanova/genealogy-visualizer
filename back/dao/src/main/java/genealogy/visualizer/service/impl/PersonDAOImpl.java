package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.DeathDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.LocalityDAO;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.PersonDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static genealogy.visualizer.service.helper.FilterHelper.addFullNameFilter;

public class PersonDAOImpl implements PersonDAO {

    private final PersonRepository personRepository;
    private final LocalityDAO localityDAO;
    private final ChristeningDAO christeningDAO;
    private final DeathDAO deathDAO;
    private final MarriageDAO marriageDAO;
    private final FamilyRevisionDAO familyRevisionDAO;
    private final EntityManager entityManager;

    public PersonDAOImpl(PersonRepository personRepository,
                         LocalityDAO localityDAO,
                         ChristeningDAO christeningDAO,
                         DeathDAO deathDAO,
                         MarriageDAO marriageDAO,
                         FamilyRevisionDAO familyRevisionDAO,
                         EntityManager entityManager) {
        this.personRepository = personRepository;
        this.localityDAO = localityDAO;
        this.christeningDAO = christeningDAO;
        this.deathDAO = deathDAO;
        this.marriageDAO = marriageDAO;
        this.familyRevisionDAO = familyRevisionDAO;
        this.entityManager = entityManager;
    }

    @Override
    public List<Person> getAllEasyPersons() {
        return personRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        personRepository.deleteParentLinkById(id);
        personRepository.deletePartnerLinkById(id);
        personRepository.deleteMarriageLinkById(id);
        christeningDAO.updatePersonIdByPersonId(id, null);
        deathDAO.updatePersonIdByPersonId(id, null);
        familyRevisionDAO.updatePersonIdByPersonId(id, null);
        personRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Person save(Person person) {
        if (person.getId() != null)
            throw new IllegalArgumentException("Cannot save person with id");
        person = saveLocalitiesIfNotExist(person);
        person = updateInfo(person, person);
        person = personRepository.save(person);
        return updateLinksForSavePerson(person);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Person update(Person person) {
        if (person.getId() == null)
            throw new IllegalArgumentException("Cannot update person without id");
        person = saveLocalitiesIfNotExist(person);
        Person updatedPerson = personRepository.update(person);
        if (updatedPerson == null) {
            return null;
        }
        return updateInfo(updatedPerson, person);
    }

    @Override
    @Transactional(readOnly = true)
    public Person findFullInfoById(Long id) {
        Person person = personRepository.findByIdWithMarriages(id).orElse(null);
        if (person == null) return null;
        personRepository.findByIdWithChildren(id).orElseThrow();
        personRepository.findByIdWithParents(id).orElseThrow();
        personRepository.findByIdWithPartners(id).orElseThrow();
        return personRepository.findFullInfoById(id).orElseThrow();
    }

    @Override
    public List<Person> filter(PersonFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> root = cq.from(Person.class);
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getBirthYear() != null) {
            predicates.add(cb.like(cb.lower(root.get("birthDate").get("date")), "%" + filter.getBirthYear() + "%"));
        }
        if (filter.getDeathYear() != null) {
            predicates.add(cb.like(cb.lower(root.get("deathDate").get("date")), "%" + filter.getDeathYear() + "%"));
        }
        if (filter.getSex() != null) {
            predicates.add(cb.equal(cb.lower(root.get("sex")), filter.getSex().getName().toLowerCase()));
        }
        predicates.addAll(addFullNameFilter(cb, root, filter.getFullName(), "fullName"));
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected List<Person> saveIfNotExist(List<Person> persons) {
        if (persons == null || persons.isEmpty()) return Collections.emptyList();
        List<Person> savedPersons = new ArrayList<>(persons.size());
        for (Person person : persons) {
            if (person.getId() == null) {
                savedPersons.add(this.save(person));
            } else {
                savedPersons.add(personRepository.findById(person.getId()).orElseThrow());
            }
        }
        return savedPersons;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person saveLocalitiesIfNotExist(Person person) {
        Locality birthLocality = person.getBirthLocality();
        if (birthLocality != null && birthLocality.getId() == null) {
            birthLocality = localityDAO.saveOrFindIfExist(birthLocality);
            person.setBirthLocality(birthLocality);
        }
        Locality deathLocality = person.getDeathLocality();
        if (deathLocality != null && deathLocality.getId() == null) {
            deathLocality = localityDAO.saveOrFindIfExist(deathLocality);
            person.setDeathLocality(deathLocality);
        }
        return person;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected List<Marriage> saveMarriagesIfNotExist(List<Marriage> marriages) {
        if (marriages == null || marriages.isEmpty()) return Collections.emptyList();
        for (Marriage marriage : marriages) {
            if (marriage.getId() == null) {
                marriageDAO.save(marriage);
            }
        }
        return marriages;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected List<FamilyRevision> saveRevisionsIfNotExist(List<FamilyRevision> revisions) {
        if (revisions == null || revisions.isEmpty()) return Collections.emptyList();
        for (FamilyRevision revision : revisions) {
            if (revision.getId() == null) {
                familyRevisionDAO.save(revision);
            }
        }
        return revisions;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateChristening(Person updatedPerson, Christening christening) {
        if (christening != null) {
            if (christening.getId() == null) {
                christening = christeningDAO.save(christening);
            }
            if (updatedPerson.getId() == null) {
                return updatedPerson;
            }
            if (updatedPerson.getChristening() != null && updatedPerson.getChristening().getId() != null
                    && !christening.getId().equals(updatedPerson.getChristening().getId())) {
                christeningDAO.updatePersonIdById(updatedPerson.getChristening().getId(), null);
            }
            christeningDAO.updatePersonIdById(christening.getId(), updatedPerson.getId());
            updatedPerson.setChristening(christening);
        } else if (updatedPerson.getChristening() != null) {
            christeningDAO.updatePersonIdByPersonId(updatedPerson.getId(), null);
            updatedPerson.setChristening(null);
        }
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateDeath(Person updatedPerson, Death death) {
        if (death != null) {
            if (death.getId() == null) {
                death = deathDAO.save(death);
            }
            if (updatedPerson.getId() == null) {
                return updatedPerson;
            }
            if (updatedPerson.getDeath() != null && updatedPerson.getDeath().getId() != null &&
                    !death.getId().equals(updatedPerson.getDeath().getId())) {
                deathDAO.updatePersonIdById(updatedPerson.getDeath().getId(), null);
            }
            deathDAO.updatePersonIdById(death.getId(), updatedPerson.getId());
            updatedPerson.setDeath(death);
        } else if (updatedPerson.getDeath() != null) {
            deathDAO.updatePersonIdByPersonId(updatedPerson.getId(), null);
            updatedPerson.setDeath(null);
        }
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateRevisions(Person updatedPerson, List<FamilyRevision> revisions) {
        revisions = saveRevisionsIfNotExist(revisions);
        if (updatedPerson.getRevisions() == null || updatedPerson.getRevisions().isEmpty()) {
            revisions.forEach(revision -> familyRevisionDAO.updatePersonIdById(revision.getId(), updatedPerson.getId()));
            updatedPerson.setRevisions(revisions);
            return updatedPerson;
        }
        Set<Long> newIds = revisions.stream().map(FamilyRevision::getId).collect(Collectors.toSet());
        Set<Long> existIds = updatedPerson.getRevisions().stream().map(FamilyRevision::getId).collect(Collectors.toSet());
        if (!newIds.equals(existIds)) {
            List<FamilyRevision> resultRevision = new ArrayList<>();
            Set<Long> idsForAdd = newIds.stream().filter(existIds::contains).collect(Collectors.toSet());
            Set<Long> idsForDelete = existIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());
            for (FamilyRevision revision : updatedPerson.getRevisions()) {
                if (idsForDelete.contains(revision.getId())) {
                    familyRevisionDAO.updatePersonIdByPersonId(updatedPerson.getId(), null);
                } else {
                    resultRevision.add(revision);
                }
            }
            for (FamilyRevision revision : revisions) {
                if (idsForAdd.contains(revision.getId())) {
                    familyRevisionDAO.updatePersonIdById(revision.getId(), updatedPerson.getId());
                    resultRevision.add(revision);
                }
            }
            updatedPerson.setRevisions(resultRevision);
        }
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateMarriages(Person updatedPerson, List<Marriage> marriages) {
        marriages = saveMarriagesIfNotExist(marriages);
        if (updatedPerson.getMarriages() == null || updatedPerson.getMarriages().isEmpty()) {
            updatedPerson.setMarriages(marriages);
            return updatedPerson;
        }
        Set<Long> newIds = marriages.stream().map(Marriage::getId).collect(Collectors.toSet());
        for (Marriage marriage : updatedPerson.getMarriages()) {
            if (!newIds.contains(marriage.getId())) {
                personRepository.deleteMarriageLinkById(updatedPerson.getId());
            }
        }
        updatedPerson.setMarriages(marriages);
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateChildren(Person updatedPerson, List<Person> children) {
        children = saveIfNotExist(children);
        if (updatedPerson.getChildren() != null && !updatedPerson.getChildren().isEmpty()) {
            Set<Long> newIds = children.stream().map(Person::getId).collect(Collectors.toSet());
            for (Person child : updatedPerson.getChildren()) {
                if (!newIds.contains(child.getId())) {
                    personRepository.deleteParentLinkByPersonIdAndParentId(updatedPerson.getId(), child.getId());
                }
            }
        }
        Set<Long> existIds = updatedPerson.getChildren().stream().map(Person::getId).collect(Collectors.toSet());
        updatedPerson.setChildren(children);
        for (Person child : updatedPerson.getChildren()) {
            if (!existIds.contains(child.getId())) {
                List<Person> parents = child.getParents();
                if (parents != null && !parents.isEmpty()) {
                    parents.add(updatedPerson);
                } else {
                    parents = List.of(updatedPerson);
                }
                child.setParents(parents);
            }
        }
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updatePartners(Person updatedPerson, List<Person> partners) {
        partners = saveIfNotExist(partners);
        if (updatedPerson.getPartners() != null && !updatedPerson.getPartners().isEmpty()) {
            Set<Long> newIds = partners.stream().map(Person::getId).collect(Collectors.toSet());
            for (Person partner : updatedPerson.getPartners()) {
                if (!newIds.contains(partner.getId())) {
                    personRepository.deletePartnerLinkByPersonIdAndPartnerId(updatedPerson.getId(), partner.getId());
                    personRepository.deletePartnerLinkByPersonIdAndPartnerId(partner.getId(), updatedPerson.getId());
                }
            }
        }
        Set<Long> existIds = updatedPerson.getPartners().stream().map(Person::getId).collect(Collectors.toSet());
        updatedPerson.setPartners(partners);
        for (Person partner : updatedPerson.getPartners()) {
            if (!existIds.contains(partner.getId())) {
                List<Person> partnersIn = partner.getPartners();
                if (partnersIn != null && !partnersIn.isEmpty()) {
                    partnersIn.add(updatedPerson);
                } else {
                    partnersIn = List.of(updatedPerson);
                }
                partner.setPartners(partnersIn);
            }
        }
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateParents(Person updatedPerson, List<Person> parents) {
        parents = saveIfNotExist(parents);
        if (updatedPerson.getParents() != null && !updatedPerson.getParents().isEmpty()) {
            Set<Long> newIds = parents.stream().map(Person::getId).collect(Collectors.toSet());
            for (Person parent : updatedPerson.getParents()) {
                if (!newIds.contains(parent.getId())) {
                    personRepository.deleteParentLinkByPersonIdAndParentId(parent.getId(), updatedPerson.getId());
                }
            }
        }
        Set<Long> existIds = updatedPerson.getParents().stream().map(Person::getId).collect(Collectors.toSet());
        updatedPerson.setParents(parents);
        for (Person parent : updatedPerson.getParents()) {
            if (!existIds.contains(parent.getId())) {
                List<Person> children = parent.getChildren();
                if (children != null && !children.isEmpty()) {
                    children.add(updatedPerson);
                } else {
                    children = List.of(updatedPerson);
                }
                parent.setChildren(children);
            }
        }
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateInfo(Person updatedPerson, Person person) {
        updatedPerson = updateChristening(updatedPerson, person.getChristening());
        updatedPerson = updateDeath(updatedPerson, person.getDeath());
        updatedPerson = updateRevisions(updatedPerson, person.getRevisions());
        updatedPerson = updateMarriages(updatedPerson, person.getMarriages());
        updatedPerson = updateChildren(updatedPerson, person.getChildren());
        updatedPerson = updatePartners(updatedPerson, person.getPartners());
        updatedPerson = updateParents(updatedPerson, person.getParents());
        return updatedPerson;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateLinksForSavePerson(Person person) {
        if (person.getChristening() != null) {
            christeningDAO.updatePersonIdById(person.getChristening().getId(), person.getId());
        }
        if (person.getDeath() != null) {
            deathDAO.updatePersonIdById(person.getDeath().getId(), person.getId());
        }
        if (person.getRevisions() != null) {
            for (FamilyRevision revision : person.getRevisions()) {
                familyRevisionDAO.updatePersonIdById(revision.getId(), person.getId());
            }
        }
        if (person.getChildren() != null) {
            person.getChildren().forEach(c -> {
                List<Person> childParents = c.getParents();
                if (childParents != null && !childParents.isEmpty()) {
                    childParents.add(person);
                } else {
                    childParents = List.of(person);
                }
                c.setParents(childParents);
            });
        }
        if (person.getPartners() != null) {
            person.getPartners().forEach(p -> {
                List<Person> partners = p.getPartners();
                if (partners != null && !partners.isEmpty()) {
                    partners.add(person);
                } else {
                    partners = List.of(person);
                }
                p.setPartners(partners);
            });
        }
        if (person.getParents() != null) {
            person.getParents().forEach(p -> {
                List<Person> parentChildren = p.getChildren();
                if (parentChildren != null && !parentChildren.isEmpty()) {
                    parentChildren.add(person);
                } else {
                    parentChildren = List.of(person);
                }
                p.setChildren(parentChildren);
            });
        }
        return person;
    }
}
