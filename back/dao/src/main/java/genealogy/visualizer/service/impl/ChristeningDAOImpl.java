package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.ChristeningDAO;
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

        christening.setArchiveDocument(christening.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(christening.getArchiveDocument(), christening.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);
        christening.setLocality(christening.getLocality() != null ?
                localityHelper.saveEntityIfNotExist(christening.getLocality(), christening.getLocality().getId(), localityRepository) :
                null);
        christening.setPerson(christening.getPerson() != null ?
                personHelper.saveEntityIfNotExist(christening.getPerson(), christening.getPerson().getId(), personRepository) :
                null);
        if (christening.getGodParents() != null && !christening.getGodParents().isEmpty()) {
            christening.getGodParents().forEach(godParent -> {
                if (godParent.getLocality() != null) {
                    godParent.setLocality(godParent.getLocality().getId() != null ? godParent.getLocality() : localityRepository.save(godParent.getLocality()));
                }
            });
        }
        return christeningRepository.save(christening);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Christening update(Christening christening) {
        if (christening.getId() == null)
            throw new IllegalArgumentException("Cannot update christening without id");

        christening.setPerson(christening.getPerson() != null ?
                personHelper.saveEntityIfNotExist(christening.getPerson(), christening.getPerson().getId(), personRepository) :
                null);
        christening.setLocality(christening.getLocality() != null ?
                localityHelper.saveEntityIfNotExist(christening.getLocality(), christening.getLocality().getId(), localityRepository) :
                null);
        christening.setArchiveDocument(christening.getArchiveDocument() != null ?
                archiveDocumentHelper.saveEntityIfNotExist(christening.getArchiveDocument(), christening.getArchiveDocument().getId(), archiveDocumentRepository) :
                null);

        Christening updatedChristening = christeningRepository.update(christening);

        if (christening.getGodParents() != null && !christening.getGodParents().isEmpty()) {
            christening.getGodParents().forEach(godParent -> {
                if (godParent.getLocality() != null) {
                    godParent.setLocality(godParent.getLocality().getId() != null ? godParent.getLocality() : localityRepository.save(godParent.getLocality()));
                }
            });
        }
        updatedChristening.setGodParents(christening.getGodParents());
        return updatedChristening;
    }

    @Override
    public Christening findFullInfoById(Long id) {
        return christeningRepository.findFullInfoById(id).orElse(null);
    }

    @Override
    public List<Christening> filter(ChristeningFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Christening> cq = cb.createQuery(Christening.class);
        Root<Christening> root = cq.from(Christening.class);
        List<Predicate> predicates = new ArrayList<>();
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
        if (filter.getArchiveDocumentId() != null) {
            Join<Christening, ArchiveDocument> join = root.join("archiveDocument", JoinType.LEFT);
            predicates.add(cb.equal(join.get("id"), filter.getArchiveDocumentId()));
        }
        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public void updatePersonIdByPersonId(Long personId, Long newPersonId) {
        christeningRepository.updatePersonIdByPersonId(personId, newPersonId);
    }

    @Override
    public void updatePersonIdById(Long id, Long newPersonId) {
        christeningRepository.updatePersonIdById(id, newPersonId);
    }
}
