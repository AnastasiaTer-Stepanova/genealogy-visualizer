package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.DeathDAO;
import genealogy.visualizer.service.LocalityDAO;

public class DeathDAOImpl implements DeathDAO {

    private final DeathRepository deathRepository;
    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final LocalityDAO localityDAO;

    public DeathDAOImpl(DeathRepository deathRepository,
                        ArchiveDocumentDAO archiveDocumentDAO,
                        LocalityDAO localityDAO) {
        this.deathRepository = deathRepository;
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.localityDAO = localityDAO;
    }

    @Override
    public void save(Death death) {
        ArchiveDocument archiveDocument = death.getArchiveDocument();
        if (archiveDocument != null && archiveDocument.getId() == null) {
            archiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(archiveDocument);
            death.setArchiveDocument(archiveDocument);
        }
        Locality locality = death.getLocality();
        if (locality != null && locality.getId() == null) {
            locality = localityDAO.saveOrFindIfExist(locality);
            death.setLocality(locality);
        }
        deathRepository.save(death);
    }
}
