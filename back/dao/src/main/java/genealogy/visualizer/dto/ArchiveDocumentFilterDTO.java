package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.ArchiveDocumentType;

public class ArchiveDocumentFilterDTO {

    private Long archiveId;

    private String name;

    private String abbreviation;

    private ArchiveDocumentType type;

    private Integer year;

    public Long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(Long archiveId) {
        this.archiveId = archiveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public ArchiveDocumentType getType() {
        return type;
    }

    public void setType(ArchiveDocumentType type) {
        this.type = type;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
