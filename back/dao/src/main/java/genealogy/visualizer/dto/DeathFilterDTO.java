package genealogy.visualizer.dto;

public class DeathFilterDTO {

    private Long archiveDocumentId;

    private Integer deathYear;

    private FullNameFilterDTO fullName;

    private Boolean isFindWithHavePerson;

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

    public FullNameFilterDTO getFullName() {
        return fullName;
    }

    public void setFullName(FullNameFilterDTO fullName) {
        this.fullName = fullName;
    }

    public Boolean getFindWithHavePerson() {
        return isFindWithHavePerson;
    }

    public void setFindWithHavePerson(Boolean findWithHavePerson) {
        isFindWithHavePerson = findWithHavePerson;
    }
}
