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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static genealogy.visualizer.service.helper.FilterHelper.addArchiveDocumentIdFilter;
import static genealogy.visualizer.service.helper.FilterHelper.addFullNameFilter;

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
    public void delete(Long id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("Cannot delete death without id");
        deathRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Death save(Death death) throws IllegalArgumentException, EmptyResultDataAccessException {
        if (death.getId() != null)
            throw new IllegalArgumentException("Cannot save death with id");
        return deathRepository.save(updateLinks(death));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Death update(Death death) throws IllegalArgumentException, EmptyResultDataAccessException {
        Long id = death.getId();
        if (id == null)
            throw new IllegalArgumentException("Cannot update death without id");
        deathRepository.update(updateLinks(death)).orElseThrow(() -> new EmptyResultDataAccessException("Updating christening failed", 1));
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Death findFullInfoById(Long id) throws EmptyResultDataAccessException {
        return deathRepository.findFullInfoById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(String.format("Death not found by id: %d", id), 1));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Death> filter(DeathFilterDTO filter) throws EmptyResultDataAccessException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Death> cq = cb.createQuery(Death.class);
        Root<Death> root = cq.from(Death.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(addArchiveDocumentIdFilter(cb, root, filter.getArchiveDocumentId()));
        predicates.addAll(addFullNameFilter(cb, root, filter.getFullName(), "fullName"));
        if (filter.getDeathYear() != null) {
            Expression<Integer> yearExpression = cb.function("date_part", Integer.class, cb.literal("year"), root.get("date"));
            predicates.add(cb.equal(yearExpression, filter.getDeathYear()));
        }
        if (!filter.getFindWithHavePerson()) {
            predicates.add(cb.isNull(root.get("person")));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        List<Death> result = entityManager.createQuery(cq).getResultList();
        if (result == null || result.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format("Deaths not found filter: %s", filter), 1);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Death updateLinks(Death info) {
        info.setArchiveDocument(archiveDocumentHelper.saveEntityIfNotExist(info.getArchiveDocument(), ArchiveDocument::getId, archiveDocumentRepository));
        info.setLocality(localityHelper.saveEntityIfNotExist(info.getLocality(), Locality::getId, localityRepository));
        info.setPerson(personHelper.saveEntityIfNotExist(info.getPerson(), Person::getId, personRepository));
        return info;
    }
}
