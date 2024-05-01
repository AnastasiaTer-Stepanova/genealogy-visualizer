package genealogy.visualizer.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FamilyRevision implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAMILY_REVISION_SEQ")
    @SequenceGenerator(name = "FAMILY_REVISION_SEQ", sequenceName = "FAMILY_REVISION_SEQ", allocationSize = 20)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(nullable = false)
    @Comment("Номер семьи в текущей ревизии")
    private Short familyRevisionNumber;

    @Comment("Номер семьи в предыдущей ревизии")
    private Short previousFamilyRevisionNumber;

    @Comment("Номер семьи в следующей ревизии")
    private Short nextFamilyRevisionNumber;

    @Comment("Номер страницы в деле, на котором указана семья")
    private Short listNumber;

    @Column(nullable = false)
    @Comment("Является ли главой двора")
    private Boolean isHeadOfYard = false;

    @Comment("Статус человека")
    private String status;

    @Embedded
    private FullName fullName;

    @Embedded
    private Age age;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "age", column = @Column(name = "AGE_IN_PREVIOUS_REVISION")),
            @AttributeOverride(name = "ageType", column = @Column(name = "AGE_TYPE_IN_PREVIOUS_REVISION"))
    })
    @Comment(value = "Возраст в прежней ревизии", on = "AGE_IN_PREVIOUS_REVISION")
    @Comment(value = "Тип возраста в прежней ревизии", on = "AGE_TYPE_IN_PREVIOUS_REVISION")
    private Age ageInPreviousRevision;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "age", column = @Column(name = "AGE_IN_NEXT_REVISION")),
            @AttributeOverride(name = "ageType", column = @Column(name = "AGE_TYPE_IN_NEXT_REVISION"))
    })
    @Comment(value = "Возраст в последующей ревизии", on = "AGE_IN_NEXT_REVISION")
    @Comment(value = "Тип возраста в последующей ревизии", on = "AGE_TYPE_IN_NEXT_REVISION")
    private Age ageInNextRevision;

    @Comment("Комментарий о выбытии/смерти/рекрутинга в армию")
    private String departed;

    @Comment("Комментарий о том откуда прибыли")
    private String arrived;

    @OneToMany(mappedBy = "revisionPerson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnotherNameInRevision> anotherNames = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_DOCUMENT_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE_DOCUMENT"))
    private ArchiveDocument archiveDocument;

    public FamilyRevision() {
    }

    public FamilyRevision(Long id, Short familyRevisionNumber, Short previousFamilyRevisionNumber, Short nextFamilyRevisionNumber, Short listNumber, Boolean isHeadOfYard, String status, FullName fullName, Age age, Age ageInPreviousRevision, Age ageInNextRevision, String departed, String arrived, List<AnotherNameInRevision> anotherNames, ArchiveDocument archiveDocument) {
        this.id = id;
        this.familyRevisionNumber = familyRevisionNumber;
        this.previousFamilyRevisionNumber = previousFamilyRevisionNumber;
        this.nextFamilyRevisionNumber = nextFamilyRevisionNumber;
        this.listNumber = listNumber;
        this.isHeadOfYard = isHeadOfYard;
        this.status = status;
        this.fullName = fullName;
        this.age = age;
        this.ageInPreviousRevision = ageInPreviousRevision;
        this.ageInNextRevision = ageInNextRevision;
        this.departed = departed;
        this.arrived = arrived;
        this.anotherNames = anotherNames;
        this.archiveDocument = archiveDocument;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getFamilyRevisionNumber() {
        return familyRevisionNumber;
    }

    public void setFamilyRevisionNumber(Short familyRevisionNumber) {
        this.familyRevisionNumber = familyRevisionNumber;
    }

    public Short getPreviousFamilyRevisionNumber() {
        return previousFamilyRevisionNumber;
    }

    public void setPreviousFamilyRevisionNumber(Short previousFamilyRevisionNumber) {
        this.previousFamilyRevisionNumber = previousFamilyRevisionNumber;
    }

    public Short getNextFamilyRevisionNumber() {
        return nextFamilyRevisionNumber;
    }

    public void setNextFamilyRevisionNumber(Short nextFamilyRevisionNumber) {
        this.nextFamilyRevisionNumber = nextFamilyRevisionNumber;
    }

    public Short getListNumber() {
        return listNumber;
    }

    public void setListNumber(Short listNumber) {
        this.listNumber = listNumber;
    }

    public Boolean getHeadOfYard() {
        return isHeadOfYard;
    }

    public void setHeadOfYard(Boolean headOfYard) {
        isHeadOfYard = headOfYard;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FullName getFullName() {
        return fullName;
    }

    public void setFullName(FullName fullName) {
        this.fullName = fullName;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Age getAgeInPreviousRevision() {
        return ageInPreviousRevision;
    }

    public void setAgeInPreviousRevision(Age ageInPreviousRevision) {
        this.ageInPreviousRevision = ageInPreviousRevision;
    }

    public Age getAgeInNextRevision() {
        return ageInNextRevision;
    }

    public void setAgeInNextRevision(Age ageInNextRevision) {
        this.ageInNextRevision = ageInNextRevision;
    }

    public String getDeparted() {
        return departed;
    }

    public void setDeparted(String departed) {
        this.departed = departed;
    }

    public String getArrived() {
        return arrived;
    }

    public void setArrived(String arrived) {
        this.arrived = arrived;
    }

    public List<AnotherNameInRevision> getAnotherNames() {
        return anotherNames;
    }

    public void setAnotherNames(List<AnotherNameInRevision> anotherNames) {
        this.anotherNames = anotherNames;
    }

    public ArchiveDocument getArchiveDocument() {
        return archiveDocument;
    }

    public void setArchiveDocument(ArchiveDocument archiveDocument) {
        this.archiveDocument = archiveDocument;
    }
}
