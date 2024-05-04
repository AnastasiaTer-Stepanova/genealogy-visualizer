package genealogy.visualizer.entity;

import genealogy.visualizer.entity.model.FullName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Table
public class GodParent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GOD_PARENT")
    @SequenceGenerator(name = "SEQ_GOD_PARENT", sequenceName = "SEQ_GOD_PARENT", allocationSize = 2)
    @Comment("Идентификатор записи")
    private Long id;

    @Embedded
    private FullName fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_LOCALITY"))
    private Locality locality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHRISTENING_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CHRISTENING"))
    private Christening christening;

    public GodParent() {
    }

    public GodParent(FullName fullName, Locality locality) {
        this.fullName = fullName;
        this.locality = locality;
    }

    public GodParent(Long id, FullName fullName, Locality locality, Christening christening) {
        this.id = id;
        this.fullName = fullName;
        this.locality = locality;
        this.christening = christening;
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

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Christening getChristening() {
        return christening;
    }

    public void setChristening(Christening christening) {
        this.christening = christening;
    }
}
