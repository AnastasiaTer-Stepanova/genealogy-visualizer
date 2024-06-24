package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.entity.Locality;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EasyLocalityMapper extends EasyCommonMapper<EasyLocality, Locality> {

    @Mapping(target = "christenings", ignore = true)
    @Mapping(target = "deaths", ignore = true)
    @Mapping(target = "personsWithDeathLocality", ignore = true)
    @Mapping(target = "personsWithBirthLocality", ignore = true)
    @Mapping(target = "marriagesWithHusbandLocality", ignore = true)
    @Mapping(target = "marriagesWithWifeLocality", ignore = true)
    @Mapping(target = "witnesses", ignore = true)
    @Mapping(target = "godParents", ignore = true)
    Locality toEntity(EasyLocality locality);

    @BeanMapping(ignoreUnmappedSourceProperties = {"christenings", "deaths", "personsWithDeathLocality", "personsWithBirthLocality",
            "marriagesWithHusbandLocality", "marriagesWithWifeLocality", "witnesses", "godParents"})
    EasyLocality toDTO(Locality locality);

    List<EasyLocality> toDTOs(List<Locality> locality);

    List<Locality> toEntities(List<EasyLocality> locality);

}
