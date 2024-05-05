package genealogy.visualizer.entity.enums;

import java.util.stream.Stream;

public enum WitnessType {
    HUSBAND("по жениху"),
    WIFE("по невесте");

    private final String name;

    WitnessType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WitnessType of(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(WitnessType.values())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
