package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.ArchiveWithFamilyMembers;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.api.model.FamilyMemberSave;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.CycleAvoidingMappingContext;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

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
        genealogy.visualizer.entity.ArchiveDocument archiveDocumentEntity = archiveDocumentDAO.findArchiveDocumentWithFamilyRevisionByNumberFamily(
                familyMemberFilter.getArchiveDocumentId(),
                familyMemberFilter.getFamilyRevisionNumber().shortValue());
        if (archiveDocumentEntity == null || archiveDocumentEntity.getFamilyRevisions() == null ||
                archiveDocumentEntity.getFamilyRevisions().isEmpty()) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        List<FamilyRevision> familyMembers = archiveDocumentEntity.getFamilyRevisions();
        if (familyMemberFilter.getIsFindInAllRevision() && (archiveDocumentEntity.getPreviousRevisions() != null &&
                !archiveDocumentEntity.getPreviousRevisions().isEmpty() || archiveDocumentEntity.getNextRevision() != null)) {
            return familyMembers
                    .stream()
                    .map(this::getFamilyMemberFullInfo)
                    .toList();
        }
        return familyMembers
                .stream()
                .map(fr -> new FamilyMemberFullInfo().familyMember(familyRevisionMapper.toDTO(fr, mappingContext)))
                .toList();
    }

    private FamilyMemberFullInfo getFamilyMemberFullInfo(FamilyRevision familyMember) {
        FamilyMemberFullInfo fullInfo = new FamilyMemberFullInfo();
        fullInfo.setFamilyMember(familyRevisionMapper.toDTO(familyMember, mappingContext));
        ArchiveDocument archiveDocument = familyMember.getArchiveDocument();
        if (archiveDocument == null || archiveDocument.getNextRevision() == null && (archiveDocument.getPreviousRevisions() == null
                || archiveDocument.getPreviousRevisions().isEmpty())) {
            return fullInfo;
        }
        Map<ArchiveDocument, List<FamilyMember>> archiveDocumentMap = new HashMap<>();
        archiveDocumentMap.put(archiveDocument, Collections.emptyList());
        if (archiveDocument.getNextRevision() != null) {
            do {
                archiveDocument = archiveDocument.getNextRevision();
                if (familyMember.getNextFamilyRevisionNumber() == null) continue;
                archiveDocumentMap.put(archiveDocument, familyRevisionMapper.toListDTO(
                        familyRevisionDAO.findFamilyRevisionsByNumberFamilyAndArchiveDocumentId(
                                archiveDocument.getId(),
                                familyMember.getNextFamilyRevisionNumber()),
                        mappingContext));
            } while (archiveDocument.getNextRevision() != null);
        }

        List<FamilyMember> lastFamily = archiveDocumentMap.get(archiveDocument);
        if (lastFamily == null) {
            archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap, archiveDocument, familyMember.getFamilyRevisionNumber());
        } else {
            for (FamilyMember lastFamilyMember : lastFamily) {
                archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap, archiveDocument, lastFamilyMember.getFamilyRevisionNumber().shortValue());
            }
        }

        fullInfo.setAnotherFamilies(archiveDocumentMap
                .entrySet().stream()
                .map(map -> {
                    if (map.getValue() == null || map.getValue().isEmpty()) return null;
                    return new ArchiveWithFamilyMembers()
                            .archive(archiveDocumentMapper.toDTO(map.getKey(), mappingContext))
                            .families(map.getValue());
                })
                .filter(Objects::nonNull)
                .toList());
        return fullInfo;
    }

    private Map<ArchiveDocument, List<FamilyMember>> getArchiveWithFamilyMembers(Map<ArchiveDocument, List<FamilyMember>> archiveDocumentMap,
                                                                                 ArchiveDocument archiveDocument, Short familyRevisionNumber) {
        if (archiveDocument.getPreviousRevisions() == null || archiveDocument.getPreviousRevisions().isEmpty()) {
            return archiveDocumentMap;
        }
        for (ArchiveDocument archiveDocumentPrevious : archiveDocument.getPreviousRevisions()) {
            if (archiveDocumentMap.containsKey(archiveDocumentPrevious)) continue;
            List<FamilyMember> members = familyRevisionMapper.toListDTO(
                    familyRevisionDAO.findFamilyRevisionsByNextFamilyRevisionNumberAndArchiveDocumentId(
                            archiveDocumentPrevious.getId(),
                            familyRevisionNumber),
                    mappingContext);
            archiveDocumentMap.put(archiveDocumentPrevious, members);
            if (members != null && !members.isEmpty()) {
                for (FamilyMember familyMember : members) {
                    archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap, archiveDocumentPrevious, familyMember.getFamilyRevisionNumber().shortValue());
                }
            }
        }
        return archiveDocumentMap;
    }
}
