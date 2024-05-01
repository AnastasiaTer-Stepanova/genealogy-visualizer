package genealogy.visualizer.parser;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.ArchiveDocumentType;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Интерфейс для парсинга листов excel файла
 */
public interface SheetParser {

    void parse(Sheet excelSheet, ArchiveDocument archive);

    ArchiveDocumentType type();
}
