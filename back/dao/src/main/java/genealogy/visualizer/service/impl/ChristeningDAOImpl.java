package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.model.GodParent;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.LocalityDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ChristeningDAOImpl implements ChristeningDAO {

    private final ChristeningRepository christeningRepository;
    private final LocalityDAO localityDAO;
    private final ArchiveDocumentDAO archiveDocumentDAO;

    public ChristeningDAOImpl(ChristeningRepository christeningRepository,
                              LocalityDAO localityDAO,
                              ArchiveDocumentDAO archiveDocumentDAO) {
        this.christeningRepository = christeningRepository;
        this.localityDAO = localityDAO;
        this.archiveDocumentDAO = archiveDocumentDAO;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void save(Christening christening) {
        ArchiveDocument archiveDocument = christening.getArchiveDocument();
        if (archiveDocument != null && archiveDocument.getId() == null) {
            archiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(archiveDocument);
            christening.setArchiveDocument(archiveDocument);
        }
        List<GodParent> godParents = christening.getGodParents();
        if (godParents != null && !godParents.isEmpty()) {
            godParents.forEach(godParent -> godParent.setLocality(localityDAO.saveOrFindIfExist(godParent.getLocality())));
        }
        Locality locality = christening.getLocality();
        if (locality != null && locality.getId() == null) {
            locality = localityDAO.saveOrFindIfExist(locality);
            christening.setLocality(locality);
        }
        christeningRepository.save(christening);
    }
}
