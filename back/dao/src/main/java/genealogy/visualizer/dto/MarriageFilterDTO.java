package genealogy.visualizer.dto;

public class MarriageFilterDTO {

    private Long archiveDocumentId;

    private Integer marriageYear;

    private String husbandName;

    private String wifeName;

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

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }

    public String getWifeName() {
        return wifeName;
    }

    public void setWifeName(String wifeName) {
        this.wifeName = wifeName;
    }
}
