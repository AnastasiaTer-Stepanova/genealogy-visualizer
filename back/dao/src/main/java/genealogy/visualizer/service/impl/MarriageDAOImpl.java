package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.model.Witness;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.LocalityDAO;
import genealogy.visualizer.service.MarriageDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class MarriageDAOImpl implements MarriageDAO {

    private final MarriageRepository marriageRepository;
    private final LocalityDAO localityDAO;
    private final ArchiveDocumentDAO archiveDocumentDAO;

    public MarriageDAOImpl(MarriageRepository marriageRepository, LocalityDAO localityDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        this.marriageRepository = marriageRepository;
        this.localityDAO = localityDAO;
        this.archiveDocumentDAO = archiveDocumentDAO;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void save(Marriage marriage) {
        ArchiveDocument archiveDocument = marriage.getArchiveDocument();
        if (archiveDocument != null && archiveDocument.getId() == null) {
            archiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(archiveDocument);
            marriage.setArchiveDocument(archiveDocument);
        }
        List<Witness> witnesses = marriage.getWitnesses();
        if (witnesses != null && !witnesses.isEmpty()) {
            witnesses.forEach(witness -> witness.setLocality(localityDAO.saveOrFindIfExist(witness.getLocality())));
        }
        Locality husbandLocality = marriage.getHusbandLocality();
        if (husbandLocality != null && husbandLocality.getId() == null) {
            husbandLocality = localityDAO.saveOrFindIfExist(husbandLocality);
            marriage.setHusbandLocality(husbandLocality);
        }
        Locality wifeLocality = marriage.getWifeLocality();
        if (wifeLocality != null && wifeLocality.getId() == null) {
            wifeLocality = localityDAO.saveOrFindIfExist(wifeLocality);
            marriage.setWifeLocality(wifeLocality);
        }
        marriageRepository.save(marriage);
    }
}
