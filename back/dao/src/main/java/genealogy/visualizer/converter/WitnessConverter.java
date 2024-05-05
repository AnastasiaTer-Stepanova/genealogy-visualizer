package genealogy.visualizer.converter;

import genealogy.visualizer.entity.enums.WitnessType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WitnessConverter implements AttributeConverter<WitnessType, String> {

    @Override
    public String convertToDatabaseColumn(WitnessType type) {
        return type != null ? type.getName() : null;
    }

    @Override
    public WitnessType convertToEntityAttribute(String s) {
        return s != null ? WitnessType.of(s) : null;
    }
}