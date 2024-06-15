package genealogy.visualizer.entity;

import genealogy.visualizer.converter.SexConverter;
import genealogy.visualizer.entity.enums.Sex;
import genealogy.visualizer.entity.model.FullName;
import genealogy.visualizer.entity.model.GodParent;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_CHRISTENING_PERSON_ID", columnNames = {"PERSON_ID"}))
public class Christening implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CHRISTENING")
    @SequenceGenerator(name = "SEQ_CHRISTENING", sequenceName = "SEQ_CHRISTENING", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(columnDefinition = "DATE", nullable = false)
    @Comment("Дата рождения")
    private LocalDate birthDate;

    @Column(columnDefinition = "DATE")
    @Comment("Дата крещения")
    private LocalDate christeningDate;

    @Column(length = 1, nullable = false)
    @Comment("Пол: Ж - женщина, М - мужчина")
    @Convert(converter = SexConverter.class)
    private Sex sex;

    @Column(length = 50, nullable = false)
    @Comment("Имя при крещении")
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "FATHER_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "FATHER_NAME", length = 50)),
            @AttributeOverride(name = "surname", column = @Column(name = "FATHER_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "FATHER_STATUS"))
    })
    @Comment(value = "Имя отца", on = "FATHER_LAST_NAME")
    @Comment(value = "Фамилия отца", on = "FATHER_NAME")
    @Comment(value = "Отчество отца", on = "FATHER_SURNAME")
    @Comment(value = "Статус отца", on = "FATHER_STATUS")
    private FullName father;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "MOTHER_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "MOTHER_NAME", length = 50)),
            @AttributeOverride(name = "surname", column = @Column(name = "MOTHER_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "MOTHER_STATUS"))
    })
    @Comment(value = "Имя матери", on = "MOTHER_LAST_NAME")
    @Comment(value = "Фамилия матери", on = "MOTHER_NAME")
    @Comment(value = "Отчество матери", on = "MOTHER_SURNAME")
    @Comment(value = "Статус матери", on = "MOTHER_STATUS")
    private FullName mother;

    @Comment("Комментарий")
    private String comment;

    @Comment("Законнорожденность: true - законнорожденный, false - незаконнорожденные")
    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean legitimacy;

    @Comment("Город, село, деревня и т.д.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_LOCALITY"))
    private Locality locality;

    @ElementCollection(targetClass = GodParent.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "GOD_PARENT",
            joinColumns = @JoinColumn(name = "CHRISTENING_ID",
                    foreignKey = @ForeignKey(name = "FK_GOD_PARENT")))
    @AssociationOverrides({
            @AssociationOverride(name = "locality",
                    joinColumns = @JoinColumn(name = "LOCALITY_ID"),
                    foreignKey = @ForeignKey(name = "FK_GOD_PARENT_LOCALITY"))
    })
    private List<GodParent> godParents = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CHRISTENING_PERSON"),
            unique = true)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_DOCUMENT_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE_DOCUMENT"))
    private ArchiveDocument archiveDocument;

    public Christening() {
    }

    public Christening(Long id, LocalDate birthDate, LocalDate christeningDate, Sex sex, String name, FullName father, FullName mother, String comment, Boolean legitimacy, Locality locality, List<GodParent> godParents, Person person, ArchiveDocument archiveDocument) {
        this.id = id;
        this.birthDate = birthDate;
        this.christeningDate = christeningDate;
        this.sex = sex;
        this.name = name;
        this.father = father;
        this.mother = mother;
        this.comment = comment;
        this.legitimacy = legitimacy;
        this.locality = locality;
        this.godParents = godParents;
        this.person = person;
        this.archiveDocument = archiveDocument;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getChristeningDate() {
        return christeningDate;
    }

    public void setChristeningDate(LocalDate christeningDate) {
        this.christeningDate = christeningDate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FullName getFather() {
        return father;
    }

    public void setFather(FullName father) {
        this.father = father;
    }

    public FullName getMother() {
        return mother;
    }

    public void setMother(FullName mother) {
        this.mother = mother;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getLegitimacy() {
        return legitimacy;
    }

    public void setLegitimacy(Boolean legitimacy) {
        this.legitimacy = legitimacy;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public List<GodParent> getGodParents() {
        return godParents;
    }

    public void setGodParents(List<GodParent> godParents) {
        this.godParents = godParents;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ArchiveDocument getArchiveDocument() {
        return archiveDocument;
    }

    public void setArchiveDocument(ArchiveDocument archiveDocument) {
        this.archiveDocument = archiveDocument;
    }

    public Christening clone() {
        return new Christening(id, birthDate, christeningDate, sex, name, father, mother, comment, legitimacy, locality,
                godParents, person, archiveDocument);
    }
}
