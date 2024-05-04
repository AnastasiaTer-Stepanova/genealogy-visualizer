package genealogy.visualizer.converter;


import genealogy.visualizer.entity.enums.LocalityType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalityTypeConverter implements AttributeConverter<LocalityType, String> {

    @Override
    public String convertToDatabaseColumn(LocalityType localityType) {
        return localityType != null ? localityType.getName() : null;
    }

    @Override
    public LocalityType convertToEntityAttribute(String s) {
        return s != null ? LocalityType.of(s) : null;
    }
}