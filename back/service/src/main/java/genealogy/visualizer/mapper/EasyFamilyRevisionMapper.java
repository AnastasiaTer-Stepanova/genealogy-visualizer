package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyFamilyMember;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, AgeMapper.class})
public interface EasyFamilyRevisionMapper {

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    @Mapping(target = "isLastNameClearlyStated", source = "lastNameClearlyStated")
    @BeanMapping(ignoreUnmappedSourceProperties = {"partner", "archiveDocument", "person"})
    EasyFamilyMember toDTO(genealogy.visualizer.entity.FamilyRevision familyMember);

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "lastNameClearlyStated", source = "isLastNameClearlyStated")
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    genealogy.visualizer.entity.FamilyRevision toEntity(EasyFamilyMember familyMember);

    List<EasyFamilyMember> toDTOs(List<genealogy.visualizer.entity.FamilyRevision> familyMembers);

    List<genealogy.visualizer.entity.FamilyRevision> toEntities(List<EasyFamilyMember> christening);

}
