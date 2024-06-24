package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.FullName;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface FullNameMapper {

    String COMMA = ", ";

    @Mapping(target = "status", source = "statuses", qualifiedByName = "fromListToStringWithComma")
    genealogy.visualizer.entity.model.FullName toEntity(FullName fullName);

    @Mapping(target = "statuses", source = "status", qualifiedByName = "fromStringWithCommaToList")
    FullName toDTO(genealogy.visualizer.entity.model.FullName fullName);

    @Named("fromStringWithCommaToList")
    default List<String> fromStringWithCommaToList(String string) {
        if (string == null || string.isEmpty()) {
            return Collections.emptyList();
        }
        return List.of(string.split(COMMA));
    }

    @Named("fromListToStringWithComma")
    default String fromListToStringWithComma(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return StringUtils.join(list, COMMA);
    }
}
