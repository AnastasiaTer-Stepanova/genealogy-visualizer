package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Locality;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface LocalityMapper extends CommonMapper {

    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    @Mapping(target = "personsWithDeathLocality", ignore = true)
    @Mapping(target = "personsWithBirthLocality", ignore = true)
    genealogy.visualizer.entity.Locality toEntity(Locality locality);

    @BeanMapping(ignoreUnmappedSourceProperties = {"christenings", "deaths", "personsWithDeathLocality", "personsWithBirthLocality"})
    Locality toDTO(genealogy.visualizer.entity.Locality locality);

}
