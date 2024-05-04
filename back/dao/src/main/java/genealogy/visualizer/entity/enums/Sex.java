package genealogy.visualizer.entity.enums;

import java.util.stream.Stream;

public enum Sex {
    MALE("М"),
    FEMALE("Ж");

    private final String name;

    Sex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Sex of(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(Sex.values())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
