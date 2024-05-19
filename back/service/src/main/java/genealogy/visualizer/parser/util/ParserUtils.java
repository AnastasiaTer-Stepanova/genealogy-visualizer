package genealogy.visualizer.parser.util;

import genealogy.visualizer.entity.enums.AgeType;
import genealogy.visualizer.entity.enums.Sex;
import genealogy.visualizer.entity.model.Age;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Утилитарный класс для вспомогательных методов необходимых для парсинга excel файлов
 */
public class ParserUtils {

    public static final String STATUS_IMPORTED = "imported";
    public static final String STATUS_COLUMN_NAME = "Status"; //для первой заливки эта колонка должна быть пустая, в последующем здесь будет проставлен статус imported для семей, которые сохранились в БД
    public static final String WITHOUT_LAST_NAME = "БФ";
    public static final String HYPHEN = "-";

    private static final Logger LOGGER = LogManager.getLogger(ParserUtils.class);

    private static final String NEWBORN = "н/р";

    private static final Set<String> OBSCURE_DATA = Set.of("?", "-", ",", ".");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Get header map kay - name of column, value - number of column, add status column if sheet does not have it
     *
     * @param excelSheet excel list
     * @return header map
     */
    public static Map<String, Integer> getHeaderWithStatusColumn(Sheet excelSheet) {
        if (excelSheet == null || excelSheet.getLastRowNum() < 0)
            throw new NullPointerException("No header found in sheet");
        Row header = excelSheet.getRow(0);
        Map<String, Integer> headerData = new HashMap<>();
        boolean hasStatusColumn = false;
        for (Cell cell : header) {
            String value = StringUtils.capitalize(getStringCellValue(cell));
            if (STATUS_COLUMN_NAME.equals(value)) hasStatusColumn = true;
            headerData.put(value, cell.getColumnIndex());
        }
        if (!hasStatusColumn) {
            excelSheet.shiftColumns(0, header.getLastCellNum(), 1);
            CellStyle cellStyle = header.getCell(1).getCellStyle();
            header = excelSheet.getRow(0);
            header.createCell(0).setCellValue(STATUS_COLUMN_NAME);
            header.getCell(0).setCellStyle(cellStyle);
            for (Map.Entry<String, Integer> entry : headerData.entrySet()) {
                int oldColumnNumber = entry.getValue();
                entry.setValue(oldColumnNumber + 1);
            }
            headerData.put(STATUS_COLUMN_NAME, 0);
        }
        return headerData;
    }

    /**
     * Parse string age
     *
     * @param age age
     * @return age
     */
    public static Age parseAge(String age) {
        if (StringUtils.isEmpty(age)) return null;
        for (String subString : OBSCURE_DATA) {
            if (age.contains(subString)) return null;
        }
        if (age.toLowerCase().contains(NEWBORN)) return new Age(BigDecimal.ZERO, AgeType.NEWBORN);
        String prefixRegexp = "\\d+(\\.\\d+)?\\s*";
        if (age.matches(prefixRegexp + "(дней$|дн$|д$)")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("(дней$|дн$|д$)", "").trim());
            return new Age(formatAge, AgeType.DAY);
        }
        if (age.matches(prefixRegexp + "(недель$|нед$|н$)")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("(недель$|нед$|н$)", "").trim());
            return new Age(formatAge, AgeType.WEEK);
        }
        if (age.matches(prefixRegexp + "(месяцев$|мес$|м$)")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("(месяцев$|мес$|м$)", "").trim());
            return new Age(formatAge, AgeType.MONTH);
        }
        return new Age(new BigDecimal(age), AgeType.YEAR);
    }

    /**
     * Get short cell value
     *
     * @param row     row
     * @param cellNum header params
     * @return short value
     */
    public static LocalDate getDateCellValue(Row row, Integer cellNum) {
        Cell cell = checkAndReturnFromRow(row, cellNum);
        return getDateCellValue(cell);
    }

    public static LocalDate getDateCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case CellType.NUMERIC, CellType.BLANK -> null;
            case CellType.STRING -> {
                String cellValue = cell.getStringCellValue();
                if (HYPHEN.equals(cellValue)) yield null;
                yield LocalDate.parse(cellValue, dateFormatter);
            }
            default -> throw new IllegalArgumentException("Параметр в excel задан в неверном формате");
        };
    }

    /**
     * Get short cell value
     *
     * @param row     row
     * @param cellNum header params
     * @return short value
     */
    public static Short getShortCellValue(Row row, Integer cellNum) {
        Cell cell = checkAndReturnFromRow(row, cellNum);
        return getShortCellValue(cell);
    }

    /**
     * Get short cell value
     *
     * @param cell cell
     * @return short value
     */
    public static Short getShortCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case CellType.NUMERIC -> (short) cell.getNumericCellValue();
            case CellType.STRING -> {
                String value = cell.getStringCellValue();
                for (String subString : OBSCURE_DATA) {
                    if (value.contains(subString)) yield null;
                }
                yield Short.parseShort(value);
            }
            case CellType.BLANK -> null;
            default -> throw new IllegalArgumentException("Параметр в excel задан в неверном формате");
        };
    }

    /**
     * Get string cell value
     *
     * @param row     row
     * @param cellNum cell num
     * @return string value
     */
    public static String getStringCellValue(Row row, Integer cellNum) {
        return getStringCellValue(checkAndReturnFromRow(row, cellNum));
    }

    /**
     * Get string cell value
     *
     * @param cell cell
     * @return string value
     */
    public static String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case CellType.STRING -> cell.getStringCellValue().trim();
            case CellType.NUMERIC -> {
                String value = String.valueOf(cell.getNumericCellValue()).trim();
                if (value.endsWith(".0")) {
                    yield value.substring(0, value.length() - 2);
                }
                if (HYPHEN.equals(value)) yield null;
                yield value;
            }
            case CellType.BLANK -> null;
            case CellType.BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> throw new IllegalArgumentException("Параметр в excel задан в неверном формате");
        };
    }

    /**
     * Update status column in excel
     *
     * @param excelSheet               excel
     * @param successParsingRowNumbers success parsing row numbers
     */
    public static void updateStatus(Sheet excelSheet, List<Integer> successParsingRowNumbers, int statusColumnNumber) {
        CellStyle cellStyle = excelSheet.getRow(0).getRowStyle();
        for (Row row : excelSheet) {
            if (row.getRowNum() == 0) continue;
            Cell cell = row.getCell(statusColumnNumber);
            if (cell == null) {
                cell = row.createCell(statusColumnNumber);
                row.getCell(statusColumnNumber).setCellStyle(cellStyle);
            }
            if (STATUS_IMPORTED.equals(cell.getStringCellValue())) continue;
            if (successParsingRowNumbers.contains(row.getRowNum())) {
                cell.setCellValue(STATUS_IMPORTED);
            }
        }
    }

    public static Sex getSex(String maleStr, String femaleStr) {
        if (maleStr != null) {
            return Sex.MALE;
        } else if (femaleStr != null) {
            return Sex.FEMALE;
        }
        LOGGER.error("Sex doesn't exist");
        return null;
    }

    private static Cell checkAndReturnFromRow(Row row, Integer cellNum) {
        if (row == null) return null;
        if (cellNum == null) return null;
        return row.getCell(cellNum);
    }
}
