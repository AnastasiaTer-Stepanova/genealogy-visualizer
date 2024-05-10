package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.FamilyRevision;
import genealogy.visualizer.api.model.FamilyRevisionSave;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, AgeMapper.class, ArchiveDocumentMapper.class, PersonMapper.class})
public interface FamilyRevisionMapper {

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "person", ignore = true)
    genealogy.visualizer.entity.FamilyRevision toEntity(FamilyRevision family, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "headOfYard", source = "isHeadOfYard")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    genealogy.visualizer.entity.FamilyRevision toEntity(FamilyRevisionSave family, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "isHeadOfYard", source = "headOfYard")
    @Mapping(target = "partner.isHeadOfYard", source = "partner.headOfYard")
    @Mapping(target = "partner.partner", ignore = true)
    FamilyRevision toDTO(genealogy.visualizer.entity.FamilyRevision family, @Context CycleAvoidingMappingContext context);

    List<FamilyRevision> toListDTO(List<genealogy.visualizer.entity.FamilyRevision> families, @Context CycleAvoidingMappingContext context);

}
