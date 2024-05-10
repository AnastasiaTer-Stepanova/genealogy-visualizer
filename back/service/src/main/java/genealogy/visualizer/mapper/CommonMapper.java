package genealogy.visualizer.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

public interface CommonMapper {

    String COMMA = ", ";

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
