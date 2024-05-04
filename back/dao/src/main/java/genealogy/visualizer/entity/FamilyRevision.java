package genealogy.visualizer.entity;

import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.AnotherNameInRevision;
import genealogy.visualizer.entity.model.FullName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_FAMILY_REVISION_PERSON_ID", columnNames = {"PERSON_ID"}))
public class FamilyRevision implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FAMILY_REVISION")
    @SequenceGenerator(name = "SEQ_FAMILY_REVISION", sequenceName = "SEQ_FAMILY_REVISION", allocationSize = 20)
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

    @ElementCollection(targetClass = AnotherNameInRevision.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ANOTHER_NAME_IN_REVISION",
            joinColumns = @JoinColumn(name = "FAMILY_REVISION_ID",
                    foreignKey = @ForeignKey(name = "FK_ANOTHER_NAME_IN_REVISION")),
            uniqueConstraints = @UniqueConstraint(name = "UK_ANOTHER_NAME_IN_REVISION",
                    columnNames = {"FAMILY_REVISION_ID", "NUMBER"}))
    private List<AnotherNameInRevision> anotherNames = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_DOCUMENT_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE_DOCUMENT"))
    private ArchiveDocument archiveDocument;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_FAMILY_REVISION_PERSON"),
            unique = true)
    private Person person;

    public FamilyRevision() {
    }

    public FamilyRevision(Long id, Short familyRevisionNumber, Short previousFamilyRevisionNumber, Short nextFamilyRevisionNumber, Short listNumber, Boolean isHeadOfYard, FullName fullName, Age age, Age ageInPreviousRevision, Age ageInNextRevision, String departed, String arrived, List<AnotherNameInRevision> anotherNames, ArchiveDocument archiveDocument, Person person) {
        this.id = id;
        this.familyRevisionNumber = familyRevisionNumber;
        this.previousFamilyRevisionNumber = previousFamilyRevisionNumber;
        this.nextFamilyRevisionNumber = nextFamilyRevisionNumber;
        this.listNumber = listNumber;
        this.isHeadOfYard = isHeadOfYard;
        this.fullName = fullName;
        this.age = age;
        this.ageInPreviousRevision = ageInPreviousRevision;
        this.ageInNextRevision = ageInNextRevision;
        this.departed = departed;
        this.arrived = arrived;
        this.anotherNames = anotherNames;
        this.archiveDocument = archiveDocument;
        this.person = person;
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
        if (anotherNames == null) {
            return new ArrayList<>();
        }
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
