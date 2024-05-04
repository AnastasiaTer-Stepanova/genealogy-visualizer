package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.GodParent;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.GodParentDAO;
import genealogy.visualizer.service.LocalityDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ChristeningDAOImpl implements ChristeningDAO {

    private final ChristeningRepository christeningRepository;
    private final LocalityDAO localityDAO;
    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final GodParentDAO godParentDAO;

    public ChristeningDAOImpl(ChristeningRepository christeningRepository,
                              LocalityDAO localityDAO,
                              ArchiveDocumentDAO archiveDocumentDAO,
                              GodParentDAO godParentDAO) {
        this.christeningRepository = christeningRepository;
        this.localityDAO = localityDAO;
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.godParentDAO = godParentDAO;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void save(Christening christening) {
        Locality locality = christening.getLocality();
        if (locality != null && locality.getId() == null) {
            locality = localityDAO.saveOrFindIfExist(locality);
            christening.setLocality(locality);
        }
        ArchiveDocument archiveDocument = christening.getArchiveDocument();
        if (archiveDocument != null && archiveDocument.getId() == null) {
            archiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(archiveDocument);
            christening.setArchiveDocument(archiveDocument);
        }
        christening = christeningRepository.save(christening);
        List<GodParent> godParents = christening.getGodParents();
        if (godParents != null && !godParents.isEmpty()) {
            for (GodParent godParent : godParents) {
                godParent.setChristening(christening);
            }
            godParentDAO.saveBatch(christening.getGodParents());
        }
    }
}
