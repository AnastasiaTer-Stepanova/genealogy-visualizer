package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.FamilyRevision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {AgeMapper.class,
                EasyArchiveDocumentMapper.class,
                EasyFamilyRevisionMapper.class,
                EasyPersonMapper.class,
                FullNameMapper.class})
public interface FamilyRevisionMapper extends CommonMapper<FamilyMember, FamilyRevision>, FilterMapper<FamilyRevisionFilterDTO, FamilyMemberFilter> {

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "lastNameClearlyStated", source = "isLastNameClearlyStated")
    FamilyRevision toEntity(FamilyMember familyMember);

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    @Mapping(target = "isLastNameClearlyStated", source = "lastNameClearlyStated")
    FamilyMember toDTO(FamilyRevision familyMember);

    @Mapping(target = "findWithHavePerson", source = "isFindWithHavePerson")
    @Mapping(target = "graphs", ignore = true)
    FamilyRevisionFilterDTO toFilter(FamilyMemberFilter filter);
}
