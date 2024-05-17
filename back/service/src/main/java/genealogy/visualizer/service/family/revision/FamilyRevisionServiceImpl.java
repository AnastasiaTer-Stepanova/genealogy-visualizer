package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.api.model.FamilyMemberSave;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class FamilyRevisionServiceImpl implements FamilyRevisionService {

    private static final CycleAvoidingMappingContext mappingContext = new CycleAvoidingMappingContext();

    private final FamilyRevisionDAO familyRevisionDAO;
    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final FamilyRevisionMapper familyRevisionMapper;

    public FamilyRevisionServiceImpl(FamilyRevisionDAO familyRevisionDAO,
                                     ArchiveDocumentDAO archiveDocumentDAO,
                                     FamilyRevisionMapper familyRevisionMapper) {
        this.familyRevisionDAO = familyRevisionDAO;
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.familyRevisionMapper = familyRevisionMapper;
    }

    @Override
    public void delete(Long id) {
        familyRevisionDAO.delete(id);
    }

    @Override
    public FamilyMember getById(Long id) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return familyRevisionMapper.toDTO(entity, mappingContext);
    }

    @Override
    public FamilyMember save(FamilyMemberSave familyRevisionSave) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.save(familyRevisionMapper.toEntity(familyRevisionSave, mappingContext));
        return familyRevisionMapper.toDTO(entity, mappingContext);
    }

    @Override
    public FamilyMember update(FamilyMember familyRevision) {
        genealogy.visualizer.entity.FamilyRevision entity = familyRevisionDAO.update(familyRevisionMapper.toEntity(familyRevision, mappingContext));
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return familyRevisionMapper.toDTO(entity, mappingContext);
    }

    @Override
    public List<FamilyMemberFullInfo> getFamilyMemberFullInfoList(FamilyMemberFilter familyMemberFilter) {
        if (familyMemberFilter.getArchiveDocumentId() == null || familyMemberFilter.getFamilyRevisionNumber() == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        if (familyMemberFilter.getIsFindInAllRevision()) {
            //TODO доделать когда будет алгоритм связки
            throw new RuntimeException(BAD_REQUEST_ERROR);
        } else {
            genealogy.visualizer.entity.ArchiveDocument archiveDocumentEntity = archiveDocumentDAO.findArchiveDocumentWithFamilyRevisionByNumberFamily(
                    familyMemberFilter.getArchiveDocumentId(),
                    familyMemberFilter.getFamilyRevisionNumber().shortValue());
            if (archiveDocumentEntity == null || archiveDocumentEntity.getFamilyRevisions() == null ||
                    archiveDocumentEntity.getFamilyRevisions().isEmpty()) {
                throw new RuntimeException(BAD_REQUEST_ERROR);
            }
            return archiveDocumentEntity.getFamilyRevisions()
                    .stream()
                    .map(fr -> new FamilyMemberFullInfo().familyMember(familyRevisionMapper.toDTO(fr, mappingContext)))
                    .toList();
        }
    }
}
