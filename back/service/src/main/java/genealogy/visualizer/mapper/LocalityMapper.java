package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyChristeningMapper.class,
                EasyDeathMapper.class,
                EasyFamilyRevisionMapper.class,
                EasyMarriageMapper.class,
                EasyPersonMapper.class})
public interface LocalityMapper extends CommonMapper {

    @Mapping(target = "witnesses", ignore = true)
    @Mapping(target = "godParents", ignore = true)
    genealogy.visualizer.entity.Locality toEntity(Locality locality);

    @BeanMapping(ignoreUnmappedSourceProperties = {"witnesses", "godParents"})
    Locality toDTO(genealogy.visualizer.entity.Locality locality);

    genealogy.visualizer.dto.LocalityFilterDTO toFilter(LocalityFilter filter);

}
