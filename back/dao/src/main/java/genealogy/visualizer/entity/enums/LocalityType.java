package genealogy.visualizer.entity.enums;

import java.util.stream.Stream;

public enum LocalityType {
    TOWN("город"),
    VILLAGE("село"),
    HAMLET("деревня");

    private final String name;

    LocalityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LocalityType of(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(LocalityType.values())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
