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
    public void delete(Long id) {
        archiveDocumentRepository.updateArchiveId(id, null);
        archiveRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive save(Archive archive) {
        if (archive.getId() != null) {
            throw new IllegalArgumentException("Cannot save archive with id");
        }
        List<ArchiveDocument> archiveDocument = archive.getArchiveDocuments();
        archive.setArchiveDocuments(Collections.emptyList());
        Archive savedArchive = archiveRepository.save(archive);
        if (archiveDocument != null && !archiveDocument.isEmpty()) {
            RepositoryEasyModelHelper<ArchiveDocument> repositoryEasyModelHelper = new RepositoryEasyModelHelper<>();
            archiveDocument.forEach(ad -> ad.setArchive(savedArchive));
            archive.setArchiveDocuments(repositoryEasyModelHelper.saveEntitiesIfNotExist(
                    archiveDocument, archiveDocumentRepository, ArchiveDocument::getId));
        }
        return savedArchive;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive update(Archive archive) {
        if (archive.getId() == null)
            throw new IllegalArgumentException("Cannot update archive without id");
        Archive updatedArchive = archiveRepository.update(archive);
        updatedArchive.setArchiveDocuments(archiveDocumentHelper.updateEntities(
                updatedArchive.getId(), updatedArchive.getArchiveDocuments(), archive.getArchiveDocuments(), ArchiveDocument::getId,
                archiveDocumentRepository, archiveDocumentRepository::updateArchiveIdById));
        return updatedArchive;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Archive findFullInfoById(Long id) {
        return archiveRepository.findByIdWithArchiveDocuments(id).orElse(null);
    }

    @Override
    public List<Archive> filter(ArchiveFilterDTO filter) {
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
        return entityManager.createQuery(cq).getResultList();
    }
}
