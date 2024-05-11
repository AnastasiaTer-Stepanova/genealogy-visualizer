package genealogy.visualizer.parser;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.parser.impl.FileExcelParser;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class FileExcelParserTest extends AbstractTest {

    private static final String FOLDER_OUTPUT_FILE = "/result/";

    @Mock
    private Map<String, SheetParser> parserMap;

    @Mock
    private SheetParser sheetParser;

    @Test
    void checkParseTest(@TempDir(cleanup = CleanupMode.ALWAYS) Path tempDir) throws IOException {
        ArchiveDocument archiveDocument = generator.nextObject(ArchiveDocument.class);
        when(parserMap.get(archiveDocument.getType().getName())).thenReturn(sheetParser);
        FileExcelParser fileExcelParser = new FileExcelParser(parserMap);

        int count = 10;
        int listNumberFroParsing = generator.nextInt(1, 10);
        Workbook workbook = generateSheetWithParsingParamsList(listNumberFroParsing, archiveDocument);
        Sheet workbookSheetAt = workbook.getSheetAt(listNumberFroParsing);
        workbookSheetAt.createRow(0).createCell(0).setCellValue(randomAlphabetic(5));
        workbookSheetAt.createRow(1).createCell(0).setCellValue(randomAlphabetic(5));
        addBlankRows(workbookSheetAt, count);
        createWorkbook(tempDir, workbook);

        doNothing().when(sheetParser).parse(any(XSSFSheet.class), any(Map.class));

        File f = new File(tempDir + TEST_FILE_NAME);
        fileExcelParser.parse(f);

        FileInputStream fi = new FileInputStream(tempDir + FOLDER_OUTPUT_FILE + TEST_FILE_NAME);
        Workbook resultWorkbook = new XSSFWorkbook(fi);
        fi.close();

        assertEquals(resultWorkbook.getNumberOfSheets(), workbook.getNumberOfSheets());
        Sheet resultArchiveSheet = resultWorkbook.getSheet(LIST_WITH_PARAMS_NAME);
        assertNotNull(resultArchiveSheet);
        Sheet archiveSheet = workbook.getSheet(LIST_WITH_PARAMS_NAME);
        assertNotNull(archiveSheet);
        assertSheet(resultArchiveSheet, archiveSheet);
        Sheet resultWorkbookSheetAt = resultWorkbook.getSheetAt(listNumberFroParsing);
        assertNotNull(resultWorkbookSheetAt);
        assertNotNull(workbookSheetAt);
        assertSheetWithCheckBlankRows(resultWorkbookSheetAt, workbookSheetAt, count);
    }

}