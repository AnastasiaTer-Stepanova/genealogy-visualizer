package genealogy.visualizer.entity.model;

import genealogy.visualizer.converter.WitnessConverter;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.enums.WitnessType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Embeddable
public class Witness implements Serializable {

    @Embedded
    private FullName fullName;

    @Column(length = 10, nullable = false)
    @Comment("Тип поручителя: по жениху, по невесте")
    @Convert(converter = WitnessConverter.class)
    private WitnessType witnessType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALITY_ID", referencedColumnName = "id")
    private Locality locality;

    public Witness() {
    }

    public Witness(FullName fullName, Locality locality, WitnessType witnessType) {
        this.fullName = fullName;
        this.locality = locality;
        this.witnessType = witnessType;
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

    public WitnessType getWitnessType() {
        return witnessType;
    }

    public void setWitnessType(WitnessType witnessType) {
        this.witnessType = witnessType;
    }
}
