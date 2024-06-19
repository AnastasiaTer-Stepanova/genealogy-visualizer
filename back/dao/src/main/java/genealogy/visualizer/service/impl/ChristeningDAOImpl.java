package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.GodParent;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.GodParentRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.ChristeningDAO;
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

public class ChristeningDAOImpl implements ChristeningDAO {

    private final ChristeningRepository christeningRepository;
    private final PersonRepository personRepository;
    private final LocalityRepository localityRepository;
    private final ArchiveDocumentRepository archiveDocumentRepository;
    private final GodParentRepository godParentRepository;
    private final EntityManager entityManager;

    public ChristeningDAOImpl(ChristeningRepository christeningRepository,
                              PersonRepository personRepository,
                              LocalityRepository localityRepository,
                              ArchiveDocumentRepository archiveDocumentRepository,
                              GodParentRepository godParentRepository,
                              EntityManager entityManager) {
        this.christeningRepository = christeningRepository;
        this.personRepository = personRepository;
        this.localityRepository = localityRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.godParentRepository = godParentRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Locality> localityHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<GodParent> godParentHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("Cannot delete christening without id");
        godParentRepository.deleteGodParentsByChristeningId(id);
        christeningRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Christening save(Christening christening) throws IllegalArgumentException, EmptyResultDataAccessException {
        if (christening.getId() != null)
            throw new IllegalArgumentException("Cannot save christening with id");
        christening = updateLinks(christening);
        Christening savedChristening = christeningRepository.save(christening);
        updateLinks(savedChristening, christening);
        return savedChristening;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Christening update(Christening christening) throws IllegalArgumentException, EmptyResultDataAccessException {
        Long id = christening.getId();
        if (id == null)
            throw new IllegalArgumentException("Cannot update christening without id");
        Christening existInfo = this.findFullInfoById(id);
        christening = updateLinks(christening);
        christeningRepository.update(christening).orElseThrow(() -> new EmptyResultDataAccessException("Updating christening failed", 1));
        updateLinks(existInfo, christening);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Christening findFullInfoById(Long id) throws EmptyResultDataAccessException {
        String errorMes = String.format("Christening not found by id: %d", id);
        christeningRepository.findWithLocalityAndPersonAndArchiveDocument(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        Christening result = christeningRepository.findWithGodParents(id).orElseThrow(() -> new EmptyResultDataAccessException(errorMes, 1));
        result.setGodParents(result.getGodParents().stream().distinct().toList());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Christening> filter(ChristeningFilterDTO filter) throws EmptyResultDataAccessException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Christening> cq = cb.createQuery(Christening.class);
        Root<Christening> root = cq.from(Christening.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(addArchiveDocumentIdFilter(cb, root, filter.getArchiveDocumentId()));
        if (filter.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        if (filter.getSex() != null) {
            predicates.add(cb.equal(cb.lower(root.get("sex")), filter.getSex().getName().toLowerCase()));
        }
        if (filter.getChristeningYear() != null) {
            Expression<Integer> yearExpression = cb.function("date_part", Integer.class, cb.literal("year"), root.get("christeningDate"));
            predicates.add(cb.equal(yearExpression, filter.getChristeningYear()));
        }
        if (!filter.getFindWithHavePerson()) {
            predicates.add(cb.isNull(root.get("person")));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        List<Christening> result = entityManager.createQuery(cq).getResultList();
        if (result == null || result.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format("Christenings not found filter: %s", filter), 1);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Christening updateLinks(Christening info) {
        info.setPerson(personHelper.saveEntityIfNotExist(info.getPerson(), Person::getId, personRepository));
        info.setLocality(localityHelper.saveEntityIfNotExist(info.getLocality(), Locality::getId, localityRepository));
        info.setArchiveDocument(archiveDocumentHelper.saveEntityIfNotExist(info.getArchiveDocument(), ArchiveDocument::getId, archiveDocumentRepository));
        return info;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(Christening existInfo, Christening newInfo) {
        newInfo.getGodParents()
                .forEach(gp -> {
                    gp.setLocality(localityHelper.saveEntityIfNotExist(gp.getLocality(), Locality::getId, localityRepository));
                    gp.setChristening(existInfo);
                });

        godParentHelper.updateEntities(
                existInfo.getId(),
                existInfo.getGodParents(),
                newInfo.getGodParents(),
                GodParent::getId,
                godParentRepository,
                null,
                godParentRepository::deleteById);
    }
}
