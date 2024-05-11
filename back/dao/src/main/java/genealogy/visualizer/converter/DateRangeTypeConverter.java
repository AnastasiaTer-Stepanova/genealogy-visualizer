package genealogy.visualizer.converter;

import genealogy.visualizer.entity.enums.DateRangeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DateRangeTypeConverter implements AttributeConverter<DateRangeType, String> {

    @Override
    public String convertToDatabaseColumn(DateRangeType type) {
        return type != null ? type.getName() : null;
    }

    @Override
    public DateRangeType convertToEntityAttribute(String s) {
        return s != null ? DateRangeType.of(s) : null;
    }
}