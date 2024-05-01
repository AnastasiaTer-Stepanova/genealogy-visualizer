package genealogy.visualizer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Embeddable
public class FullName implements Serializable {

    @Comment("Фамилия")
    private String lastName;

    @Column(nullable = false)
    @Comment("Имя")
    private String name;

    @Comment("Отчество")
    private String surname;

    public FullName() {
    }

    public FullName(String lastName, String name, String surname) {
        this.lastName = lastName;
        this.name = name;
        this.surname = surname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}
