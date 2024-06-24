package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.ArchiveWithFamilyMembers;
import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.FamilyFilter;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.mapper.EasyArchiveDocumentMapper;
import genealogy.visualizer.mapper.EasyFamilyRevisionMapper;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FamilyRevisionServiceImpl extends AbstractCommonOperationService<FamilyRevision, FamilyMember, FamilyMemberFilter, EasyFamilyMember, FamilyRevisionFilterDTO>
        implements FamilyRevisionService {

    private final FamilyRevisionDAO familyRevisionDAO;
    private final EasyFamilyRevisionMapper easyFamilyRevisionMapper;
    private final EasyArchiveDocumentMapper easyArchiveDocumentMapper;

    public FamilyRevisionServiceImpl(FamilyRevisionDAO familyRevisionDAO,
                                     FamilyRevisionMapper familyRevisionMapper,
                                     EasyFamilyRevisionMapper easyFamilyRevisionMapper,
                                     EasyArchiveDocumentMapper easyArchiveDocumentMapper) {
        super(familyRevisionDAO, familyRevisionDAO, familyRevisionMapper, familyRevisionMapper, easyFamilyRevisionMapper);
        this.familyRevisionDAO = familyRevisionDAO;
        this.easyFamilyRevisionMapper = easyFamilyRevisionMapper;
        this.easyArchiveDocumentMapper = easyArchiveDocumentMapper;
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public FamilyMember getById(Long id) {
        return super.getById(id);
    }

    @Override
    public FamilyMember save(FamilyMember familyMember) {
        return super.save(familyMember);
    }

    @Override
    public FamilyMember update(FamilyMember familyMember) {
        return super.update(familyMember);
    }

    @Override
    public List<EasyFamilyMember> filter(FamilyMemberFilter filter) {
        return super.filter(filter);
    }

    @Override
    public List<FamilyMemberFullInfo> getFamilyMemberFullInfoList(FamilyFilter familyFilter) {
        if (familyFilter.getArchiveDocumentId() == null || familyFilter.getFamilyRevisionNumber() == null) {
            throw new BadRequestException();
        }
        return findFamilyMembers(new FamilyRevisionFilterDTO(
                familyFilter.getArchiveDocumentId(),
                familyFilter.getFamilyRevisionNumber().shortValue(),
                null,
                null,
                familyFilter.getIsFindWithHavePerson(),
                familyFilter.getIsFindInAllRevision() ?
                        List.of("FamilyRevision.withArchiveDocumentAndAnotherRevisionsInside") :
                        Collections.emptyList()))
                .stream()
                .map(fr -> familyFilter.getIsFindInAllRevision() ?
                        getFamilyMemberFullInfo(fr, familyFilter.getIsFindWithHavePerson()) :
                        new FamilyMemberFullInfo().familyMember(easyFamilyRevisionMapper.toDTO(fr)))
                .toList();
    }

    private FamilyMemberFullInfo getFamilyMemberFullInfo(FamilyRevision familyMember, Boolean isFindWithHavePerson) {
        FamilyMemberFullInfo fullInfo = new FamilyMemberFullInfo();
        fullInfo.setFamilyMember(easyFamilyRevisionMapper.toDTO(familyMember));

        ArchiveDocument archiveDocument = familyMember.getArchiveDocument();
        if (archiveDocument == null || (archiveDocument.getNextRevision() == null && (archiveDocument.getPreviousRevisions() == null
                || archiveDocument.getPreviousRevisions().isEmpty()))) {
            return fullInfo;
        }

        Map<ArchiveDocument, List<EasyFamilyMember>> archiveDocumentMap = buildArchiveDocumentMap(archiveDocument, familyMember, isFindWithHavePerson);

        List<EasyFamilyMember> lastFamily = archiveDocumentMap.get(archiveDocument);
        if (lastFamily == null) {
            archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap, archiveDocument, familyMember.getFamilyRevisionNumber(), isFindWithHavePerson);
        } else {
            for (EasyFamilyMember lastFamilyMember : lastFamily) {
                archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap, archiveDocument, lastFamilyMember.getFamilyRevisionNumber().shortValue(), isFindWithHavePerson);
            }
        }
        fullInfo.setAnotherFamilies(archiveDocumentMap.entrySet().stream()
                .map(map -> map.getValue().isEmpty() ? null : new ArchiveWithFamilyMembers()
                        .archive(easyArchiveDocumentMapper.toDTO(map.getKey()))
                        .families(map.getValue()))
                .filter(Objects::nonNull)
                .toList());
        return fullInfo;
    }

    private Map<ArchiveDocument, List<EasyFamilyMember>> buildArchiveDocumentMap(ArchiveDocument archiveDocument, FamilyRevision familyMember, Boolean isFindWithHavePerson) {
        Map<ArchiveDocument, List<EasyFamilyMember>> archiveDocumentMap = new HashMap<>();
        archiveDocumentMap.put(archiveDocument, Collections.emptyList());
        while (archiveDocument.getNextRevision() != null) {
            archiveDocument = archiveDocument.getNextRevision();
            if (familyMember.getNextFamilyRevisionNumber() != null) {
                archiveDocumentMap.put(archiveDocument, easyFamilyRevisionMapper.toDTOs(
                        findFamilyMembers(new FamilyRevisionFilterDTO(
                                archiveDocument.getId(),
                                familyMember.getNextFamilyRevisionNumber(),
                                null,
                                null,
                                isFindWithHavePerson,
                                List.of("FamilyRevision.withArchiveDocumentAndAnotherRevisionsInside")))));
            }
        }
        return archiveDocumentMap;
    }

    private Map<ArchiveDocument, List<EasyFamilyMember>> getArchiveWithFamilyMembers(Map<ArchiveDocument, List<EasyFamilyMember>> archiveDocumentMap,
                                                                                     ArchiveDocument archiveDocument,
                                                                                     Short familyRevisionNumber,
                                                                                     Boolean isFindWithHavePerson) {
        if (archiveDocument.getPreviousRevisions() == null || archiveDocument.getPreviousRevisions().isEmpty()) {
            return archiveDocumentMap;
        }
        for (ArchiveDocument previousRevision : archiveDocument.getPreviousRevisions()) {
            if (!archiveDocumentMap.containsKey(previousRevision)) {
                List<EasyFamilyMember> members = easyFamilyRevisionMapper.toDTOs(
                        findFamilyMembers(new FamilyRevisionFilterDTO(
                                previousRevision.getId(),
                                familyRevisionNumber,
                                null,
                                null,
                                isFindWithHavePerson,
                                List.of("FamilyRevision.withArchiveDocumentAndAnotherRevisionsInside"))));
                archiveDocumentMap.put(previousRevision, members);
                if (members != null && !members.isEmpty()) {
                    for (EasyFamilyMember familyMember : members) {
                        archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap, previousRevision, familyMember.getFamilyRevisionNumber().shortValue(), isFindWithHavePerson);
                    }
                }
            }
        }
        return archiveDocumentMap;
    }

    private List<FamilyRevision> findFamilyMembers(FamilyRevisionFilterDTO filter) {
        try {
            return familyRevisionDAO.filter(filter);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }
}
