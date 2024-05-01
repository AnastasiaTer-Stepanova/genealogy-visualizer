package genealogy.visualizer.entity.model;

import jakarta.persistence.Column;
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
    private String ageType;

    public Age() {
    }

    public Age(BigDecimal age, String ageType) {
        this.age = age;
        this.ageType = ageType;
    }

    public BigDecimal getAge() {
        return age;
    }

    public void setAge(BigDecimal age) {
        this.age = age;
    }

    public String getAgeType() {
        return ageType;
    }

    public void setAgeType(String ageType) {
        this.ageType = ageType;
    }
}
