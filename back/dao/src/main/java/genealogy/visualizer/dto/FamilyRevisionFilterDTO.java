package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.Sex;

public class FamilyRevisionFilterDTO {

    private Long archiveDocumentId;

    private Short familyRevisionNumber;

    private FullNameFilterDTO fullName;

    private Sex sex;

    private Boolean isFindWithHavePerson;

    public FamilyRevisionFilterDTO(Long archiveDocumentId, Short familyRevisionNumber, FullNameFilterDTO fullName, Sex sex, Boolean isFindWithHavePerson) {
        this.archiveDocumentId = archiveDocumentId;
        this.familyRevisionNumber = familyRevisionNumber;
        this.fullName = fullName;
        this.sex = sex;
        this.isFindWithHavePerson = isFindWithHavePerson;
    }

    public Long getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(Long archiveDocumentId) {
        this.archiveDocumentId = archiveDocumentId;
    }

    public Short getFamilyRevisionNumber() {
        return familyRevisionNumber;
    }

    public void setFamilyRevisionNumber(Short familyRevisionNumber) {
        this.familyRevisionNumber = familyRevisionNumber;
    }

    public FullNameFilterDTO getFullName() {
        return fullName;
    }

    public void setFullName(FullNameFilterDTO fullName) {
        this.fullName = fullName;
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
