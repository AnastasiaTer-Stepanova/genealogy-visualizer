package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.util.ErrorHelper;
import genealogy.visualizer.service.util.record.ResponseRecord;

public class FamilyRevisionServiceImpl implements FamilyRevisionService {

    private static final CycleAvoidingMappingContext mappingContext = new CycleAvoidingMappingContext();
    private final FamilyRevisionDAO familyRevisionDAO;
    private final FamilyRevisionMapper familyRevisionMapper;

    public FamilyRevisionServiceImpl(FamilyRevisionDAO familyRevisionDAO,
                                     FamilyRevisionMapper familyRevisionMapper) {
        this.familyRevisionDAO = familyRevisionDAO;
        this.familyRevisionMapper = familyRevisionMapper;
    }

    @Override
    public void delete(Long id) {
        familyRevisionDAO.delete(id);
    }

    @Override
    public ResponseRecord<FamilyRevision> getById(Long id) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.getById(id);
        if (entity == null) {
            return new ErrorHelper<FamilyRevision>().NOT_FOUND_ERROR;
        }
        return new ResponseRecord<>(null, familyRevisionMapper.toDTO(entity, mappingContext));
    }

    @Override
    public ResponseRecord<FamilyRevision> save(FamilyRevisionSave familyRevisionSave) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.save(familyRevisionMapper.toEntity(familyRevisionSave, mappingContext));
        return new ResponseRecord<>(null, familyRevisionMapper.toDTO(entity, mappingContext));
    }

    @Override
    public ResponseRecord<FamilyRevision> update(FamilyRevision familyRevision) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.update(familyRevisionMapper.toEntity(familyRevision, mappingContext));
        if (entity == null) {
            return new ErrorHelper<FamilyRevision>().NOT_FOUND_ERROR;
        }
        return new ResponseRecord<>(null, familyRevisionMapper.toDTO(entity, mappingContext));
    }
}
