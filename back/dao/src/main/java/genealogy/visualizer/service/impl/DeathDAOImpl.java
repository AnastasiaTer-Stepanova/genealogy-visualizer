package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.DeathFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.DeathDAO;
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
import java.util.List;

public class DeathDAOImpl implements DeathDAO {

    private final DeathRepository deathRepository;
    private final PersonRepository personRepository;
    private final LocalityRepository localityRepository;
    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final EntityManager entityManager;

    public DeathDAOImpl(DeathRepository deathRepository,
                        PersonRepository personRepository,
                        LocalityRepository localityRepository,
                        ArchiveDocumentRepository archiveDocumentRepository,
                        EntityManager entityManager) {
        this.deathRepository = deathRepository;
        this.personRepository = personRepository;
        this.localityRepository = localityRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Locality> localityHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();

    @Override
    public void delete(Long id) {
        deathRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Death save(Death death) {
        if (death.getId() != null)
            throw new IllegalArgumentException("Cannot save death with id");

        death.setArchiveDocument(death.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(death.getArchiveDocument(), death.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);
        death.setLocality(death.getLocality() != null ?
                localityHelper.saveEntityIfNotExist(death.getLocality(), death.getLocality().getId(), localityRepository) :
                null);
        death.setPerson(death.getPerson() != null ?
                personHelper.saveEntityIfNotExist(death.getPerson(), death.getPerson().getId(), personRepository) :
                null);
        return deathRepository.save(death);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Death update(Death death) {
        if (death.getId() == null)
            throw new IllegalArgumentException("Cannot update death without id");

        death.setPerson(death.getPerson() != null ?
                personHelper.saveEntityIfNotExist(death.getPerson(), death.getPerson().getId(), personRepository) :
                null);
        death.setLocality(death.getLocality() != null ?
                localityHelper.saveEntityIfNotExist(death.getLocality(), death.getLocality().getId(), localityRepository) :
                null);
        death.setArchiveDocument(death.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(death.getArchiveDocument(), death.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);

        return deathRepository.update(death);
    }

    @Override
    public Death findFullInfoById(Long id) {
        return deathRepository.findFullInfoById(id).orElse(null);
    }

    @Override
    public List<Death> filter(DeathFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Death> cq = cb.createQuery(Death.class);
        Root<Death> root = cq.from(Death.class);
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("fullName").get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        if (filter.getDeathYear() != null) {
            Expression<Integer> yearExpression = cb.function("date_part", Integer.class, cb.literal("year"), root.get("date"));
            predicates.add(cb.equal(yearExpression, filter.getDeathYear()));
        }
        if (filter.getArchiveDocumentId() != null) {
            Join<Death, ArchiveDocument> join = root.join("archiveDocument", JoinType.LEFT);
            predicates.add(cb.equal(join.get("id"), filter.getArchiveDocumentId()));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public void updatePersonIdByPersonId(Long personId, Long newPersonId) {
        deathRepository.updatePersonIdByPersonId(personId, newPersonId);
    }

    @Override
    public void updatePersonIdById(Long id, Long newPersonId) {
        deathRepository.updatePersonIdById(id, newPersonId);
    }
}
