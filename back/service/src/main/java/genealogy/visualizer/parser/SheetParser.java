package genealogy.visualizer.parser;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * Интерфейс для парсинга листов excel файла
 */
public interface SheetParser {

    void parse(Sheet excelSheet, Map<String, String> parsingParams);

    String type();
}
