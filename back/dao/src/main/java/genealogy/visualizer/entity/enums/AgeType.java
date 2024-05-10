package genealogy.visualizer.entity.enums;

import java.util.stream.Stream;

public enum AgeType {
    NEWBORN("новорожденный"),
    DAY("дни"),
    WEEK("недели"),
    MONTH("месяцы"),
    YEAR("года");

    private final String name;

    AgeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AgeType of(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(AgeType.values())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
