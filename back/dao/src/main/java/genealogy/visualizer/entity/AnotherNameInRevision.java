package genealogy.visualizer.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "CONSTRAINT_ANOTHER_NAME", columnNames = {"REVISION_PERSON_ID", "NUMBER"}))
public class AnotherNameInRevision implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANOTHER_NAME_IN_REVISION_SEQ")
    @SequenceGenerator(name = "ANOTHER_NAME_IN_REVISION_SEQ", sequenceName = "ANOTHER_NAME_IN_REVISION_SEQ", allocationSize = 5)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(name = "NUMBER", nullable = false)
    @Comment("Номер фамилии")
    private Byte number;

    @Column(name = "LAST_NAME", nullable = false)
    @Comment("Фамилия")
    private String lastName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REVISION_PERSON_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ANOTHER_NAME"))
    private FamilyRevision revisionPerson;

    public AnotherNameInRevision() {
    }

    public AnotherNameInRevision(Long id, Byte number, String lastName, FamilyRevision revisionPerson) {
        this.id = id;
        this.number = number;
        this.lastName = lastName;
        this.revisionPerson = revisionPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getNumber() {
        return number;
    }

    public void setNumber(Byte number) {
        this.number = number;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public FamilyRevision getRevisionPerson() {
        return revisionPerson;
    }

    public void setRevisionPerson(FamilyRevision revisionPerson) {
        this.revisionPerson = revisionPerson;
    }
}
