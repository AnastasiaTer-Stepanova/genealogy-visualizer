package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.Sex;

import java.util.ArrayList;
import java.util.List;

public class FamilyRevisionFilterDTO extends EntityFilter {

    private Long archiveDocumentId;

    private Short familyRevisionNumber;

    private FullNameFilterDTO fullName;

    private Sex sex;

    private Boolean isFindWithHavePerson;

    private List<String> graphs = new ArrayList<>();

    public FamilyRevisionFilterDTO() {
    }

    public FamilyRevisionFilterDTO(Long archiveDocumentId, Short familyRevisionNumber, FullNameFilterDTO fullName, Sex sex, Boolean isFindWithHavePerson, List<String> graphs) {
        this.archiveDocumentId = archiveDocumentId;
        this.familyRevisionNumber = familyRevisionNumber;
        this.fullName = fullName;
        this.sex = sex;
        this.isFindWithHavePerson = isFindWithHavePerson;
        this.graphs = graphs;
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

    public List<String> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<String> graphs) {
        this.graphs = graphs;
    }
}
