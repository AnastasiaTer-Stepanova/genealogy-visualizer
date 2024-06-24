package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.entity.Marriage;
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
public interface EasyMarriageMapper extends EasyCommonMapper<EasyMarriage, Marriage> {

    @Mapping(target = "witnesses", ignore = true)
    @Mapping(target = "wifeLocality", ignore = true)
    @Mapping(target = "husbandLocality", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    @Mapping(target = "persons", ignore = true)
    Marriage toEntity(EasyMarriage marriage);

    @BeanMapping(ignoreUnmappedSourceProperties = {"witnesses", "wifeLocality", "husbandLocality", "archiveDocument", "persons"})
    EasyMarriage toDTO(Marriage marriage);

    List<EasyMarriage> toDTOs(List<Marriage> marriage);

    List<Marriage> toEntities(List<EasyMarriage> christening);

}
