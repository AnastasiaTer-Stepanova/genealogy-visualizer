package genealogy.visualizer.entity.enums;

import java.util.stream.Stream;

public enum DateRangeType {

    EXACTLY("ровно"),
    BEFORE("до"),
    AFTER("после");

    private final String name;

    DateRangeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DateRangeType of(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(DateRangeType.values())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
