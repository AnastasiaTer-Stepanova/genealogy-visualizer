package genealogy.visualizer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

//TODO Раскомментить при реализации DeathController
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class})
public interface DeathMapper extends CommonMapper {

//    @Mapping(target = "relative", ignore = true)
//    @Mapping(target = "age", ignore = true)
//    @Mapping(target = "cause", ignore = true)
//    @Mapping(target = "burialPlace", ignore = true)
//    @Mapping(target = "comment", ignore = true)
//    @Mapping(target = "locality", ignore = true)
//    @Mapping(target = "archiveDocument", ignore = true)
//    @Mapping(target = "person", ignore = true)
//    genealogy.visualizer.entity.Death toEntity(Death death);
//
//    @BeanMapping(ignoreUnmappedSourceProperties = {"relative", "age", "cause", "burialPlace", "comment", "locality", "archiveDocument", "person"})
//    Death toDTO(genealogy.visualizer.entity.Death death);
}
