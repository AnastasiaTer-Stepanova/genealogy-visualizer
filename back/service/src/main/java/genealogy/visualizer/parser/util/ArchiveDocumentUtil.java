package genealogy.visualizer.parser.util;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;

import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static java.lang.Short.valueOf;

/**
 * Утилитарный класс для вспомогательных методов необходимых для парсинга из excel в {@link ArchiveDocument}
 */
public class ArchiveDocumentUtil {

    public static final String ARCHIVE_PARAM_NAME = "Archive";

    private static final String DOCUMENT_TYPE_PARAM_NAME = "Type";
    private static final String FUND_PARAM_NAME = "Fund";
    private static final String CATALOG_PARAM_NAME = "Catalog";
    private static final String INSTANCE_PARAM_NAME = "Instance";
    private static final String BUNCH_PARAM_NAME = "Bunch";
    private static final String YEAR_PARAM_NAME = "YearOfDocument";

    public static Map<String, String> getArchiveParams(Sheet initSheet) {
        Map<String, String> params = new HashMap<>();
        for (Row row : initSheet) {
            params.put(row.getCell(0).getStringCellValue(), getStringCellValue(row.getCell(1)));
        }
        return params;
    }

    public static ArchiveDocument createArchiveDocument(Map<String, String> archiveParams) {
        return new ArchiveDocument(
                ArchiveDocumentType.of(archiveParams.get(DOCUMENT_TYPE_PARAM_NAME)),
                valueOf(archiveParams.get(YEAR_PARAM_NAME)),
                archiveParams.get(FUND_PARAM_NAME),
                archiveParams.get(CATALOG_PARAM_NAME),
                archiveParams.get(INSTANCE_PARAM_NAME),
                archiveParams.get(BUNCH_PARAM_NAME),
                new Archive(archiveParams.get(ARCHIVE_PARAM_NAME))
        );
    }

}
