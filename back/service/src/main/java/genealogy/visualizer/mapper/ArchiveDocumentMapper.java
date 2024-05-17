package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.ArchiveDocument;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {ArchiveMapper.class})
public interface ArchiveDocumentMapper extends CommonMapper {

    @Mapping(target = "familyRevisions", ignore = true)
    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "marriages", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    genealogy.visualizer.entity.ArchiveDocument toEntity(ArchiveDocument archiveDocument, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "nextRevision.nextRevision", ignore = true)
    @Mapping(target = "nextRevision.previousRevisions", ignore = true)
    @Mapping(target = "previousRevisions", ignore = true)
    ArchiveDocument toDTO(genealogy.visualizer.entity.ArchiveDocument archiveDocument, @Context CycleAvoidingMappingContext context);

    List<ArchiveDocument> toDTO(List<genealogy.visualizer.entity.ArchiveDocument> archiveDocument, @Context CycleAvoidingMappingContext context);

}
