package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyMarriage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {AgeMapper.class,
                FullNameMapper.class})
public interface EasyMarriageMapper extends CommonMapper {

    @Mapping(target = "witnesses", ignore = true)
    @Mapping(target = "wifeLocality", ignore = true)
    @Mapping(target = "husbandLocality", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    @Mapping(target = "persons", ignore = true)
    genealogy.visualizer.entity.Marriage toEntity(EasyMarriage marriage);

    @BeanMapping(ignoreUnmappedSourceProperties = {"witnesses", "wifeLocality", "husbandLocality", "archiveDocument", "persons"})
    EasyMarriage toDTO(genealogy.visualizer.entity.Marriage marriage);

    List<EasyMarriage> toDTOs(List<genealogy.visualizer.entity.Marriage> marriage);
}
