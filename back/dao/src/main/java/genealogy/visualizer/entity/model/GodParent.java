package genealogy.visualizer.entity.model;

import genealogy.visualizer.entity.Locality;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class GodParent implements Serializable {

    @Embedded
    private FullName fullName;

    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", referencedColumnName = "id")
    private Locality locality;

    public GodParent() {
    }

    public GodParent(FullName fullName, Locality locality) {
        this.fullName = fullName;
        this.locality = locality;
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
}
