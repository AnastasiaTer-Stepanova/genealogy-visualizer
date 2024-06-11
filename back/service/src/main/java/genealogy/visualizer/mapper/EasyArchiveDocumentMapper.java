package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyArchiveDocument;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EasyArchiveDocumentMapper extends CommonMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "familyRevisions",
            "christenings",
            "marriages",
            "deaths",
            "previousRevisions",
            "nextRevision",
            "archive"})
    EasyArchiveDocument toDTO(genealogy.visualizer.entity.ArchiveDocument archiveDocument);

    List<EasyArchiveDocument> toDTOs(List<genealogy.visualizer.entity.ArchiveDocument> archiveDocument);

    @Mapping(target = "familyRevisions", ignore = true)
    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "marriages", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    @Mapping(target = "previousRevisions", ignore = true)
    @Mapping(target = "nextRevision", ignore = true)
    @Mapping(target = "archive", ignore = true)
    genealogy.visualizer.entity.ArchiveDocument toEntity(EasyArchiveDocument easyArchiveDocument);

}
