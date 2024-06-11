package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyChristening;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EasyChristeningMapper extends CommonMapper {

    @Mapping(target = "father", ignore = true)
    @Mapping(target = "mother", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "locality", ignore = true)
    @Mapping(target = "godParents", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    genealogy.visualizer.entity.Christening toEntity(EasyChristening christening);

    @BeanMapping(ignoreUnmappedSourceProperties = {"father", "mother", "comment", "locality", "godParents", "person", "archiveDocument"})
    EasyChristening toDTO(genealogy.visualizer.entity.Christening christening);
}
