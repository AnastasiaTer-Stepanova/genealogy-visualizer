package genealogy.visualizer.service.impl;

import genealogy.visualizer.dto.ArchiveFilterDTO;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.service.ArchiveDAO;
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

public class ArchiveDAOImpl implements ArchiveDAO {

    private final ArchiveRepository archiveRepository;

    private final ArchiveDocumentRepository archiveDocumentRepository;

    private final EntityManager entityManager;

    public ArchiveDAOImpl(ArchiveRepository archiveRepository, ArchiveDocumentRepository archiveDocumentRepository, EntityManager entityManager) {
        this.archiveRepository = archiveRepository;
        this.archiveDocumentRepository = archiveDocumentRepository;
        this.entityManager = entityManager;
    }

    private static final RepositoryEasyModelHelper<ArchiveDocument> archiveDocumentHelper = new RepositoryEasyModelHelper<>();

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long id) throws IllegalArgumentException {
        if (id == null)
            throw new IllegalArgumentException("Cannot delete archive without id");
        archiveDocumentRepository.updateArchiveId(id, null);
        archiveRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive save(Archive archive) throws IllegalArgumentException, EmptyResultDataAccessException {
        if (archive.getId() != null)
            throw new IllegalArgumentException("Cannot save archive with id");
        Archive archiveForSave = archive.clone();
        archiveForSave.setArchiveDocuments(Collections.emptyList());
        Archive savedArchive = archiveRepository.save(archiveForSave);
        updateLinks(savedArchive, archive);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(savedArchive.getId());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive update(Archive archive) throws IllegalArgumentException, EmptyResultDataAccessException {
        Long id = archive.getId();
        if (id == null)
            throw new IllegalArgumentException("Cannot update archive without id");
        Archive existInfo = this.findFullInfoById(id);
        archiveRepository.update(archive).orElseThrow(() -> new EmptyResultDataAccessException("Updating archive failed", 1));
        updateLinks(existInfo, archive);
        entityManager.flush();
        entityManager.clear();
        return this.findFullInfoById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Archive findFullInfoById(Long id) throws EmptyResultDataAccessException {
        return archiveRepository.findFullInfoById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(String.format("Archive not found by id: %d", id), 1));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Archive> filter(ArchiveFilterDTO filter) throws EmptyResultDataAccessException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Archive> cq = cb.createQuery(Archive.class);
        Root<Archive> aRoot = cq.from(Archive.class);
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getAbbreviation() != null) {
            predicates.add(cb.like(cb.lower(aRoot.get("abbreviation")), "%" + filter.getAbbreviation().toLowerCase() + "%"));
        }
        if (filter.getName() != null) {
            predicates.add(cb.like(cb.lower(aRoot.get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        cq.select(aRoot).where(predicates.toArray(new Predicate[0]));
        List<Archive> result = entityManager.createQuery(cq).getResultList();
        if (result == null || result.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format("Archive not found filter: %s", filter), 1);
        }
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void updateLinks(Archive existInfo, Archive newInfo) {
        archiveDocumentHelper.updateEntities(
                existInfo.getId(),
                existInfo.getArchiveDocuments(),
                newInfo.getArchiveDocuments(),
                ArchiveDocument::getId,
                archiveDocumentRepository,
                archiveDocumentRepository::updateArchiveIdById);
    }
}
