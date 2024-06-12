package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.GodParent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, EasyLocalityMapper.class})
public interface GodParentMapper extends CommonMapper {

    genealogy.visualizer.entity.model.GodParent toEntity(GodParent godParent);

    GodParent toDTO(genealogy.visualizer.entity.model.GodParent godParent);
}