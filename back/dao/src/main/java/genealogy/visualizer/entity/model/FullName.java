package genealogy.visualizer.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FullName implements Serializable {

    @Column(length = 50)
    @Comment("Фамилия")
    private String lastName;

    @Column(length = 50, nullable = false)
    @Comment("Имя")
    private String name;

    @Column(length = 50)
    @Comment("Отчество")
    private String surname;

    @Comment("Статус человека")
    private String status;

    public FullName() {
    }

    public FullName(String lastName, String name, String surname, String status) {
        this.lastName = lastName;
        this.name = name;
        this.surname = surname;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullName fullName = (FullName) o;
        return Objects.equals(lastName, fullName.lastName) &&
                Objects.equals(name, fullName.name) &&
                Objects.equals(surname, fullName.surname) &&
                Objects.equals(status, fullName.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, name, surname, status);
    }
}
