package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.MarriageFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.entity.Witness;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.repository.WitnessRepository;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.helper.RepositoryEasyModelHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static genealogy.visualizer.service.helper.FilterHelper.addArchiveDocumentIdFilter;
import static genealogy.visualizer.service.helper.FilterHelper.addFullNameFilter;

public class MarriageDAOImpl implements MarriageDAO {

    private final MarriageRepository marriageRepository;
    private final PersonRepository personRepository;
    private final LocalityRepository localityRepository;
    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final WitnessRepository witnessRepository;
    private final EntityManager entityManager;

    public MarriageDAOImpl(MarriageRepository marriageRepository,
                           PersonRepository personRepository,
                           LocalityRepository localityRepository,
                           ArchiveDocumentRepository archiveDocumentRepository,
                           WitnessRepository witnessRepository,
                           EntityManager entityManager) {
        this.marriageRepository = marriageRepository;
        this.personRepository = personRepository;
        this.localityRepository = localityRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.witnessRepository = witnessRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Locality> localityHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Witness> witnessHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("Cannot delete marriage without id");
        witnessRepository.deleteWitnessesByMarriageId(id);
        marriageRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Marriage save(Marriage marriage) throws IllegalArgumentException, EmptyResultDataAccessException {
        if (marriage.getId() != null)
            throw new IllegalArgumentException("Cannot save marriage with id");
        marriage = updateLinks(marriage);
        Marriage marriageForSave = marriage.clone();
        marriageForSave.setPersons(Collections.emptyList());
        Marriage savedMarriage = marriageRepository.save(marriageForSave);
        updateLinks(savedMarriage, marriage);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(savedMarriage.getId());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Marriage update(Marriage marriage) throws IllegalArgumentException, EmptyResultDataAccessException {
        Long id = marriage.getId();
        if (id == null)
            throw new IllegalArgumentException("Cannot update marriage without id");
        Marriage existInfo = this.findFullInfoById(id);
        marriage = updateLinks(marriage);
        marriageRepository.update(marriage).orElseThrow(() -> new EmptyResultDataAccessException("Updating marriage failed", 1));
        updateLinks(existInfo, marriage);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Marriage findFullInfoById(Long id) throws EmptyResultDataAccessException {
        String errorMes = String.format("Marriage not found by id: %d", id);
        marriageRepository.findWithWifeLocality(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        marriageRepository.findWithHusbandLocality(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        marriageRepository.findWithPersons(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        marriageRepository.findWithArchiveDocument(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        Marriage result = marriageRepository.findWithWitnesses(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        result.setWitnesses(result.getWitnesses().stream().distinct().toList());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marriage> filter(MarriageFilterDTO filter) throws EmptyResultDataAccessException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Marriage> cq = cb.createQuery(Marriage.class);
        Root<Marriage> root = cq.from(Marriage.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(addArchiveDocumentIdFilter(cb, root, filter.getArchiveDocumentId()));
        predicates.addAll(addFullNameFilter(cb, root, filter.getHusbandFullName(), "husband"));
        predicates.addAll(addFullNameFilter(cb, root, filter.getWifeFullName(), "wife"));
        if (filter.getMarriageYear() != null) {
            Expression<Integer> yearExpression = cb.function("date_part", Integer.class, cb.literal("year"), root.get("date"));
            predicates.add(cb.equal(yearExpression, filter.getMarriageYear()));
        }
        if (!filter.getFindWithHavePerson()) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Person> personRoot = subquery.from(Person.class);
            subquery.select(personRoot.get("marriages").get("id"));
            predicates.add(cb.not(cb.in(root.get("id")).value(subquery)));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        List<Marriage> result = entityManager.createQuery(cq).getResultList();
        if (result == null || result.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format("Marriages not found filter: %s", filter), 1);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Marriage updateLinks(Marriage info) {
        info.setArchiveDocument(archiveDocumentHelper.saveEntityIfNotExist(info.getArchiveDocument(), ArchiveDocument::getId, archiveDocumentRepository));
        info.setHusbandLocality(localityHelper.saveEntityIfNotExist(info.getHusbandLocality(), Locality::getId, localityRepository));
        info.setWifeLocality(localityHelper.saveEntityIfNotExist(info.getWifeLocality(), Locality::getId, localityRepository));
        return info;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(Marriage existInfo, Marriage newInfo) {
        newInfo.getWitnesses()
                .forEach(w -> {
                    w.setLocality(localityHelper.saveEntityIfNotExist(w.getLocality(), Locality::getId, localityRepository));
                    w.setMarriage(existInfo);
                });

        witnessHelper.updateEntities(
                existInfo.getId(),
                existInfo.getWitnesses(),
                newInfo.getWitnesses(),
                Witness::getId,
                witnessRepository,
                null,
                witnessRepository::deleteById);

        personHelper.updateEntitiesWithLinkTable(
                existInfo.getId(),
                existInfo.getPersons(),
                newInfo.getPersons(),
                Person::getId,
                personRepository,
                marriageRepository::deletePersonMarriageLinkByPersonIdAndMarriageId,
                marriageRepository::insertPersonMarriageLink);
    }
}
