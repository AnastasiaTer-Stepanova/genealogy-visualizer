package genealogy.visualizer.entity;

import genealogy.visualizer.converter.SexConverter;
import genealogy.visualizer.entity.enums.Sex;
import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.FullName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "FamilyRevision.full",
                attributeNodes = {
                        @NamedAttributeNode(value = "partner", subgraph = "familyRevisionGraph"),
                        @NamedAttributeNode(value = "person", subgraph = "personGraph"),
                        @NamedAttributeNode("archiveDocument"),
                        @NamedAttributeNode("anotherNames"),
                },
                subgraphs = {
                        @NamedSubgraph(name = "familyRevisionGraph", attributeNodes = {@NamedAttributeNode("anotherNames")}),
                        @NamedSubgraph(name = "personGraph", attributeNodes = {@NamedAttributeNode("christening"), @NamedAttributeNode("death")}),
                }
        ),
        @NamedEntityGraph(
                name = "FamilyRevision.withArchiveDocumentAndAnotherRevisionsInside",
                attributeNodes = {
                        @NamedAttributeNode(value ="archiveDocument", subgraph = "archiveDocumentGraph"),
                        @NamedAttributeNode("anotherNames"),
                },
                subgraphs = {
                        @NamedSubgraph(name = "archiveDocumentGraph", attributeNodes = {
                                @NamedAttributeNode("nextRevision"),
                                @NamedAttributeNode("previousRevisions"),
                        }),
                }
        ),

})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_FAMILY_REVISION_PARTNER_ID", columnNames = {"PARTNER_ID"}),
        },
        indexes = {
                @Index(name = "IDX_FAMILY_REVISION_NUMBER_ARCHIVE_DOCUMENT_ID", columnList = "FAMILY_REVISION_NUMBER, ARCHIVE_DOCUMENT_ID"),
        }
)
public class FamilyRevision implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FAMILY_REVISION")
    @SequenceGenerator(name = "SEQ_FAMILY_REVISION", sequenceName = "SEQ_FAMILY_REVISION", allocationSize = 20)
    @Comment("Идентификатор записи")
    private Long id;

    @Comment("Идентификатор записи партнера (мужа/жены) в рамках одной семьи")
    @OneToOne
    @JoinColumn(name = "PARTNER_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_FAMILY_REVISION_PARTNER"))
    private FamilyRevision partner;

    @Column(nullable = false)
    @Comment("Номер семьи в текущей ревизии")
    private Short familyRevisionNumber;

    @Comment("Номер семьи в следующей ревизии")
    private Short nextFamilyRevisionNumber;

    @Comment("Номер страницы в деле, на котором указана семья")
    private Short listNumber;

    @Column(nullable = false)
    @Comment("Является ли главой двора")
    private Boolean isHeadOfYard = false;

    @Column(nullable = false)
    @Comment("Фамилия в документе указана явно, true - явно, false - выведена")
    private Boolean isLastNameClearlyStated = true;

    @Embedded
    private FullName fullName;

    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "AGE_TYPE", length = 15))
    @Comment(value = "Тип возраста", on = "AGE_TYPE")
    private Age age;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "age", column = @Column(name = "AGE_IN_NEXT_REVISION", precision = 5, scale = 1)),
            @AttributeOverride(name = "type", column = @Column(name = "AGE_TYPE_IN_NEXT_REVISION", length = 15))
    })
    @Comment(value = "Возраст в последующей ревизии", on = "AGE_IN_NEXT_REVISION")
    @Comment(value = "Тип возраста в последующей ревизии", on = "AGE_TYPE_IN_NEXT_REVISION")
    private Age ageInNextRevision;

    @Comment("Комментарий о выбытии/смерти/рекрутинга в армию")
    private String departed;

    @Comment("Комментарий о том откуда прибыли")
    private String arrived;

    @Comment("Поколение в семье")
    @Column(length = 1, nullable = false)
    private Byte familyGeneration;

    @Comment("Комментарий")
    private String comment;

    @Column(length = 1)
    @Comment("Пол: Ж - женщина, М - мужчина")
    @Convert(converter = SexConverter.class)
    private Sex sex;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "RELATIVE_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "RELATIVE_NAME", length = 50)),
            @AttributeOverride(name = "surname", column = @Column(name = "RELATIVE_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "RELATIVE_STATUS"))
    })
    @Comment(value = "Имя родственника", on = "RELATIVE_LAST_NAME")
    @Comment(value = "Фамилия родственника", on = "RELATIVE_NAME")
    @Comment(value = "Отчество родственника", on = "RELATIVE_SURNAME")
    @Comment(value = "Статус родственника", on = "RELATIVE_STATUS")
    private FullName relative;

    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "ANOTHER_NAME_IN_REVISION",
            joinColumns = @JoinColumn(name = "FAMILY_REVISION_ID",
                    foreignKey = @ForeignKey(name = "FK_ANOTHER_NAME_IN_REVISION")),
            uniqueConstraints = @UniqueConstraint(name = "UK_ANOTHER_NAME_IN_REVISION",
                    columnNames = {"FAMILY_REVISION_ID", "ANOTHER_NAME"}))
    @Column(name = "ANOTHER_NAME", length = 50)
    private Set<String> anotherNames = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_DOCUMENT_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE_DOCUMENT"))
    private ArchiveDocument archiveDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_FAMILY_REVISION_PERSON"))
    private Person person;

    public FamilyRevision() {
    }

    public FamilyRevision(Long id, FamilyRevision partner, Short familyRevisionNumber, Short nextFamilyRevisionNumber, Short listNumber, Boolean isHeadOfYard, Boolean isLastNameClearlyStated, FullName fullName, Age age, Age ageInNextRevision, String departed, String arrived, Byte familyGeneration, String comment, Sex sex, FullName relative, Set<String> anotherNames, ArchiveDocument archiveDocument, Person person) {
        this.id = id;
        this.partner = partner;
        this.familyRevisionNumber = familyRevisionNumber;
        this.nextFamilyRevisionNumber = nextFamilyRevisionNumber;
        this.listNumber = listNumber;
        this.isHeadOfYard = isHeadOfYard;
        this.isLastNameClearlyStated = isLastNameClearlyStated;
        this.fullName = fullName;
        this.age = age;
        this.ageInNextRevision = ageInNextRevision;
        this.departed = departed;
        this.arrived = arrived;
        this.familyGeneration = familyGeneration;
        this.comment = comment;
        this.sex = sex;
        this.relative = relative;
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

    public FamilyRevision getPartner() {
        return partner;
    }

    public void setPartner(FamilyRevision partner) {
        this.partner = partner;
    }

    public Short getFamilyRevisionNumber() {
        return familyRevisionNumber;
    }

    public void setFamilyRevisionNumber(Short familyRevisionNumber) {
        this.familyRevisionNumber = familyRevisionNumber;
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

    public Boolean getLastNameClearlyStated() {
        return isLastNameClearlyStated;
    }

    public void setLastNameClearlyStated(Boolean lastNameClearlyStated) {
        isLastNameClearlyStated = lastNameClearlyStated;
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

    public Byte getFamilyGeneration() {
        return familyGeneration;
    }

    public void setFamilyGeneration(Byte familyGeneration) {
        this.familyGeneration = familyGeneration;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public FullName getRelative() {
        return relative;
    }

    public void setRelative(FullName relative) {
        this.relative = relative;
    }

    public Set<String> getAnotherNames() {
        return anotherNames;
    }

    public void setAnotherNames(Set<String> anotherNames) {
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

    public FamilyRevision clone() {
        return new FamilyRevision(id, partner, familyRevisionNumber, nextFamilyRevisionNumber, listNumber, isHeadOfYard,
                isLastNameClearlyStated, fullName, age, ageInNextRevision, departed, arrived, familyGeneration, comment,
                sex, relative, anotherNames, archiveDocument, person);
    }
}
