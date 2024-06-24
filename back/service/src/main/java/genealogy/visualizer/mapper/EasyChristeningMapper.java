package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.entity.Christening;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class})
public interface EasyChristeningMapper extends EasyCommonMapper<EasyChristening, Christening> {

    @Mapping(target = "locality", ignore = true)
    @Mapping(target = "godParents", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    Christening toEntity(EasyChristening christening);

    @BeanMapping(ignoreUnmappedSourceProperties = {"locality", "godParents", "person", "archiveDocument"})
    EasyChristening toDTO(genealogy.visualizer.entity.Christening christening);

    List<EasyChristening> toDTOs(List<Christening> christening);

    List<Christening> toEntities(List<EasyChristening> christening);
}
