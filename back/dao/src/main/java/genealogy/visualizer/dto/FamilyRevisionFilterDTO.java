package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.Sex;

public class FamilyRevisionFilterDTO {

    private Long archiveDocumentId;

    private Integer familyRevisionNumber;

    private FullNameFilterDTO fullName;

    private Sex sex;

    public Long getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(Long archiveDocumentId) {
        this.archiveDocumentId = archiveDocumentId;
    }

    public Integer getFamilyRevisionNumber() {
        return familyRevisionNumber;
    }

    public void setFamilyRevisionNumber(Integer familyRevisionNumber) {
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
}
