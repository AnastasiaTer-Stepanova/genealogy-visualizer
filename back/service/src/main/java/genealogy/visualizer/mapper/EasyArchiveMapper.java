package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyArchive;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EasyArchiveMapper extends CommonMapper {

    @Mapping(target = "archiveDocuments", ignore = true)
    genealogy.visualizer.entity.Archive toEntity(EasyArchive archive);

    @BeanMapping(ignoreUnmappedSourceProperties = {"archiveDocuments"})
    EasyArchive toDTO(genealogy.visualizer.entity.Archive archive);
}
