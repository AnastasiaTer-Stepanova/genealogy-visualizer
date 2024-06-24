package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.entity.FamilyRevision;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, AgeMapper.class})
public interface EasyFamilyRevisionMapper extends EasyCommonMapper<EasyFamilyMember, FamilyRevision> {

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    @Mapping(target = "isLastNameClearlyStated", source = "lastNameClearlyStated")
    @BeanMapping(ignoreUnmappedSourceProperties = {"partner", "archiveDocument", "person"})
    EasyFamilyMember toDTO(FamilyRevision familyMember);

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "lastNameClearlyStated", source = "isLastNameClearlyStated")
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    FamilyRevision toEntity(EasyFamilyMember familyMember);

    List<EasyFamilyMember> toDTOs(List<FamilyRevision> familyMembers);

    List<FamilyRevision> toEntities(List<EasyFamilyMember> christening);

}
