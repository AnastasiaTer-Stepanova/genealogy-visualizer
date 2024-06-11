package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.FullName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface FullNameMapper extends CommonMapper {

    @Mapping(target = "status", source = "statuses", qualifiedByName = "fromListToStringWithComma")
    genealogy.visualizer.entity.model.FullName toEntity(FullName fullName);

    @Mapping(target = "statuses", source = "status", qualifiedByName = "fromStringWithCommaToList")
    FullName toDTO(genealogy.visualizer.entity.model.FullName fullName);
}
