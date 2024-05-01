package genealogy.visualizer.entity;

import java.util.stream.Stream;

public enum ArchiveDocumentType {
    RL("РС", "Ревизская сказка"), //Revision legend
    PR("РС", "Ревизская сказка"), //Parish register
    CS("ИВ", "Исповедная ведомость"), //confession sheet
    ANOTHER("Другое", "Другое");

    private final String name;

    private final String title;

    ArchiveDocumentType(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public static ArchiveDocumentType of(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(ArchiveDocumentType.values())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
