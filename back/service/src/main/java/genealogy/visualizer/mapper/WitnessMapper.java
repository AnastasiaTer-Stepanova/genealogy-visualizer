package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Witness;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, LocalityMapper.class})
public interface WitnessMapper extends CommonMapper {

    genealogy.visualizer.entity.model.Witness toEntity(Witness witness);

    Witness toDTO(genealogy.visualizer.entity.model.Witness witness);
}