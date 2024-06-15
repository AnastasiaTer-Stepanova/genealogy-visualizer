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
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FamilyRevisionServiceImpl implements FamilyRevisionService {

    private final FamilyRevisionDAO familyRevisionDAO;
    private final FamilyRevisionMapper familyRevisionMapper;
    private final EasyFamilyRevisionMapper easyFamilyRevisionMapper;
    private final EasyArchiveDocumentMapper easyArchiveDocumentMapper;

    public FamilyRevisionServiceImpl(FamilyRevisionDAO familyRevisionDAO,
                                     FamilyRevisionMapper familyRevisionMapper,
                                     EasyFamilyRevisionMapper easyFamilyRevisionMapper,
                                     EasyArchiveDocumentMapper easyArchiveDocumentMapper) {
        this.familyRevisionDAO = familyRevisionDAO;
        this.familyRevisionMapper = familyRevisionMapper;
        this.easyFamilyRevisionMapper = easyFamilyRevisionMapper;
        this.easyArchiveDocumentMapper = easyArchiveDocumentMapper;
    }

    @Override
    public void delete(Long id) {
        familyRevisionDAO.delete(id);
    }

    @Override
    public FamilyMember getById(Long id) {
        return Optional.ofNullable(familyRevisionMapper.toDTO(familyRevisionDAO.findFullInfoById(id)))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public FamilyMember save(FamilyMember familyMember) {
        if (familyMember == null || familyMember.getId() != null) {
            throw new BadRequestException("FamilyMember must not have an id");
        }
        return familyRevisionMapper.toDTO(familyRevisionDAO.save(familyRevisionMapper.toEntity(familyMember)));
    }

    @Override
    public FamilyMember update(FamilyMember familyRevision) {
        if (familyRevision == null || familyRevision.getId() == null) {
            throw new BadRequestException("FamilyMember must have an id");
        }
        genealogy.visualizer.entity.FamilyRevision entity;
        try {
            entity = familyRevisionDAO.update(familyRevisionMapper.toEntity(familyRevision));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Family revision for update not found");
        }
        return familyRevisionMapper.toDTO(entity);
    }

    @Override
    public List<EasyFamilyMember> filter(FamilyMemberFilter filter) {
        return Optional.ofNullable(easyFamilyRevisionMapper.toDTOs(familyRevisionDAO.filter(familyRevisionMapper.toFilter(filter))))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<FamilyMemberFullInfo> getFamilyMemberFullInfoList(FamilyFilter familyFilter) {
        if (familyFilter.getArchiveDocumentId() == null || familyFilter.getFamilyRevisionNumber() == null) {
            throw new BadRequestException();
        }
        List<FamilyRevision> familyMembers = familyRevisionDAO.filter(new FamilyRevisionFilterDTO(
                familyFilter.getArchiveDocumentId(),
                familyFilter.getFamilyRevisionNumber().shortValue(),
                null,
                null,
                familyFilter.getIsFindWithHavePerson()));
        if (familyMembers == null || familyMembers.isEmpty()) {
            throw new NotFoundException();
        }
        return familyMembers
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
        if (archiveDocument == null || archiveDocument.getNextRevision() == null && (archiveDocument.getPreviousRevisions() == null
                || archiveDocument.getPreviousRevisions().isEmpty())) {
            return fullInfo;
        }
        Map<ArchiveDocument, List<EasyFamilyMember>> archiveDocumentMap = new HashMap<>();
        archiveDocumentMap.put(archiveDocument, Collections.emptyList());
        if (archiveDocument.getNextRevision() != null) {
            do {
                archiveDocument = archiveDocument.getNextRevision();
                if (familyMember.getNextFamilyRevisionNumber() == null) continue;
                archiveDocumentMap.put(archiveDocument, easyFamilyRevisionMapper.toDTOs(
                        familyRevisionDAO.filter(new FamilyRevisionFilterDTO(
                                archiveDocument.getId(),
                                familyMember.getNextFamilyRevisionNumber(),
                                null,
                                null,
                                isFindWithHavePerson))));
            } while (archiveDocument.getNextRevision() != null);
        }

        List<EasyFamilyMember> lastFamily = archiveDocumentMap.get(archiveDocument);
        if (lastFamily == null) {
            archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap,
                    archiveDocument,
                    familyMember.getFamilyRevisionNumber(),
                    isFindWithHavePerson);
        } else {
            for (EasyFamilyMember lastFamilyMember : lastFamily) {
                archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap,
                        archiveDocument,
                        lastFamilyMember.getFamilyRevisionNumber().shortValue(),
                        isFindWithHavePerson);
            }
        }

        fullInfo.setAnotherFamilies(archiveDocumentMap
                .entrySet().stream()
                .map(map -> {
                    if (map.getValue() == null || map.getValue().isEmpty()) return null;
                    return new ArchiveWithFamilyMembers()
                            .archive(easyArchiveDocumentMapper.toDTO(map.getKey()))
                            .families(map.getValue());
                })
                .filter(Objects::nonNull)
                .toList());
        return fullInfo;
    }

    private Map<ArchiveDocument, List<EasyFamilyMember>> getArchiveWithFamilyMembers(Map<ArchiveDocument, List<EasyFamilyMember>> archiveDocumentMap,
                                                                                     ArchiveDocument archiveDocument,
                                                                                     Short familyRevisionNumber,
                                                                                     Boolean isFindWithHavePerson) {
        if (archiveDocument.getPreviousRevisions() == null || archiveDocument.getPreviousRevisions().isEmpty()) {
            return archiveDocumentMap;
        }
        for (ArchiveDocument archiveDocumentPrevious : archiveDocument.getPreviousRevisions()) {
            if (archiveDocumentMap.containsKey(archiveDocumentPrevious)) continue;
            List<EasyFamilyMember> members = easyFamilyRevisionMapper.toDTOs(
                    familyRevisionDAO.filter(new FamilyRevisionFilterDTO(
                            archiveDocumentPrevious.getId(),
                            familyRevisionNumber,
                            null,
                            null,
                            isFindWithHavePerson)));
            archiveDocumentMap.put(archiveDocumentPrevious, members);
            if (members != null && !members.isEmpty()) {
                for (EasyFamilyMember familyMember : members) {
                    archiveDocumentMap = getArchiveWithFamilyMembers(archiveDocumentMap,
                            archiveDocumentPrevious,
                            familyMember.getFamilyRevisionNumber().shortValue(),
                            isFindWithHavePerson);
                }
            }
        }
        return archiveDocumentMap;
    }
}
