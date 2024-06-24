package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.entity.Archive;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EasyArchiveMapper extends EasyCommonMapper<EasyArchive, Archive> {

    @Mapping(target = "archiveDocuments", ignore = true)
    Archive toEntity(EasyArchive archive);

    @BeanMapping(ignoreUnmappedSourceProperties = {"archiveDocuments"})
    EasyArchive toDTO(Archive archive);

    List<EasyArchive> toDTOs(List<Archive> archive);
}
