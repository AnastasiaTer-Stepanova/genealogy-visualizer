package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberSave;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, AgeMapper.class, ArchiveDocumentMapper.class, PersonMapper.class})
public interface FamilyRevisionMapper {

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "person", ignore = true)
    genealogy.visualizer.entity.FamilyRevision toEntity(FamilyMember member, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    genealogy.visualizer.entity.FamilyRevision toEntity(FamilyMemberSave member, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    @Mapping(target = "partner.isHeadOfYard", source = "partner.headOfYard")
    @Mapping(target = "partner.partner", ignore = true)
    FamilyMember toDTO(genealogy.visualizer.entity.FamilyRevision family, @Context CycleAvoidingMappingContext context);
}
