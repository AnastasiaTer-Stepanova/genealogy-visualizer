package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.FamilyMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, AgeMapper.class, ArchiveDocumentMapper.class, PersonMapper.class})
public interface FamilyRevisionMapper {

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "partner", expression = "java(this.fromEasyDTO(familyMember.getPartner()))")
    genealogy.visualizer.entity.FamilyRevision toEntity(FamilyMember familyMember);

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    @Mapping(target = "partner", expression = "java(this.toEasyDTO(familyMember.getPartner()))")
    FamilyMember toDTO(genealogy.visualizer.entity.FamilyRevision familyMember);

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    EasyFamilyMember toEasyDTO(genealogy.visualizer.entity.FamilyRevision familyMember);

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "partner", ignore = true)
    genealogy.visualizer.entity.FamilyRevision fromEasyDTO(EasyFamilyMember familyMember);

    List<FamilyMember> toListDTO(List<genealogy.visualizer.entity.FamilyRevision> familyMembers);
}
