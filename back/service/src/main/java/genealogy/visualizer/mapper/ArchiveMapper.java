package genealogy.visualizer.mapper;


import genealogy.visualizer.api.model.Archive;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR,
uses = {ArchiveDocumentMapper.class, })
public interface ArchiveMapper extends CommonMapper {

    @Mapping(target = "archiveDocuments", ignore = true)
    genealogy.visualizer.entity.Archive toEntity(Archive archive);

    @BeanMapping(ignoreUnmappedSourceProperties = {"archiveDocuments"})
    Archive toDTO(genealogy.visualizer.entity.Archive archive);
}
