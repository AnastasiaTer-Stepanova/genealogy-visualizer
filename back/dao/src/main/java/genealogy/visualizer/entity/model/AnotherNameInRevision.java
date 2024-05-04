package genealogy.visualizer.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Embeddable
public class AnotherNameInRevision implements Serializable {

    @Column(name = "NUMBER", nullable = false)
    @Comment("Номер фамилии")
    private Byte number;

    @Column(name = "LAST_NAME", nullable = false)
    @Comment("Фамилия")
    private String lastName;

    public AnotherNameInRevision() {
    }

    public AnotherNameInRevision(Byte number, String lastName) {
        this.number = number;
        this.lastName = lastName;
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
}
