package genealogy.visualizer.parser;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.enums.LocalityType;
import genealogy.visualizer.entity.model.FullName;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.config.EasyRandomParamsBuilder.getGeneratorParams;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
abstract class AbstractTest {

    static final Logger LOGGER = LogManager.getLogger(AbstractTest.class);
    static final String TEST_FILE_NAME = "/testFile.xlsx";

    static final String DOCUMENT_TYPE_PARAM_NAME = "Type";
    static final String FUND_PARAM_NAME = "Fund";
    static final String CATALOG_PARAM_NAME = "Catalog";
    static final String INSTANCE_PARAM_NAME = "Instance";
    static final String BUNCH_PARAM_NAME = "Bunch";
    static final String YEAR_PARAM_NAME = "YearOfDocument";
    static final String LIST_NUMBER_PARAM_NAME = "ListNumber";
    static final String ARCHIVE_PARAM_NAME = "Archive";
    static final String LIST_WITH_PARAMS_NAME = "ParsingSettings";
    static final String STATUS_COLUMN_NAME = "Status";
    static final String STATUS_IMPORTED = "imported";

    static final String DATE_PATTERN = "dd.MM.yyyy";

    static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN);

    static EasyRandom generator;

    ArchiveDocument archiveDocument;

    @Mock
    ArchiveDocumentDAO archiveDocumentDAO;

    static {
        generator = new EasyRandom(getGeneratorParams());
    }

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setArchive(archive);
    }

    void assertSheetWithCheckBlankRows(Sheet firstSheet, Sheet secondSheetWithBlankRows, int count) {
        assertEquals(firstSheet.getLastRowNum(), secondSheetWithBlankRows.getLastRowNum() - count);
        deleteBlankRows(secondSheetWithBlankRows);
        assertSheet(firstSheet, secondSheetWithBlankRows);
    }

    void assertSheet(Sheet firstSheet, Sheet secondSheet) {
        assertEquals(firstSheet.getSheetName(), secondSheet.getSheetName());
        assertSheet(firstSheet, secondSheet, false, false);
    }

    void assertSheet(Sheet firstSheet, Sheet secondSheet, boolean withStatusColumnInFirstSheet, boolean withoutImportedStatus) {
        assertTrue(firstSheet.getSheetName().contains(secondSheet.getSheetName()));
        if (firstSheet.getLastRowNum() == -1) return;
        assertEquals(firstSheet.getLastRowNum(), secondSheet.getLastRowNum());
        for (int i = 0; i < firstSheet.getLastRowNum(); i++) {
            assertRow(firstSheet.getRow(i), secondSheet.getRow(i), withStatusColumnInFirstSheet, withoutImportedStatus);
        }
    }

    void assertRow(Row firstRow, Row secondRow, boolean withStatusColumnInFirstSheet, boolean withoutImportedStatus) {
        if (firstRow.getLastCellNum() == -1) return;
        if (withStatusColumnInFirstSheet) {
            assertEquals(firstRow.getLastCellNum() - 1, secondRow.getLastCellNum());
        } else {
            assertEquals(firstRow.getLastCellNum(), secondRow.getLastCellNum());
        }
        for (int i = 0; i < secondRow.getLastCellNum(); i++) {
            if (withStatusColumnInFirstSheet) {
                if (i == 0) {
                    if (firstRow.getCell(i).getRowIndex() == 0) {
                        assertEquals(firstRow.getCell(i).getStringCellValue(), STATUS_COLUMN_NAME);
                        continue;
                    }
                    if (withoutImportedStatus) {
                        assertEquals(firstRow.getCell(i).getCellType(), CellType.BLANK);
                        continue;
                    }
                    assertEquals(firstRow.getCell(i).getStringCellValue(), STATUS_IMPORTED);
                }
                if (firstRow.getCell(i + 1) == null) {
                    assertNull(secondRow.getCell(i));
                } else {
                    assertCell(firstRow.getCell(i + 1), secondRow.getCell(i));
                }
            } else {
                if (firstRow.getCell(i) == null) {
                    assertNull(secondRow.getCell(i));
                } else {
                    assertCell(firstRow.getCell(i), secondRow.getCell(i));
                }
            }
        }
    }

    void assertCell(Cell firstCell, Cell secondCell) {
        assertEquals(firstCell.getCellStyle(), secondCell.getCellStyle());
        assertEquals(firstCell.getCellType(), secondCell.getCellType());
        switch (firstCell.getCellType()) {
            case STRING -> assertEquals(firstCell.getStringCellValue(), secondCell.getStringCellValue());
            case NUMERIC -> assertEquals(firstCell.getNumericCellValue(), secondCell.getNumericCellValue());
            case BOOLEAN -> assertEquals(firstCell.getBooleanCellValue(), secondCell.getBooleanCellValue());
            case FORMULA -> assertEquals(firstCell.getCellFormula(), secondCell.getCellFormula());
            case BLANK -> assertEquals(firstCell.getCellComment(), secondCell.getCellComment());
            case ERROR -> assertEquals(firstCell.getErrorCellValue(), secondCell.getErrorCellValue());
            default -> throw new AssertionError("Unexpected cell type: " + firstCell.getCellType());
        }
    }

    void addBlankRows(Sheet sheet, int count) {
        int lastRowNum = sheet.getLastRowNum() + 1;
        for (int i = lastRowNum; i < lastRowNum + count; i++) {
            sheet.createRow(i).createCell(0);
        }
    }

    void createWorkbook(Path tempDir, Workbook workbook) throws IOException {
        File file = new File(tempDir + TEST_FILE_NAME);
        if (file.createNewFile()) {
            LOGGER.info("File created: {}", file.getAbsolutePath());
        } else {
            LOGGER.info("File already exists: {}", file.getAbsolutePath());
        }
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        workbook.close();
    }

    Workbook generateSheetWithParsingParamsList(int listNumberFroParsing, ArchiveDocument archiveDocument) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(LIST_WITH_PARAMS_NAME);
        createRow(sheet, List.of(new CellValue(DOCUMENT_TYPE_PARAM_NAME), new CellValue(archiveDocument.getType().getName())));
        createRow(sheet, List.of(new CellValue(ARCHIVE_PARAM_NAME), new CellValue(archiveDocument.getArchive().getName())));
        createRow(sheet, List.of(new CellValue(FUND_PARAM_NAME), new CellValue(archiveDocument.getFund())));
        createRow(sheet, List.of(new CellValue(CATALOG_PARAM_NAME), new CellValue(archiveDocument.getCatalog())));
        createRow(sheet, List.of(new CellValue(INSTANCE_PARAM_NAME), new CellValue(archiveDocument.getInstance())));
        createRow(sheet, List.of(new CellValue(BUNCH_PARAM_NAME), new CellValue(archiveDocument.getBunch())));
        createRow(sheet, List.of(new CellValue(YEAR_PARAM_NAME), new CellValue(archiveDocument.getYear())));
        createRow(sheet, List.of(new CellValue(LIST_NUMBER_PARAM_NAME), new CellValue(listNumberFroParsing)));
        for (int i = 0; i < listNumberFroParsing; i++) {
            workbook.createSheet(randomAlphabetic(10));
        }
        return workbook;
    }

    Map<String, String> getParsingParams() {
        int listNumberFroParsing = generator.nextInt(1, 10);
        return getParsingParams(generateSheetWithParsingParamsList(listNumberFroParsing, this.archiveDocument).getSheet(LIST_WITH_PARAMS_NAME));
    }

    static Map<String, String> getParsingParams(Sheet initSheet) {
        Map<String, String> params = new HashMap<>();
        for (Row row : initSheet) {
            if (row.getCell(0) == null || row.getCell(1) == null) {
                continue;
            }
            params.put(row.getCell(0).getStringCellValue(), getStringCellValue(row.getCell(1)));
        }
        return params;
    }

    void createRow(Sheet sheet, List<CellValue> cells) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < cells.size(); i++) {
            CellValue cellValue = cells.get(i);
            switch (cellValue.getCellType()) {
                case NUMERIC -> row.createCell(i, CellType.NUMERIC).setCellValue(cellValue.getNumberValue());
                case BLANK -> row.createCell(i, CellType.BLANK);
                default -> row.createCell(i, CellType.STRING).setCellValue(cellValue.getStringValue());
            }
        }
    }

    static void deleteBlankRows(Sheet sheet) {
        if (!isBlank(sheet.getRow(sheet.getLastRowNum()))) return;
        int lastRowNum = sheet.getLastRowNum();
        int i = lastRowNum;
        while (isBlank(sheet.getRow(i))) {
            removeRow(sheet, i);
            i--;
        }
        int numbers = (lastRowNum - i);
        LOGGER.info("Deleted {} blank rows in {}", numbers, sheet.getSheetName());
    }

    static void removeRow(Sheet sheet, int rowIndex) {
        if (rowIndex >= 0) {
            sheet.removeRow(sheet.getRow(rowIndex));
            if (rowIndex < sheet.getLastRowNum()) {
                sheet.shiftRows(rowIndex + 1, sheet.getLastRowNum(), -1);
            }
        }
    }

    static Sheet createXSSFWorkbook(Map<String, Integer> headers, String sheetName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        workbook.close();
        Sheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);
        for (Map.Entry<String, Integer> entry : headers.entrySet()) {
            row.createCell(entry.getValue()).setCellValue(entry.getKey());
        }
        return sheet;
    }

    static String getFullName(FullName fullName) {
        return fullName.getStatus() + " " +
                fullName.getName() + " " +
                fullName.getSurname() + " " +
                fullName.getLastName() + " ";
    }

    static String getLocality(Locality locality) {
        StringBuilder builder = new StringBuilder();
        if (locality.getAddress() != null) {
            builder.append(locality.getAddress()).append(" слободы ");
        }
        String type = LocalityType.TOWN.equals(locality.getType()) ? "г. " : locality.getType().getName();
        builder.append(type).append(" ").append(locality.getName());
        return builder.toString();
    }


    static boolean isBlank(Row row) {
        boolean isBlank = true;
        for (Cell cell : row) {
            CellType cellType = cell.getCellType();
            if (!CellType.BLANK.equals(cellType) && !CellType.FORMULA.equals(cellType)) return false;
        }
        return isBlank;
    }
}
