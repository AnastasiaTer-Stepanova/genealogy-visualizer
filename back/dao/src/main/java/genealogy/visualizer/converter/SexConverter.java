package genealogy.visualizer.converter;

import genealogy.visualizer.entity.enums.Sex;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SexConverter implements AttributeConverter<Sex, String> {

    @Override
    public String convertToDatabaseColumn(Sex sex) {
        return sex != null ? sex.getName() : null;
    }

    @Override
    public Sex convertToEntityAttribute(String s) {
        return s != null ? Sex.of(s) : null;
    }
}