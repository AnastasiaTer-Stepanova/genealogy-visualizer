package genealogy.visualizer.entity.model;

import genealogy.visualizer.converter.DateRangeTypeConverter;
import genealogy.visualizer.entity.enums.DateRangeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Embeddable
public class DateInfo implements Serializable {

    @Column(length = 10)
    @Comment("Дата, формат: YYYY/MM-YYYY/DD-MM-YYYY")
    private String date;

    @Column(length = 20)
    @Comment("Тип диапазона даты")
    @Convert(converter = DateRangeTypeConverter.class)
    private DateRangeType dateRangeType;

    public DateInfo() {
    }

    public DateInfo(String date, DateRangeType dateRangeType) {
        this.date = date;
        this.dateRangeType = dateRangeType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DateRangeType getDateRangeType() {
        return dateRangeType;
    }

    public void setDateRangeType(DateRangeType dateRangeType) {
        this.dateRangeType = dateRangeType;
    }
}
