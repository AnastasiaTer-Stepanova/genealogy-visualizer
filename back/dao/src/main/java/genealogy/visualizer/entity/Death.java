package genealogy.visualizer.entity;

import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.FullName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Death implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DEATH")
    @SequenceGenerator(name = "SEQ_DEATH", sequenceName = "SEQ_DEATH", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(columnDefinition = "DATE", nullable = false)
    @Comment("Дата смерти")
    private Date date;

    @Embedded
    private FullName fullName;

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

    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "AGE_TYPE", length = 15))
    @Comment(value = "Тип возраста", on = "AGE_TYPE")
    private Age age;

    @Comment("Причина смерти")
    @Column(length = 50)
    private String cause;

    @Comment("Место погребения")
    private String burialPlace;

    @Comment("Комментарий")
    private String comment;

    @Comment("Город, село, деревня и т.д.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_LOCALITY"))
    private Locality locality;

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

    public Death() {
    }

    public Death(Long id, Date date, FullName fullName, FullName relative, Age age, String cause, String burialPlace, String comment, Locality locality, ArchiveDocument archiveDocument, Person person) {
        this.id = id;
        this.date = date;
        this.fullName = fullName;
        this.relative = relative;
        this.age = age;
        this.cause = cause;
        this.burialPlace = burialPlace;
        this.comment = comment;
        this.locality = locality;
        this.archiveDocument = archiveDocument;
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FullName getFullName() {
        return fullName;
    }

    public void setFullName(FullName fullName) {
        this.fullName = fullName;
    }

    public FullName getRelative() {
        return relative;
    }

    public void setRelative(FullName relative) {
        this.relative = relative;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getBurialPlace() {
        return burialPlace;
    }

    public void setBurialPlace(String burialPlace) {
        this.burialPlace = burialPlace;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
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
