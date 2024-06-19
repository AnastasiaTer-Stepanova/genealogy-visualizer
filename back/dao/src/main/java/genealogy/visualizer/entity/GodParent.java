package genealogy.visualizer.entity;

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
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class GodParent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GOD_PARENT")
    @SequenceGenerator(name = "SEQ_GOD_PARENT", sequenceName = "SEQ_GOD_PARENT", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Embedded
    private FullName fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_LOCALITY"))
    private Locality locality;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHRISTENING_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CHRISTENING"))
    private Christening christening;

    public GodParent() {
    }

    public GodParent(FullName fullName, Locality locality, FullName relative) {
        this.fullName = fullName;
        this.locality = locality;
        this.relative = relative;
    }

    public GodParent(Long id, FullName fullName, Locality locality, FullName relative, Christening christening) {
        this.id = id;
        this.fullName = fullName;
        this.locality = locality;
        this.relative = relative;
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

    public FullName getRelative() {
        return relative;
    }

    public void setRelative(FullName relative) {
        this.relative = relative;
    }

    public Christening getChristening() {
        return christening;
    }

    public void setChristening(Christening christening) {
        this.christening = christening;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GodParent godParent = (GodParent) o;
        boolean namesEqual = Objects.equals(fullName, godParent.fullName) &&
                Objects.equals(id, godParent.id) &&
                Objects.equals(relative, godParent.relative);
        boolean localitiesEqual = Objects.equals(locality, godParent.locality);
        if (locality != null && godParent.locality != null) {
            localitiesEqual = Objects.equals(locality.getId(), godParent.locality.getId());
        }
        boolean christeningEqual = Objects.equals(christening, godParent.christening);
        if (christening != null && godParent.christening != null) {
            christeningEqual = Objects.equals(christening.getId(), godParent.christening.getId());
        }
        return namesEqual && localitiesEqual && christeningEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, (locality != null ? locality.getId() : null), relative);
    }
}
