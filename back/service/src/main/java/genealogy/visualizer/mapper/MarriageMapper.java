package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Marriage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class})
public interface MarriageMapper extends CommonMapper {

    @Mapping(target = "husbandLocality", ignore = true)
    @Mapping(target = "husbandsFather", ignore = true)
    @Mapping(target = "husbandAge", ignore = true)
    @Mapping(target = "wifeLocality", ignore = true)
    @Mapping(target = "wifesFather", ignore = true)
    @Mapping(target = "wifeAge", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "witnesses", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    @Mapping(target = "persons", ignore = true)
    genealogy.visualizer.entity.Marriage toEntity(Marriage marriage);

    @BeanMapping(ignoreUnmappedSourceProperties = {"husbandLocality", "husbandsFather", "husbandAge", "wifeLocality",
            "wifesFather", "wifeAge", "comment", "witnesses", "archiveDocument", "persons"})
    Marriage toDTO(genealogy.visualizer.entity.Marriage marriage);
}
