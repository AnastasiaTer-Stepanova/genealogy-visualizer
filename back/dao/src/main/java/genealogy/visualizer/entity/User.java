package genealogy.visualizer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Table(name = "USERS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_USER_LOGIN", columnNames = {"LOGIN"}),
        },
        indexes = {
                @Index(name = "IDX_USER_LOGIN", columnList = "LOGIN"),
        }
)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER")
    @SequenceGenerator(name = "SEQ_USER", sequenceName = "SEQ_USER", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(nullable = false)
    @Comment("Логин")
    private String login;

    @Column(nullable = false)
    @Comment("Пароль")
    private String password;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
