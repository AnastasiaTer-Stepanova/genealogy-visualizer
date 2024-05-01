package genealogy.visualizer.converter;

import genealogy.visualizer.entity.ArchiveDocumentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ArchiveDocumentTypeConverter implements AttributeConverter<ArchiveDocumentType, String> {

    @Override
    public String convertToDatabaseColumn(ArchiveDocumentType archiveDocumentType) {
        return archiveDocumentType != null ? archiveDocumentType.getName() : null;
    }

    @Override
    public ArchiveDocumentType convertToEntityAttribute(String s) {
        return s != null ? ArchiveDocumentType.of(s) : null;
    }
}
