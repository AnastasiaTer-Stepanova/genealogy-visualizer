package genealogy.visualizer.entity.model;

import genealogy.visualizer.entity.Locality;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Embeddable
public class GodParent implements Serializable {

    @Embedded
    private FullName fullName;

    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", referencedColumnName = "id")
    private Locality locality;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastName", column = @Column(name = "RELATIVE_LAST_NAME")),
            @AttributeOverride(name = "name", column = @Column(name = "RELATIVE_NAME")),
            @AttributeOverride(name = "surname", column = @Column(name = "RELATIVE_SURNAME")),
            @AttributeOverride(name = "status", column = @Column(name = "RELATIVE_STATUS"))
    })
    @Comment(value = "Имя родственника", on = "RELATIVE_LAST_NAME")
    @Comment(value = "Фамилия родственника", on = "RELATIVE_NAME")
    @Comment(value = "Отчество родственника", on = "RELATIVE_SURNAME")
    @Comment(value = "Статус родственника", on = "RELATIVE_STATUS")
    private FullName relative;

    public GodParent() {
    }

    public GodParent(FullName fullName, Locality locality, FullName relative) {
        this.fullName = fullName;
        this.locality = locality;
        this.relative = relative;
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
}
