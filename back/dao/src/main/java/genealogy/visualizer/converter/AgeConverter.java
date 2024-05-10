package genealogy.visualizer.converter;

import genealogy.visualizer.entity.enums.AgeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AgeConverter implements AttributeConverter<AgeType, String> {

    @Override
    public String convertToDatabaseColumn(AgeType ageType) {
        return ageType != null ? ageType.getName() : null;
    }

    @Override
    public AgeType convertToEntityAttribute(String s) {
        return s != null ? AgeType.of(s) : null;
    }
}
