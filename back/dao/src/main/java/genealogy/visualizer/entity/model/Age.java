package genealogy.visualizer.entity.model;

import genealogy.visualizer.converter.AgeConverter;
import genealogy.visualizer.entity.enums.AgeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class Age implements Serializable {

    @Comment("Возраст")
    @Column(precision = 5, scale = 1)
    private BigDecimal age;

    @Comment("Тип возраста")
    @Convert(converter = AgeConverter.class)
    @Column(length = 15)
    private AgeType type;

    public Age() {
    }

    public Age(BigDecimal age, AgeType type) {
        this.age = age;
        this.type = type;
    }

    public BigDecimal getAge() {
        return age;
    }

    public void setAge(BigDecimal age) {
        this.age = age;
    }

    public AgeType getType() {
        return type;
    }

    public void setType(AgeType type) {
        this.type = type;
    }
}
