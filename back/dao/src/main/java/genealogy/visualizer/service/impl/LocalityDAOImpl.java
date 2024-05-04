package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.service.LocalityDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class LocalityDAOImpl implements LocalityDAO {

    private final LocalityRepository localityRepository;

    public LocalityDAOImpl(LocalityRepository localityRepository) {
        this.localityRepository = localityRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Locality saveOrFindIfExist(Locality locality) {
        if (locality == null || locality.getName() == null) return null;
        if (locality.getId() == null) {
            return localityRepository.findLocality(locality.getName(), locality.getType(), locality.getAddress())
                    .or(() -> Optional.of(localityRepository.save(locality)))
                    .orElseThrow();
        }
        return localityRepository.findById(locality.getId()).orElseThrow();
    }
}
