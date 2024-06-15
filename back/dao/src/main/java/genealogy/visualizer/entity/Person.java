package genealogy.visualizer.entity;

import genealogy.visualizer.converter.SexConverter;
import genealogy.visualizer.entity.enums.Sex;
import genealogy.visualizer.entity.model.DateInfo;
import genealogy.visualizer.entity.model.FullName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PERSON")
    @SequenceGenerator(name = "SEQ_PERSON", sequenceName = "SEQ_PERSON", allocationSize = 5)
    @Comment("Идентификатор записи")
    private Long id;

    @Embedded
    private FullName fullName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "date", column = @Column(name = "BIRTH_DATE", length = 10)),
            @AttributeOverride(name = "dateRangeType", column = @Column(name = "BIRTH_DATE_RANGE_TYPE", length = 20))
    })
    @Comment(value = "Дата рождения", on = "BIRTH_DATE")
    @Comment(value = "Тип диапазона даты рождения", on = "BIRTH_DATE_RANGE_TYPE")
    private DateInfo birthDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "date", column = @Column(name = "DEATH_DATE", length = 10)),
            @AttributeOverride(name = "dateRangeType", column = @Column(name = "DEATH_DATE_RANGE_TYPE", length = 20))
    })
    @Comment(value = "Дата смерти", on = "DEATH_DATE")
    @Comment(value = "Тип диапазона даты смерти", on = "DEATH_DATE_RANGE_TYPE")
    private DateInfo deathDate;

    @ManyToOne
    @JoinColumn(name = "BIRTH_LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_PERSON_BIRTH_LOCALITY"))
    private Locality birthLocality;

    @ManyToOne
    @JoinColumn(name = "DEATH_LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_PERSON_DEATH_LOCALITY"))
    private Locality deathLocality;

    @Column(length = 1)
    @Comment("Пол: Ж - женщина, М - мужчина")
    @Convert(converter = SexConverter.class)
    private Sex sex;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PERSON_PARTNER",
            joinColumns = @JoinColumn(name = "PERSON_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_PARTNER_ID_PERSON_ID")),
            uniqueConstraints = @UniqueConstraint(name = "UK_PARTNER_ID_PERSON_ID",
                    columnNames = {"PARTNER_ID", "PERSON_ID"}),
            inverseJoinColumns = @JoinColumn(name = "PARTNER_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_PERSON_ID_PARTNER_ID")))
    private List<Person> partners = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PERSON_PARENT",
            joinColumns = @JoinColumn(name = "PARENT_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_PARENT_ID_PERSON_ID")),
            uniqueConstraints = @UniqueConstraint(name = "UK_PARENT_ID_PERSON_ID",
                    columnNames = {"PARENT_ID", "PERSON_ID"}),
            inverseJoinColumns = @JoinColumn(name = "PERSON_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_PERSON_ID_PARENT_ID")))
    private List<Person> children = new ArrayList<>();

    @ManyToMany(mappedBy = "children", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Person> parents = new ArrayList<>();

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    private Christening christening;

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    private Death death;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private List<FamilyRevision> revisions = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PERSON_MARRIAGE",
            joinColumns = @JoinColumn(name = "PERSON_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_PERSON_ID_MARRIAGE_ID")),
            uniqueConstraints = @UniqueConstraint(name = "UK_MARRIAGE_ID_PERSON_ID",
                    columnNames = {"PERSON_ID", "MARRIAGE_ID"}),
            inverseJoinColumns = @JoinColumn(name = "MARRIAGE_ID", referencedColumnName = "ID"))
    private List<Marriage> marriages = new ArrayList<>();

    public Person() {
    }

    public Person(FullName fullName, DateInfo birthDate, DateInfo deathDate, Locality birthLocality, Locality deathLocality, Sex sex) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.birthLocality = birthLocality;
        this.deathLocality = deathLocality;
        this.sex = sex;
    }

    public Person(Long id, FullName fullName, DateInfo birthDate, DateInfo deathDate, Locality birthLocality, Locality deathLocality, Sex sex, List<Person> partners, List<Person> children, List<Person> parents, Christening christening, Death death, List<FamilyRevision> revisions, List<Marriage> marriages) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.birthLocality = birthLocality;
        this.deathLocality = deathLocality;
        this.sex = sex;
        this.partners = partners;
        this.children = children;
        this.parents = parents;
        this.christening = christening;
        this.death = death;
        this.revisions = revisions;
        this.marriages = marriages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FullName getFullName() {
        return fullName;
    }

    public void setFullName(FullName fullName) {
        this.fullName = fullName;
    }

    public DateInfo getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(DateInfo birthDate) {
        this.birthDate = birthDate;
    }

    public DateInfo getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(DateInfo deathDate) {
        this.deathDate = deathDate;
    }

    public Locality getBirthLocality() {
        return birthLocality;
    }

    public void setBirthLocality(Locality birthLocality) {
        this.birthLocality = birthLocality;
    }

    public Locality getDeathLocality() {
        return deathLocality;
    }

    public void setDeathLocality(Locality deathLocality) {
        this.deathLocality = deathLocality;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<Person> getPartners() {
        return partners;
    }

    public void setPartners(List<Person> partners) {
        this.partners = partners;
    }

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public List<Person> getParents() {
        return parents;
    }

    public void setParents(List<Person> parents) {
        this.parents = parents;
    }

    public Christening getChristening() {
        return christening;
    }

    public void setChristening(Christening christening) {
        this.christening = christening;
    }

    public Death getDeath() {
        return death;
    }

    public void setDeath(Death death) {
        this.death = death;
    }

    public List<FamilyRevision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<FamilyRevision> revisions) {
        this.revisions = revisions;
    }

    public List<Marriage> getMarriages() {
        return marriages;
    }

    public void setMarriages(List<Marriage> marriages) {
        this.marriages = marriages;
    }

    public Person clone() {
        return new Person(id, fullName, birthDate, deathDate, birthLocality, deathLocality, sex, partners, children,
                parents, christening, death, revisions, marriages);
    }
}
