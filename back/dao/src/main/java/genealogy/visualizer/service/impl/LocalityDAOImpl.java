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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        List<Christening> christenings = locality.getChristenings();
        List<Death> deaths = locality.getDeaths();
        List<Marriage> marriagesWithHusbandLocality = locality.getMarriagesWithHusbandLocality();
        List<Marriage> marriagesWithWifeLocality = locality.getMarriagesWithWifeLocality();
        List<Person> personsWithBirthLocality = locality.getPersonsWithBirthLocality();
        List<Person> personsWithDeathLocality = locality.getPersonsWithDeathLocality();
        locality.setChristenings(Collections.emptyList());
        locality.setDeaths(Collections.emptyList());
        locality.setMarriagesWithHusbandLocality(Collections.emptyList());
        locality.setMarriagesWithWifeLocality(Collections.emptyList());
        locality.setPersonsWithDeathLocality(Collections.emptyList());
        locality.setPersonsWithBirthLocality(Collections.emptyList());

        Locality savedLocality = localityRepository.save(locality);

        savedLocality.setChristenings(christenings != null ?
                christeningHelper.saveEntitiesIfNotExist(christenings.stream().peek(c -> c.setLocality(savedLocality)).toList(),
                        christeningRepository, Christening::getId) :
                null);
        savedLocality.setDeaths(deaths != null ?
                deathHelper.saveEntitiesIfNotExist(deaths.stream().peek(d -> d.setLocality(savedLocality)).toList(),
                        deathRepository, Death::getId) :
                null);
        savedLocality.setMarriagesWithWifeLocality(marriagesWithWifeLocality != null ?
                marriageHelper.saveEntitiesIfNotExist(marriagesWithWifeLocality.stream().peek(m -> m.setWifeLocality(savedLocality)).toList(),
                        marriageRepository, Marriage::getId) :
                null);
        savedLocality.setMarriagesWithHusbandLocality(marriagesWithHusbandLocality != null ?
                marriageHelper.saveEntitiesIfNotExist(marriagesWithHusbandLocality.stream().peek(m -> m.setHusbandLocality(savedLocality)).toList(),
                        marriageRepository, Marriage::getId) :
                null);
        savedLocality.setPersonsWithBirthLocality(personsWithBirthLocality != null ?
                personHelper.saveEntitiesIfNotExist(personsWithBirthLocality.stream().peek(p -> p.setBirthLocality(savedLocality)).toList(),
                        personRepository, Person::getId) :
                null);
        savedLocality.setPersonsWithDeathLocality(personsWithDeathLocality != null ?
                personHelper.saveEntitiesIfNotExist(personsWithDeathLocality.stream().peek(p -> p.setDeathLocality(savedLocality)).toList(),
                        personRepository, Person::getId) :
                null);
        return savedLocality;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Locality update(Locality locality) {
        if (locality.getId() == null)
            throw new IllegalArgumentException("Cannot update locality without id");

        Locality updatedLocality = localityRepository.update(locality);
        updatedLocality.setAnotherNames(locality.getAnotherNames());

        updatedLocality.setChristenings(christeningHelper.updateEntities(
                updatedLocality.getId(), updatedLocality.getChristenings(), locality.getChristenings(),
                Christening::getId, christeningRepository, christeningRepository::updateLocalityIdById));
        updatedLocality.setDeaths(deathHelper.updateEntities(
                updatedLocality.getId(), updatedLocality.getDeaths(), locality.getDeaths(), Death::getId,
                deathRepository, deathRepository::updateLocalityIdById));
        updatedLocality.setMarriagesWithWifeLocality(marriageHelper.updateEntities(
                updatedLocality.getId(), updatedLocality.getMarriagesWithWifeLocality(), locality.getMarriagesWithWifeLocality(),
                Marriage::getId, marriageRepository, marriageRepository::updateWifeLocalityIdById));
        updatedLocality.setMarriagesWithHusbandLocality(marriageHelper.updateEntities(
                updatedLocality.getId(), updatedLocality.getMarriagesWithHusbandLocality(), locality.getMarriagesWithHusbandLocality(),
                Marriage::getId, marriageRepository, marriageRepository::updateHusbandLocalityIdById));
        updatedLocality.setPersonsWithDeathLocality(personHelper.updateEntities(
                updatedLocality.getId(), updatedLocality.getPersonsWithDeathLocality(), locality.getPersonsWithDeathLocality(),
                Person::getId, personRepository, personRepository::updateDeathLocalityIdById));
        updatedLocality.setPersonsWithBirthLocality(personHelper.updateEntities(
                updatedLocality.getId(), updatedLocality.getPersonsWithBirthLocality(), locality.getPersonsWithBirthLocality(),
                Person::getId, personRepository, personRepository::updateBirthLocalityIdById));

        return updatedLocality;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
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

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Locality saveOrFindIfExist(Locality locality) {
        if (locality == null || locality.getName() == null || locality.getName().isEmpty()) return null;
        if (locality.getId() == null) {
            return localityRepository.findLocality(locality.getName(), locality.getType(), locality.getAddress())
                    .or(() -> Optional.of(localityRepository.save(locality)))
                    .orElseThrow();
        }
        return localityRepository.findById(locality.getId()).orElseThrow();
    }
}
