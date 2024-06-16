package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.entity.model.GodParent;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.helper.RepositoryEasyModelHelper;
import genealogy.visualizer.service.helper.RepositoryEmbeddableModelHelper;
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
    private final EntityManager entityManager;

    public ChristeningDAOImpl(ChristeningRepository christeningRepository,
                              PersonRepository personRepository,
                              LocalityRepository localityRepository,
                              ArchiveDocumentRepository archiveDocumentRepository,
                              EntityManager entityManager) {
        this.christeningRepository = christeningRepository;
        this.personRepository = personRepository;
        this.localityRepository = localityRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Locality> localityHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEasyModelHelper<Person> personHelper = new RepositoryEasyModelHelper<>();
    private static final RepositoryEmbeddableModelHelper<GodParent> godParentHelper = new RepositoryEmbeddableModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) {
        christeningRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Christening save(Christening christening) {
        if (christening.getId() != null)
            throw new IllegalArgumentException("Cannot save christening with id");
        christening = updateLinks(christening);
        return christeningRepository.save(christening);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Christening update(Christening christening) {
        if (christening.getId() == null)
            throw new IllegalArgumentException("Cannot update christening without id");
        christening = updateLinks(christening);
        Christening updatedChristening = christeningRepository.update(christening);
        if (updatedChristening == null)
            throw new EmptyResultDataAccessException("Updating christening failed", 1);
        entityManager.clear();
        return this.findFullInfoById(updatedChristening.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Christening findFullInfoById(Long id) {
        return christeningRepository.findFullInfoById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(String.format("Christening not found by id: %d", id), 1));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Christening> filter(ChristeningFilterDTO filter) {
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
        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected Christening updateLinks(Christening info) {
        info.setGodParents(godParentHelper.updateEmbeddable(
                info.getId(),
                info.getGodParents(),
                (GodParent gp) -> gp.setLocality(localityRepository.save(gp.getLocality())),
                christeningRepository::deleteGodParentsById,
                christeningRepository::insertGodParent));
        info.setPerson(info.getPerson() != null ?
                personHelper.saveEntityIfNotExist(info.getPerson(), info.getPerson().getId(), personRepository) :
                null);
        info.setLocality(info.getLocality() != null ?
                localityHelper.saveEntityIfNotExist(info.getLocality(), info.getLocality().getId(), localityRepository) :
                null);
        info.setArchiveDocument(info.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(info.getArchiveDocument(), info.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);
        return info;
    }
}
