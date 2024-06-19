package genealogy.visualizer.entity;

import genealogy.visualizer.converter.WitnessConverter;
import genealogy.visualizer.entity.enums.WitnessType;
import genealogy.visualizer.entity.model.FullName;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Witness implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WITNESS")
    @SequenceGenerator(name = "SEQ_WITNESS", sequenceName = "SEQ_WITNESS", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Embedded
    private FullName fullName;

    @Column(length = 10, nullable = false)
    @Comment("Тип поручителя: по жениху, по невесте")
    @Convert(converter = WitnessConverter.class)
    private WitnessType witnessType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALITY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_LOCALITY"))
    private Locality locality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARRIAGE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_MARRIAGE"))
    private Marriage marriage;

    public Witness() {
    }

    public Witness(FullName fullName, Locality locality, WitnessType witnessType) {
        this.fullName = fullName;
        this.locality = locality;
        this.witnessType = witnessType;
    }

    public Witness(Long id, FullName fullName, WitnessType witnessType, Locality locality, Marriage marriage) {
        this.id = id;
        this.fullName = fullName;
        this.witnessType = witnessType;
        this.locality = locality;
        this.marriage = marriage;
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

    public WitnessType getWitnessType() {
        return witnessType;
    }

    public void setWitnessType(WitnessType witnessType) {
        this.witnessType = witnessType;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Marriage getMarriage() {
        return marriage;
    }

    public void setMarriage(Marriage marriage) {
        this.marriage = marriage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Witness witness = (Witness) o;
        boolean nameEqual = Objects.equals(fullName, witness.fullName) &&
                witnessType == witness.witnessType &&
                Objects.equals(id, witness.id);
        boolean localitiesEqual = Objects.equals(locality, witness.locality);
        if (locality != null && witness.locality != null) {
            localitiesEqual = Objects.equals(locality.getId(), witness.locality.getId());
        }
        boolean marriagesEqual = Objects.equals(marriage, witness.marriage);
        if (marriage != null && witness.marriage != null) {
            marriagesEqual = Objects.equals(marriage.getId(), witness.marriage.getId());
        }
        return nameEqual && localitiesEqual && marriagesEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                fullName,
                witnessType,
                (locality != null ? locality.getId() : null),
                (marriage != null ? marriage.getId() : null));
    }
}
