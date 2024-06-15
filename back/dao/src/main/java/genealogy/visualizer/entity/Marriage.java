package genealogy.visualizer.entity;

import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.FullName;
import genealogy.visualizer.entity.model.Witness;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Marriage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MARRIAGE")
    @SequenceGenerator(name = "SEQ_MARRIAGE", sequenceName = "SEQ_MARRIAGE", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(columnDefinition = "DATE", nullable = false)
    @Comment("Дата брака")
    private LocalDate date;

    @Comment("Город, село, деревня и т.д. мужа")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HUSBAND_LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_HUSBAND_LOCALITY"))
    private Locality husbandLocality;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "HUSBANDS_FATHER_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "HUSBANDS_FATHER_NAME", length = 50)),
            @AttributeOverride(name = "surname", column = @Column(name = "HUSBANDS_FATHER_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "HUSBANDS_FATHER_STATUS"))
    })
    @Comment(value = "Имя отца мужа", on = "HUSBANDS_FATHER_LAST_NAME")
    @Comment(value = "Фамилия отца мужа", on = "HUSBANDS_FATHER_NAME")
    @Comment(value = "Отчество отца мужа", on = "HUSBANDS_FATHER_SURNAME")
    @Comment(value = "Статус отца мужа", on = "HUSBANDS_FATHER_STATUS")
    private FullName husbandsFather;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "HUSBAND_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "HUSBAND_NAME", length = 50, nullable = false)),
            @AttributeOverride(name = "surname", column = @Column(name = "HUSBAND_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "HUSBAND_STATUS"))
    })
    @Comment(value = "Имя мужа", on = "HUSBAND_LAST_NAME")
    @Comment(value = "Фамилия мужа", on = "HUSBAND_NAME")
    @Comment(value = "Отчество мужа", on = "HUSBAND_SURNAME")
    @Comment(value = "Статус мужа", on = "HUSBAND_STATUS")
    private FullName husband;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "age", column = @Column(name = "HUSBAND_AGE", precision = 5, scale = 1)),
            @AttributeOverride(name = "type", column = @Column(name = "HUSBAND_AGE_TYPE", length = 15))
    })
    @Comment(value = "Возраст мужа", on = "HUSBAND_AGE")
    @Comment(value = "Тип возраста мужа", on = "HUSBAND_AGE_TYPE")
    private Age husbandAge;

    @Column(nullable = false)
    @Comment("Номер брака мужа")
    private Byte husbandMarriageNumber;

    @Comment("Город, село, деревня и т.д. жены")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WIFE_LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_WIFE_LOCALITY"))
    private Locality wifeLocality;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "WIFES_FATHER_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "WIFES_FATHER_NAME", length = 50)),
            @AttributeOverride(name = "surname", column = @Column(name = "WIFES_FATHER_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "WIFES_FATHER_STATUS"))
    })
    @Comment(value = "Имя отца жены", on = "WIFES_FATHER_LAST_NAME")
    @Comment(value = "Фамилия отца жены", on = "WIFES_FATHER_NAME")
    @Comment(value = "Отчество отца жены", on = "WIFES_FATHER_SURNAME")
    @Comment(value = "Статус отца жены", on = "WIFES_FATHER_STATUS")
    private FullName wifesFather;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "WIFE_LAST_NAME", length = 50)),
            @AttributeOverride(name = "name", column = @Column(name = "WIFE_NAME", length = 50, nullable = false)),
            @AttributeOverride(name = "surname", column = @Column(name = "WIFE_SURNAME", length = 50)),
            @AttributeOverride(name = "status", column = @Column(name = "WIFE_STATUS"))
    })
    @Comment(value = "Имя жены", on = "WIFE_LAST_NAME")
    @Comment(value = "Фамилия жены", on = "WIFE_NAME")
    @Comment(value = "Отчество жены", on = "WIFE_SURNAME")
    @Comment(value = "Статус жены", on = "WIFE_STATUS")
    private FullName wife;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "age", column = @Column(name = "WIFE_AGE", precision = 5, scale = 1)),
            @AttributeOverride(name = "type", column = @Column(name = "WIFE_AGE_TYPE", length = 15))
    })
    @Comment(value = "Возраст жены", on = "WIFE_AGE")
    @Comment(value = "Тип возраста жены", on = "WIFE_AGE_TYPE")
    private Age wifeAge;

    @Column(nullable = false)
    @Comment("Номер брака жены")
    private Byte wifeMarriageNumber;

    @Comment("Комментарий")
    private String comment;

    @ElementCollection(targetClass = Witness.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "WITNESS",
            joinColumns = @JoinColumn(name = "MARRIAGE_ID",
                    foreignKey = @ForeignKey(name = "FK_MARRIAGE")))
    @AssociationOverrides({
            @AssociationOverride(name = "locality",
                    joinColumns = @JoinColumn(name = "LOCALITY_ID"),
                    foreignKey = @ForeignKey(name = "FK_WITNESS_LOCALITY"))
    })
    private List<Witness> witnesses = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_DOCUMENT_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE_DOCUMENT"))
    private ArchiveDocument archiveDocument;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PERSON_MARRIAGE",
            joinColumns = @JoinColumn(name = "MARRIAGE_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_MARRIAGE_ID_PERSON_ID")),
            inverseJoinColumns = @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID"))
    private List<Person> persons = new ArrayList<>();

    public Marriage() {
    }

    public Marriage(Long id, LocalDate date, Locality husbandLocality, FullName husbandsFather, FullName husband, Age husbandAge, Byte husbandMarriageNumber, Locality wifeLocality, FullName wifesFather, FullName wife, Age wifeAge, Byte wifeMarriageNumber, String comment, List<Witness> witnesses, ArchiveDocument archiveDocument, List<Person> persons) {
        this.id = id;
        this.date = date;
        this.husbandLocality = husbandLocality;
        this.husbandsFather = husbandsFather;
        this.husband = husband;
        this.husbandAge = husbandAge;
        this.husbandMarriageNumber = husbandMarriageNumber;
        this.wifeLocality = wifeLocality;
        this.wifesFather = wifesFather;
        this.wife = wife;
        this.wifeAge = wifeAge;
        this.wifeMarriageNumber = wifeMarriageNumber;
        this.comment = comment;
        this.witnesses = witnesses;
        this.archiveDocument = archiveDocument;
        this.persons = persons;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Locality getHusbandLocality() {
        return husbandLocality;
    }

    public void setHusbandLocality(Locality husbandLocality) {
        this.husbandLocality = husbandLocality;
    }

    public FullName getHusbandsFather() {
        return husbandsFather;
    }

    public void setHusbandsFather(FullName husbandsFather) {
        this.husbandsFather = husbandsFather;
    }

    public FullName getHusband() {
        return husband;
    }

    public void setHusband(FullName husband) {
        this.husband = husband;
    }

    public Age getHusbandAge() {
        return husbandAge;
    }

    public void setHusbandAge(Age husbandAge) {
        this.husbandAge = husbandAge;
    }

    public Byte getHusbandMarriageNumber() {
        return husbandMarriageNumber;
    }

    public void setHusbandMarriageNumber(Byte husbandMarriageNumber) {
        this.husbandMarriageNumber = husbandMarriageNumber;
    }

    public Locality getWifeLocality() {
        return wifeLocality;
    }

    public void setWifeLocality(Locality wifeLocality) {
        this.wifeLocality = wifeLocality;
    }

    public FullName getWifesFather() {
        return wifesFather;
    }

    public void setWifesFather(FullName wifesFather) {
        this.wifesFather = wifesFather;
    }

    public FullName getWife() {
        return wife;
    }

    public void setWife(FullName wife) {
        this.wife = wife;
    }

    public Age getWifeAge() {
        return wifeAge;
    }

    public void setWifeAge(Age wifeAge) {
        this.wifeAge = wifeAge;
    }

    public Byte getWifeMarriageNumber() {
        return wifeMarriageNumber;
    }

    public void setWifeMarriageNumber(Byte wifeMarriageNumber) {
        this.wifeMarriageNumber = wifeMarriageNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Witness> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(List<Witness> witnesses) {
        this.witnesses = witnesses;
    }

    public ArchiveDocument getArchiveDocument() {
        return archiveDocument;
    }

    public void setArchiveDocument(ArchiveDocument archiveDocument) {
        this.archiveDocument = archiveDocument;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public Marriage clone() {
        return new Marriage(id, date, husbandLocality, husbandsFather, husband, husbandAge, husbandMarriageNumber,
                wifeLocality, wifesFather, wife, wifeAge, wifeMarriageNumber, comment, witnesses, archiveDocument, persons);
    }
}
