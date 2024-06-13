package genealogy.visualizer.dto;

public class MarriageFilterDTO {

    private Long archiveDocumentId;

    private Integer marriageYear;

    private FullNameFilterDTO wifeFullName;

    private FullNameFilterDTO husbandFullName;

    public Long getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(Long archiveDocumentId) {
        this.archiveDocumentId = archiveDocumentId;
    }

    public Integer getMarriageYear() {
        return marriageYear;
    }

    public void setMarriageYear(Integer marriageYear) {
        this.marriageYear = marriageYear;
    }

    public FullNameFilterDTO getWifeFullName() {
        return wifeFullName;
    }

    public void setWifeFullName(FullNameFilterDTO wifeFullName) {
        this.wifeFullName = wifeFullName;
    }

    public FullNameFilterDTO getHusbandFullName() {
        return husbandFullName;
    }

    public void setHusbandFullName(FullNameFilterDTO husbandFullName) {
        this.husbandFullName = husbandFullName;
    }
}
