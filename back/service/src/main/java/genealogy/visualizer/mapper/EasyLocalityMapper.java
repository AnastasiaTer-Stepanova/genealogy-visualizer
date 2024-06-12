package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyLocality;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EasyLocalityMapper extends CommonMapper {

    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    @Mapping(target = "personsWithDeathLocality", ignore = true)
    @Mapping(target = "personsWithBirthLocality", ignore = true)
    @Mapping(target = "marriagesWithHusbandLocality", ignore = true)
    @Mapping(target = "marriagesWithWifeLocality", ignore = true)
    genealogy.visualizer.entity.Locality toEntity(EasyLocality locality);

    @BeanMapping(ignoreUnmappedSourceProperties = {"christenings", "deaths", "personsWithDeathLocality", "personsWithBirthLocality",
            "marriagesWithHusbandLocality", "marriagesWithWifeLocality"})
    EasyLocality toDTO(genealogy.visualizer.entity.Locality locality);

    List<EasyLocality> toDTOs(List<genealogy.visualizer.entity.Locality> locality);

    List<genealogy.visualizer.entity.Locality> toEntities(List<EasyLocality> locality);

}
