package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.GodParent;
import genealogy.visualizer.repository.GodParentRepository;
import genealogy.visualizer.service.GodParentDAO;
import genealogy.visualizer.service.LocalityDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class GodParentDAOImpl implements GodParentDAO {

    private final GodParentRepository godParentRepository;
    private final LocalityDAO localityDAO;

    public GodParentDAOImpl(GodParentRepository godParentRepository,
                            LocalityDAO localityDAO) {
        this.godParentRepository = godParentRepository;
        this.localityDAO = localityDAO;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<GodParent> saveBatch(Iterable<GodParent> godParents) {
        godParents.forEach(godParent -> {
            if (godParent.getLocality() != null) {
                godParent.setLocality(localityDAO.saveOrFindIfExist(godParent.getLocality()));
            }
        });
        return godParentRepository.saveAll(godParents);
    }
}
