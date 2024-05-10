package genealogy.visualizer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PERSON_MARRIAGE",
            joinColumns = @JoinColumn(name = "MARRIAGE_ID",
                    referencedColumnName = "ID",
                    foreignKey = @ForeignKey(name = "FK_MARRIAGE_ID_PERSON_ID")),
            inverseJoinColumns = @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID"))
    private List<Marriage> marriages = new ArrayList<>();

    public Person() {
    }

    public Person(Long id, Christening christening, FamilyRevision familyRevision, List<Marriage> marriages) {
        this.id = id;
        this.christening = christening;
        this.familyRevision = familyRevision;
        this.marriages = marriages;
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

    public List<Marriage> getMarriages() {
        return marriages;
    }

    public void setMarriages(List<Marriage> marriages) {
        this.marriages = marriages;
    }
}
