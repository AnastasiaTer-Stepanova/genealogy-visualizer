package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyChristening;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, GodParentMapper.class})
public interface EasyChristeningMapper extends CommonMapper {

    @Mapping(target = "locality", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    genealogy.visualizer.entity.Christening toEntity(EasyChristening christening);

    @BeanMapping(ignoreUnmappedSourceProperties = {"locality", "person", "archiveDocument"})
    EasyChristening toDTO(genealogy.visualizer.entity.Christening christening);

    List<EasyChristening> toDTOs(List<genealogy.visualizer.entity.Christening> christening);

    List<genealogy.visualizer.entity.Christening> toEntities(List<EasyChristening> christening);
}
