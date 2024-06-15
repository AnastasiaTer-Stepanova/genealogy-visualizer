package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.LocalityFilterDTO;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.LocalityDAO;
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
import java.util.List;

public class LocalityDAOImpl implements LocalityDAO {

    private final LocalityRepository localityRepository;
    private final ChristeningRepository christeningRepository;
    private final DeathRepository deathRepository;
    private final PersonRepository personRepository;
    private final MarriageRepository marriageRepository;
    private final EntityManager entityManager;

    public LocalityDAOImpl(LocalityRepository localityRepository,
                           ChristeningRepository christeningRepository,
                           DeathRepository deathRepository,
                           PersonRepository personRepository,
                           MarriageRepository marriageRepository,
                           EntityManager entityManager) {
        this.localityRepository = localityRepository;
        this.christeningRepository = christeningRepository;
        this.deathRepository = deathRepository;
        this.personRepository = personRepository;
        this.marriageRepository = marriageRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<Christening> christeningHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Marriage> marriageHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Death> deathHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        christeningRepository.updateLocalityId(id, null);
        christeningRepository.updateGodParentLocalityId(id, null);
        deathRepository.updateLocalityId(id, null);
        personRepository.updateBirthLocalityId(id, null);
        personRepository.updateDeathLocalityId(id, null);
        marriageRepository.updateWifeLocalityId(id, null);
        marriageRepository.updateHusbandLocalityId(id, null);
        marriageRepository.updateWitnessLocalityId(id, null);
        localityRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Locality save(Locality locality) {
        if (locality.getId() != null)
            throw new IllegalArgumentException("Cannot save locality with id");
        Locality localityForSave = locality.clone();
        localityForSave.setChristenings(Collections.emptyList());
        localityForSave.setDeaths(Collections.emptyList());
        localityForSave.setMarriagesWithHusbandLocality(Collections.emptyList());
        localityForSave.setMarriagesWithWifeLocality(Collections.emptyList());
        localityForSave.setPersonsWithDeathLocality(Collections.emptyList());
        localityForSave.setPersonsWithBirthLocality(Collections.emptyList());
        Locality savedLocality = localityRepository.save(localityForSave);
        updateLinks(savedLocality, locality);
        entityManager.clear();
        return this.findFullInfoById(savedLocality.getId());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Locality update(Locality locality) {
        if (locality.getId() == null)
            throw new IllegalArgumentException("Cannot update locality without id");
        Locality updatedLocality = localityRepository.update(locality);
        if (updatedLocality == null)
            throw new EmptyResultDataAccessException("Updating locality failed", 1);
        localityRepository.deleteAnotherNamesById(updatedLocality.getId());
        if (locality.getAnotherNames() != null) {
            locality.getAnotherNames().forEach(an -> localityRepository.insertAnotherName(updatedLocality.getId(), an));
        }
        updateLinks(updatedLocality, locality);
        entityManager.clear();
        return this.findFullInfoById(updatedLocality.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Locality findFullInfoById(Long id) {
        Locality locality = localityRepository.findFullInfoById(id).orElse(null);
        if (locality == null) return null;
        localityRepository.findFullInfoByIdWithChristening(id).orElseThrow();
        localityRepository.findFullInfoByIdWithDeath(id).orElseThrow();
        localityRepository.findFullInfoByIdWithMarriageWifeLocality(id).orElseThrow();
        localityRepository.findFullInfoByIdWithMarriageHusbandLocality(id).orElseThrow();
        localityRepository.findFullInfoByIdWithPersonBirthLocality(id).orElseThrow();
        return localityRepository.findFullInfoByIdWithPersonDeathLocality(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Locality> filter(LocalityFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Locality> cq = cb.createQuery(Locality.class);
        Root<Locality> root = cq.from(Locality.class);
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        if (filter.getAddress() != null) {
            predicates.add(cb.like(cb.lower(root.get("address")), "%" + filter.getAddress().toLowerCase() + "%"));
        }
        if (filter.getType() != null) {
            predicates.add(cb.equal(cb.lower(root.get("type")), filter.getType().getName().toLowerCase()));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(Locality existInfo, Locality newInfo) {

        christeningHelper.updateEntities(
                existInfo.getId(),
                existInfo.getChristenings(),
                newInfo.getChristenings(),
                Christening::getId,
                christeningRepository,
                christeningRepository::updateLocalityIdById);

        deathHelper.updateEntities(
                existInfo.getId(),
                existInfo.getDeaths(),
                newInfo.getDeaths(),
                Death::getId,
                deathRepository,
                deathRepository::updateLocalityIdById);

        marriageHelper.updateEntities(
                existInfo.getId(),
                existInfo.getMarriagesWithWifeLocality(),
                newInfo.getMarriagesWithWifeLocality(),
                Marriage::getId,
                marriageRepository,
                marriageRepository::updateWifeLocalityIdById);

        marriageHelper.updateEntities(
                existInfo.getId(),
                existInfo.getMarriagesWithHusbandLocality(),
                newInfo.getMarriagesWithHusbandLocality(),
                Marriage::getId,
                marriageRepository,
                marriageRepository::updateHusbandLocalityIdById);

        personHelper.updateEntities(
                existInfo.getId(),
                existInfo.getPersonsWithDeathLocality(),
                newInfo.getPersonsWithDeathLocality(),
                Person::getId,
                personRepository,
                personRepository::updateDeathLocalityIdById);

        personHelper.updateEntities(
                existInfo.getId(),
                existInfo.getPersonsWithBirthLocality(),
                newInfo.getPersonsWithBirthLocality(),
                Person::getId,
                personRepository,
                personRepository::updateBirthLocalityIdById);

    }
}
