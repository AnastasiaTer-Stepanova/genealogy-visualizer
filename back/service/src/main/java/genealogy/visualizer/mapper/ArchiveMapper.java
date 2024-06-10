package genealogy.visualizer.mapper;


import genealogy.visualizer.api.model.Archive;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyArchiveDocumentMapper.class})
public interface ArchiveMapper extends CommonMapper {

    genealogy.visualizer.entity.Archive toEntity(Archive archive);

    Archive toDTO(genealogy.visualizer.entity.Archive archive);
}
