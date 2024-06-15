package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.Sex;

public class ChristeningFilterDTO {

    private Long archiveDocumentId;

    private Short christeningYear;

    private String name;

    private Sex sex;

    private Boolean isFindWithHavePerson;

    public Long getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(Long archiveDocumentId) {
        this.archiveDocumentId = archiveDocumentId;
    }

    public Short getChristeningYear() {
        return christeningYear;
    }

    public void setChristeningYear(Short christeningYear) {
        this.christeningYear = christeningYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Boolean getFindWithHavePerson() {
        return isFindWithHavePerson;
    }

    public void setFindWithHavePerson(Boolean findWithHavePerson) {
        isFindWithHavePerson = findWithHavePerson;
    }
}
