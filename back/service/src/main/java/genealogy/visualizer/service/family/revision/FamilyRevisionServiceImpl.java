package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevision;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionFilter;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.util.ErrorHelper;
import genealogy.visualizer.service.util.record.ResponseRecord;

import java.util.Collections;
import java.util.List;

public class FamilyRevisionServiceImpl implements FamilyRevisionService {

    private static final CycleAvoidingMappingContext mappingContext = new CycleAvoidingMappingContext();

    private final FamilyRevisionDAO familyRevisionDAO;
    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final FamilyRevisionMapper familyRevisionMapper;
    private final ArchiveDocumentMapper archiveDocumentMapper;

    public FamilyRevisionServiceImpl(FamilyRevisionDAO familyRevisionDAO,
                                     ArchiveDocumentDAO archiveDocumentDAO,
                                     FamilyRevisionMapper familyRevisionMapper,
                                     ArchiveDocumentMapper archiveDocumentMapper) {
        this.familyRevisionDAO = familyRevisionDAO;
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.familyRevisionMapper = familyRevisionMapper;
        this.archiveDocumentMapper = archiveDocumentMapper;
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

    @Override
    public ResponseRecord<List<ArchiveWithFamilyRevision>> getArchivesWithFamilyRevision(FamilyRevisionFilter familyRevisionFilter) {
        ArchiveDocument archiveDocument = familyRevisionFilter.getArchiveDocument();
        if (archiveDocument == null || familyRevisionFilter.getFamilyRevisionNumber() == null) {
            return new ErrorHelper<List<ArchiveWithFamilyRevision>>().BAD_REQUEST_ERROR;
        }
        if (familyRevisionFilter.getIsFindInAllRevision()) {
            //TODO доделать когда будет алгоритм связки
            return new ErrorHelper<List<ArchiveWithFamilyRevision>>().BAD_REQUEST_ERROR;
        } else {
            genealogy.visualizer.entity.ArchiveDocument archiveDocumentEntity = archiveDocumentDAO.findArchiveDocumentWithFamilyRevisionByNumberFamily(
                    archiveDocumentMapper.toEntity(archiveDocument),
                    familyRevisionFilter.getFamilyRevisionNumber().shortValue());
            if (archiveDocumentEntity == null || archiveDocumentEntity.getFamilyRevisions() == null ||
                    archiveDocumentEntity.getFamilyRevisions().isEmpty()) {
                return new ErrorHelper<List<ArchiveWithFamilyRevision>>().NOT_FOUND_ERROR;
            }
            return new ResponseRecord<>(null,
                    Collections.singletonList(new ArchiveWithFamilyRevision(
                            archiveDocumentMapper.toDTO(archiveDocumentEntity),
                            familyRevisionMapper.toListDTO(archiveDocumentEntity.getFamilyRevisions(), mappingContext))));
        }
    }
}
