package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class FamilyRevisionDAOImpl implements FamilyRevisionDAO {

    private final FamilyRevisionRepository familyRevisionRepository;

    public FamilyRevisionDAOImpl(FamilyRevisionRepository familyRevisionRepository) {
        this.familyRevisionRepository = familyRevisionRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveBatch(List<FamilyRevision> familyRevisions) {
        familyRevisionRepository.saveAll(familyRevisions);
    }

}