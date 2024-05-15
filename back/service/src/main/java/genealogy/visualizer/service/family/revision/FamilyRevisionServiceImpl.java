package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevision;
import genealogy.visualizer.api.model.ArchiveWithFamilyRevisionList;
import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionFilter;
import genealogy.visualizer.api.model.FamilyRevisionResponse;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.ErrorMapper;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;

import java.util.Collections;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class FamilyRevisionServiceImpl implements FamilyRevisionService {

    private static final CycleAvoidingMappingContext mappingContext = new CycleAvoidingMappingContext();

    private final FamilyRevisionDAO familyRevisionDAO;
    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final FamilyRevisionMapper familyRevisionMapper;
    private final ArchiveDocumentMapper archiveDocumentMapper;
    private final ErrorMapper errorMapper;

    public FamilyRevisionServiceImpl(FamilyRevisionDAO familyRevisionDAO,
                                     ArchiveDocumentDAO archiveDocumentDAO,
                                     FamilyRevisionMapper familyRevisionMapper,
                                     ArchiveDocumentMapper archiveDocumentMapper,
                                     ErrorMapper errorMapper) {
        this.familyRevisionDAO = familyRevisionDAO;
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.familyRevisionMapper = familyRevisionMapper;
        this.archiveDocumentMapper = archiveDocumentMapper;
        this.errorMapper = errorMapper;
    }

    @Override
    public void delete(Long id) {
        familyRevisionDAO.delete(id);
    }

    @Override
    public FamilyRevisionResponse getById(Long id) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.getById(id);
        if (entity == null) {
            return errorMapper.toFamilyRevisionError(NOT_FOUND_ERROR);
        }
        return familyRevisionMapper.toDTO(entity, mappingContext);
    }

    @Override
    public FamilyRevisionResponse save(FamilyRevisionSave familyRevisionSave) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.save(familyRevisionMapper.toEntity(familyRevisionSave, mappingContext));
        return familyRevisionMapper.toDTO(entity, mappingContext);
    }

    @Override
    public FamilyRevisionResponse update(FamilyRevision familyRevision) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.update(familyRevisionMapper.toEntity(familyRevision, mappingContext));
        if (entity == null) {
            return errorMapper.toFamilyRevisionError(NOT_FOUND_ERROR);
        }
        return familyRevisionMapper.toDTO(entity, mappingContext);
    }

    @Override
    public FamilyRevisionResponse getArchivesWithFamilyRevision(FamilyRevisionFilter familyRevisionFilter) {
        ArchiveDocument archiveDocument = familyRevisionFilter.getArchiveDocument();
        if (archiveDocument == null || familyRevisionFilter.getFamilyRevisionNumber() == null) {
            return errorMapper.toFamilyRevisionError(BAD_REQUEST_ERROR);
        }
        if (familyRevisionFilter.getIsFindInAllRevision()) {
            //TODO доделать когда будет алгоритм связки
            return errorMapper.toFamilyRevisionError(BAD_REQUEST_ERROR);
        } else {
            genealogy.visualizer.entity.ArchiveDocument archiveDocumentEntity = archiveDocumentDAO.findArchiveDocumentWithFamilyRevisionByNumberFamily(
                    archiveDocumentMapper.toEntity(archiveDocument, mappingContext),
                    familyRevisionFilter.getFamilyRevisionNumber().shortValue());
            if (archiveDocumentEntity == null || archiveDocumentEntity.getFamilyRevisions() == null ||
                    archiveDocumentEntity.getFamilyRevisions().isEmpty()) {
                return errorMapper.toFamilyRevisionError(NOT_FOUND_ERROR);
            }
            return new ArchiveWithFamilyRevisionList().data(Collections.singletonList(new ArchiveWithFamilyRevision(
                    archiveDocumentMapper.toDTO(archiveDocumentEntity, mappingContext),
                    familyRevisionMapper.toListDTO(archiveDocumentEntity.getFamilyRevisions(), mappingContext))));
        }
    }
}
