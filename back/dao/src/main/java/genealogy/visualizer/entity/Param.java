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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_PARAM_NAME", columnNames = {"NAME"}),
        },
        indexes = {
                @Index(name = "IDX_PARAM_NAME", columnList = "NAME"),
        }
)
public class Param implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARAM")
    @SequenceGenerator(name = "SEQ_PARAM", sequenceName = "SEQ_PARAM", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(nullable = false)
    @Comment("Наименование парамера")
    private String name;

    @Comment("Значение параметра")
    private String value;

    public Param() {
    }

    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
