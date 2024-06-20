package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.PersonFilterDTO;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.PersonDAO;
import genealogy.visualizer.service.helper.RepositoryEasyModelHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static genealogy.visualizer.service.helper.FilterHelper.addFullNameFilter;
import static genealogy.visualizer.service.helper.FilterHelper.getGraphsResult;

public class PersonDAOImpl implements PersonDAO {

    private final PersonRepository personRepository;
    private final LocalityRepository localityRepository;
    private final ChristeningRepository christeningRepository;
    private final DeathRepository deathRepository;
    private final MarriageRepository marriageRepository;
    private final FamilyRevisionRepository familyRevisionRepository;
    private final EntityManager entityManager;

    public PersonDAOImpl(PersonRepository personRepository,
                         LocalityRepository localityRepository,
                         ChristeningRepository christeningRepository,
                         DeathRepository deathRepository,
                         MarriageRepository marriageRepository,
                         FamilyRevisionRepository familyRevisionRepository,
                         EntityManager entityManager) {
        this.personRepository = personRepository;
        this.localityRepository = localityRepository;
        this.christeningRepository = christeningRepository;
        this.deathRepository = deathRepository;
        this.marriageRepository = marriageRepository;
        this.familyRevisionRepository = familyRevisionRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<FamilyRevision> familyRevisionHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Christening> christeningHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Marriage> marriageHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Death> deathHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Locality> localityHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("Cannot delete person without id");
        personRepository.deleteParentLinkById(id);
        personRepository.deletePartnerLinkById(id);
        personRepository.deleteMarriageLinkById(id);
        christeningRepository.updatePersonIdByPersonId(id, null);
        deathRepository.updatePersonIdByPersonId(id, null);
        familyRevisionRepository.updatePersonIdByPersonId(id, null);
        personRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Person save(Person person) throws IllegalArgumentException, EmptyResultDataAccessException {
        if (person.getId() != null)
            throw new IllegalArgumentException("Cannot save person with id");
        person = updateLinks(person);

        Person personForSave = person.clone();
        personForSave.setChristening(null);
        personForSave.setDeath(null);
        personForSave.setRevisions(Collections.emptyList());
        personForSave.setMarriages(Collections.emptyList());
        personForSave.setParents(Collections.emptyList());
        personForSave.setPartners(Collections.emptyList());
        personForSave.setChildren(Collections.emptyList());

        Person savedPerson = personRepository.save(personForSave);
        updateLinks(savedPerson, person);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(savedPerson.getId());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Person update(Person person) throws IllegalArgumentException, EmptyResultDataAccessException {
        Long id = person.getId();
        if (id == null)
            throw new IllegalArgumentException("Cannot update person without id");
        Person existInfo = this.findFullInfoById(id);
        person = updateLinks(person);
        personRepository.update(person).orElseThrow(() -> new EmptyResultDataAccessException("Updating person failed", 1));
        updateLinks(existInfo, person);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Person findFullInfoById(Long id) throws EmptyResultDataAccessException {
        String errorMes = String.format("Person not found by id: %d", id);
        personRepository.findPersonWithBirthLocalityAndDeathLocality(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        personRepository.findPersonWithPartners(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        personRepository.findPersonWithParents(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        personRepository.findPersonWithChildren(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        Person result = personRepository.findPersonWithRevisions(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        Map<Long, FamilyRevision> familyRevisions = new HashMap<>();
        result.getRevisions().forEach(familyRevision -> {
            FamilyRevision fr = familyRevisions.get(familyRevision.getId());
            if (fr == null) {
                familyRevisions.put(familyRevision.getId(), familyRevision);
                return;
            }
            if (fr.getPartner() != null && fr.getPartner().getAnotherNames() != null
                    && familyRevision.getPartner() != null && familyRevision.getPartner().getAnotherNames() != null) {
                Set<String> anotherNames = fr.getPartner().getAnotherNames();
                anotherNames.addAll(familyRevision.getPartner().getAnotherNames());
                fr.getPartner().setAnotherNames(anotherNames);
            }
            if (fr.getAnotherNames() != null && familyRevision.getAnotherNames() != null) {
                Set<String> anotherNames = fr.getAnotherNames();
                anotherNames.addAll(familyRevision.getAnotherNames());
                fr.setAnotherNames(anotherNames);
            }
            familyRevisions.put(familyRevision.getId(), familyRevision);
        });
        result.setRevisions(familyRevisions.values().stream().toList());
        return personRepository.findPersonWithMarriages(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> filter(PersonFilterDTO filter) throws EmptyResultDataAccessException {
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
        List<Person> result = getGraphsResult(filter.getGraphs(), cq, entityManager);
        if (result == null || result.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format("Persons not found filter: %s", filter), 1);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Person updateLinks(Person info) {
        info.setDeathLocality(localityHelper.saveEntityIfNotExist(info.getDeathLocality(), Locality::getId, localityRepository));
        info.setBirthLocality(localityHelper.saveEntityIfNotExist(info.getBirthLocality(), Locality::getId, localityRepository));
        return info;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(Person existInfo, Person newInfo) {
        if (newInfo.getChristening() != null) {
            Christening christening = newInfo.getChristening();
            christening.setPerson(existInfo);
            newInfo.setChristening(christening);
        }
        christeningHelper.updateEntity(
                existInfo.getChristening(),
                newInfo.getChristening(),
                Christening::getId,
                christeningRepository,
                christeningRepository::updatePersonIdById);

        if (newInfo.getDeath() != null) {
            Death death = newInfo.getDeath();
            death.setPerson(existInfo);
            newInfo.setDeath(death);
        }
        deathHelper.updateEntity(
                existInfo.getDeath(),
                newInfo.getDeath(),
                Death::getId,
                deathRepository,
                deathRepository::updatePersonIdById);

        if (newInfo.getRevisions() != null) {
            newInfo.setRevisions(newInfo.getRevisions().stream()
                    .peek(entity -> entity.setPerson(existInfo)).toList());
        }
        familyRevisionHelper.updateEntities(
                existInfo.getId(),
                existInfo.getRevisions(),
                newInfo.getRevisions(),
                FamilyRevision::getId,
                familyRevisionRepository,
                familyRevisionRepository::updatePersonIdById);

        marriageHelper.updateEntitiesWithLinkTable(
                existInfo.getId(),
                existInfo.getMarriages(),
                newInfo.getMarriages(),
                Marriage::getId,
                marriageRepository,
                marriageRepository::deletePersonMarriageLinkByMarriageIdPersonId,
                marriageRepository::insertMarriagePersonLink);

        personHelper.updateEntitiesWithLinkTable(
                        existInfo.getId(),
                        existInfo.getPartners(),
                        newInfo.getPartners(),
                        Person::getId,
                        personRepository,
                        personRepository::deletePartnerLinkByPersonIdAndPartnerId,
                        personRepository::insertPartnerPersonLink)
                .ifPresent(savedPartners -> savedPartners
                        .forEach(partner -> personRepository.insertPartnerPersonLink(existInfo.getId(), partner.getId())));

        personHelper.updateEntitiesWithLinkTable(
                existInfo.getId(),
                existInfo.getParents(),
                newInfo.getParents(),
                Person::getId,
                personRepository,
                personRepository::deleteParentLinkByPersonIdAndParentId,
                personRepository::insertParentPersonLink);

        personHelper.updateEntitiesWithLinkTable(
                existInfo.getId(),
                existInfo.getChildren(),
                newInfo.getChildren(),
                Person::getId,
                personRepository,
                personRepository::deleteParentLinkByPersonIdAndChildId,
                personRepository::insertChildPersonLink);

    }
}
