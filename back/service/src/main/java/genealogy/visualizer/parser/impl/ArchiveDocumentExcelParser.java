package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.ArchiveDocumentType;
import genealogy.visualizer.parser.FileParser;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static genealogy.visualizer.parser.util.ArchiveDocumentUtil.createArchiveDocument;
import static genealogy.visualizer.parser.util.ArchiveDocumentUtil.getArchiveParams;
import static java.lang.Integer.parseInt;
import static org.apache.commons.io.FileUtils.openOutputStream;

/**
 * Парсей excel файла, где первая странца содержит инфаормацию о документе архива и номер страницы excel, которую нужно
 * спарсить. Так же сохраняет данные о документе архива в БД. В методе parse так же вызывается сохрание самих данных документа
 * в зависимости от его типа.
 */
public class ArchiveDocumentExcelParser implements FileParser {

    private static final Logger LOGGER = LogManager.getLogger(ArchiveDocumentExcelParser.class);
    private static final String LIST_NUMBER_PARAM_NAME = "ListNumber";
    private static final String LIST_WITH_PARAMS_NAME = "parsingSettings";
    private static final String FOLDER_OUTPUT_FILE = "/result/";
    private final Map<ArchiveDocumentType, SheetParser> parserMap;
    private final ArchiveDocumentDAO archiveDocumentDAO;

    public ArchiveDocumentExcelParser(Map<ArchiveDocumentType, SheetParser> parserMap, ArchiveDocumentDAO archiveDocumentDAO) {
        this.parserMap = parserMap;
        this.archiveDocumentDAO = archiveDocumentDAO;
    }

    @Override
    public void parse(File f) {
        Workbook workbook;
        try {
            FileInputStream inFile = new FileInputStream(f);
            workbook = new XSSFWorkbook(inFile);
            inFile.close();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file %s", f.getAbsolutePath()), e);
        }

        Map<String, String> archiveParams = getArchiveParams(workbook.getSheet(LIST_WITH_PARAMS_NAME));
        int numberOfSheetForSafe = parseInt(archiveParams.get(LIST_NUMBER_PARAM_NAME));
        ArchiveDocument archiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(createArchiveDocument(archiveParams));
        Sheet excelSheet;
        try {
            excelSheet = workbook.getSheetAt(numberOfSheetForSafe);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Excel %s with sheet number %s does not exist", f.getName(), numberOfSheetForSafe));
        }
        if (excelSheet == null || excelSheet.getLastRowNum() <= 0) {
            throw new NullPointerException(String.format("Excel %s with sheet number %s does not have info", f.getName(), numberOfSheetForSafe));
        }
        deleteBlankRows(excelSheet);
        try {
            parserMap.get(archiveDocument.getType()).parse(excelSheet, archiveDocument);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Excel %s with sheet number %s couldn't parse", f.getName(), numberOfSheetForSafe), e);
        }

        File outputFile = new File(f.getParent() + FOLDER_OUTPUT_FILE + f.getName());
        try {
            FileOutputStream fileOutputStream = openOutputStream(outputFile);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("File {} successfully written to {}", outputFile.getName(), outputFile.getAbsolutePath());
        if (f.delete()) {
            LOGGER.info("File {} successfully deleted", f.getAbsolutePath());
        } else {
            LOGGER.error("File {} could not be deleted", f.getAbsolutePath());
        }
    }

    private static void deleteBlankRows(Sheet sheet) {
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

    private static void removeRow(Sheet sheet, int rowIndex) {
        if (rowIndex >= 0) {
            sheet.removeRow(sheet.getRow(rowIndex));
            if (rowIndex < sheet.getLastRowNum()) {
                sheet.shiftRows(rowIndex + 1, sheet.getLastRowNum(), -1);
            }
        }
    }

    private static boolean isBlank(Row row) {
        boolean isBlank = true;
        for (Cell cell : row) {
            CellType cellType = cell.getCellType();
            if (!CellType.BLANK.equals(cellType) && !CellType.FORMULA.equals(cellType)) return false;
        }
        return isBlank;
    }
}
