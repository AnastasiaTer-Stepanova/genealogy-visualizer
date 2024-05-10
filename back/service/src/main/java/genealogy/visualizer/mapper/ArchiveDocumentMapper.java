package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.ArchiveDocument;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {ArchiveMapper.class})
public interface ArchiveDocumentMapper extends CommonMapper {

    @Mapping(target = "familyRevisions", ignore = true)
    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "marriages", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    genealogy.visualizer.entity.ArchiveDocument toEntity(ArchiveDocument archiveDocument);

    @BeanMapping(ignoreUnmappedSourceProperties = {"familyRevisions", "christenings", "marriages", "deaths"})
    ArchiveDocument toDTO(genealogy.visualizer.entity.ArchiveDocument archiveDocument);

}
