package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.EasyArchiveDocument;
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
    genealogy.visualizer.entity.ArchiveDocument toEntity(ArchiveDocument archiveDocument);

    @Mapping(target = "nextRevision", expression = "java(this.toEasyDTO(archiveDocument.getNextRevision()))")
    @Mapping(target = "previousRevisions", expression = "java(this.toEasyDTOs(archiveDocument.getPreviousRevisions()))")
    ArchiveDocument toDTO(genealogy.visualizer.entity.ArchiveDocument archiveDocument);

    EasyArchiveDocument toEasyDTO(genealogy.visualizer.entity.ArchiveDocument archiveDocument);

    List<EasyArchiveDocument> toEasyDTOs(List<genealogy.visualizer.entity.ArchiveDocument> archiveDocument);

    @Mapping(target = "familyRevisions", ignore = true)
    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "marriages", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    @Mapping(target = "previousRevisions", ignore = true)
    @Mapping(target = "nextRevision", ignore = true)
    genealogy.visualizer.entity.ArchiveDocument fromEasyDTO(EasyArchiveDocument easyArchiveDocument);

    List<ArchiveDocument> toDTO(List<genealogy.visualizer.entity.ArchiveDocument> archiveDocument);

}
