package genealogy.visualizer.entity.enums;

import java.util.stream.Stream;

public enum ArchiveDocumentType {
    RL("РС", "Ревизская сказка"), //Revision legend
    PR_CHR("МК крещение", "Метрическая книга о крещении"), //Parish register christening
    PR_MRG("МК брак", "Метрическая книга о браке"), //Parish register marriage
    PR_DTH("МК смерть", "Метрическая книга о смерти"), //Parish register death
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
