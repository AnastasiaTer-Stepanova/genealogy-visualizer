package genealogy.visualizer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

//TODO Раскомментить при реализации ChristeningController
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface ChristeningMapper extends CommonMapper {
//
//    @Mapping(target = "father", ignore = true)
//    @Mapping(target = "mother", ignore = true)
//    @Mapping(target = "comment", ignore = true)
//    @Mapping(target = "locality", ignore = true)
//    @Mapping(target = "godParents", ignore = true)
//    @Mapping(target = "person", ignore = true)
//    @Mapping(target = "archiveDocument", ignore = true)
//    genealogy.visualizer.entity.Christening toEntity(Christening christening);
//
//    @BeanMapping(ignoreUnmappedSourceProperties = {"father", "mother", "comment", "locality", "godParents", "person", "archiveDocument"})
//    Christening toDTO(genealogy.visualizer.entity.Christening christening);
}
