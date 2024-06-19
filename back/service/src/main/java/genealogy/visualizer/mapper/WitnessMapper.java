package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Witness;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, EasyLocalityMapper.class})
public interface WitnessMapper extends CommonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "marriage", ignore = true)
    genealogy.visualizer.entity.Witness toEntity(Witness witness);

    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "marriage"})
    Witness toDTO(genealogy.visualizer.entity.Witness witness);
}