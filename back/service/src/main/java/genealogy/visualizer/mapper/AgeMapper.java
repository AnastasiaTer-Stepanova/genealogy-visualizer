package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Age;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface AgeMapper extends CommonMapper {

    genealogy.visualizer.entity.model.Age toEntity(Age age);

    Age toEntity(genealogy.visualizer.entity.model.Age age);

}
