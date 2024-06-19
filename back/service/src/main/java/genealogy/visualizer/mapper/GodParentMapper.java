package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.GodParent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, EasyLocalityMapper.class})
public interface GodParentMapper extends CommonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "christening", ignore = true)
    genealogy.visualizer.entity.GodParent toEntity(GodParent godParent);

    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "christening"})
    GodParent toDTO(genealogy.visualizer.entity.GodParent godParent);
}