package genealogy.visualizer.dto;

public class DeathFilterDTO {

    private Long archiveDocumentId;

    private Integer deathYear;

    private String name;

    public Long getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(Long archiveDocumentId) {
        this.archiveDocumentId = archiveDocumentId;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
