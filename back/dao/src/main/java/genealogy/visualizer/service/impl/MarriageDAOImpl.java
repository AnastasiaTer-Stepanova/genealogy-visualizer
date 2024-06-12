package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.MarriageFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.helper.RepositoryEasyModelHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarriageDAOImpl implements MarriageDAO {

    private final MarriageRepository marriageRepository;
    private final PersonRepository personRepository;
    private final LocalityRepository localityRepository;
    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final EntityManager entityManager;

    public MarriageDAOImpl(MarriageRepository marriageRepository,
                           PersonRepository personRepository,
                           LocalityRepository localityRepository,
                           ArchiveDocumentRepository archiveDocumentRepository,
                           EntityManager entityManager) {
        this.marriageRepository = marriageRepository;
        this.personRepository = personRepository;
        this.localityRepository = localityRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Locality> localityHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        marriageRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Marriage save(Marriage marriage) {
        if (marriage.getId() != null)
            throw new IllegalArgumentException("Cannot save marriage with id");

        List<Person> persons = marriage.getPersons();
        marriage.setPersons(Collections.emptyList());

        marriage.setArchiveDocument(marriage.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(marriage.getArchiveDocument(), marriage.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);
        marriage.setHusbandLocality(marriage.getHusbandLocality() != null ?
                localityHelper.saveEntityIfNotExist(marriage.getHusbandLocality(), marriage.getHusbandLocality().getId(), localityRepository) :
                null);
        marriage.setWifeLocality(marriage.getWifeLocality() != null ?
                localityHelper.saveEntityIfNotExist(marriage.getWifeLocality(), marriage.getWifeLocality().getId(), localityRepository) :
                null);
        if (marriage.getWitnesses() != null && !marriage.getWitnesses().isEmpty()) {
            marriage.getWitnesses().forEach(witness -> {
                if (witness.getLocality() != null) {
                    witness.setLocality(witness.getLocality().getId() != null ? witness.getLocality() : localityRepository.save(witness.getLocality()));
                }
            });
        }

        Marriage savedMarriage = marriageRepository.save(marriage);

        savedMarriage.setPersons(persons != null ?
                personHelper.saveEntitiesIfNotExist(persons, personRepository, Person::getId) :
                null);

        return savedMarriage;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Marriage update(Marriage marriage) {
        if (marriage.getId() == null)
            throw new IllegalArgumentException("Cannot update marriage without id");

        marriage.setArchiveDocument(marriage.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(marriage.getArchiveDocument(), marriage.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);
        marriage.setHusbandLocality(marriage.getHusbandLocality() != null ?
                localityHelper.saveEntityIfNotExist(marriage.getHusbandLocality(), marriage.getHusbandLocality().getId(), localityRepository) :
                null);
        marriage.setWifeLocality(marriage.getWifeLocality() != null ?
                localityHelper.saveEntityIfNotExist(marriage.getWifeLocality(), marriage.getWifeLocality().getId(), localityRepository) :
                null);

        Marriage updatedMarriage = marriageRepository.update(marriage);

        if (marriage.getWitnesses() != null && !marriage.getWitnesses().isEmpty()) {
            marriage.getWitnesses().forEach(witness -> {
                if (witness.getLocality() != null) {
                    witness.setLocality(witness.getLocality().getId() != null ? witness.getLocality() : localityRepository.save(witness.getLocality()));
                }
            });
        }
        updatedMarriage.setWitnesses(marriage.getWitnesses());

        updatedMarriage.setPersons(personHelper.updateEntitiesWithLinkTable(
                updatedMarriage.getId(),
                updatedMarriage.getPersons(),
                marriage.getPersons(),
                Person::getId,
                personRepository, marriageRepository::deletePersonMarriageLinkByPersonIdAndMarriageId,
                null));

        return updatedMarriage;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Marriage findFullInfoById(Long id) {
        Marriage marriage = marriageRepository.findFullInfoById(id).orElse(null);
        if (marriage == null) return null;
        return marriageRepository.findFullInfoWithPersons(id).orElseThrow();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<Marriage> filter(MarriageFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Marriage> cq = cb.createQuery(Marriage.class);
        Root<Marriage> root = cq.from(Marriage.class);
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getHusbandName() != null) {
            predicates.add(cb.like(cb.lower(root.get("husband").get("name")), "%" + filter.getHusbandName().toLowerCase() + "%"));
        }
        if (filter.getWifeName() != null) {
            predicates.add(cb.like(cb.lower(root.get("wife").get("name")), "%" + filter.getWifeName().toLowerCase() + "%"));
        }
        if (filter.getMarriageYear() != null) {
            Expression<Integer> yearExpression = cb.function("date_part", Integer.class, cb.literal("year"), root.get("date"));
            predicates.add(cb.equal(yearExpression, filter.getMarriageYear()));
        }
        if (filter.getArchiveDocumentId() != null) {
            Join<Marriage, ArchiveDocument> join = root.join("archiveDocument", JoinType.LEFT);
            predicates.add(cb.equal(join.get("id"), filter.getArchiveDocumentId()));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }
}
