package genealogy.visualizer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UK_CHRISTENING_IN_PERSON", columnNames = {"CHRISTENING_ID"}),
        @UniqueConstraint(name = "UK_FAMILY_REVISION_IN_PERSON", columnNames = {"FAMILY_REVISION_ID"}),
})
public class Person implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PERSON")
    @SequenceGenerator(name = "SEQ_PERSON", sequenceName = "SEQ_PERSON", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    private Christening christening;

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    private FamilyRevision familyRevision;

    public Person() {
    }

    public Person(Long id, Christening christening, FamilyRevision familyRevision) {
        this.id = id;
        this.christening = christening;
        this.familyRevision = familyRevision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Christening getChristening() {
        return christening;
    }

    public void setChristening(Christening christening) {
        this.christening = christening;
    }

    public FamilyRevision getFamilyRevision() {
        return familyRevision;
    }

    public void setFamilyRevision(FamilyRevision familyRevision) {
        this.familyRevision = familyRevision;
    }
}
