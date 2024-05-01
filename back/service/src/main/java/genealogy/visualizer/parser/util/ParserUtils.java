package genealogy.visualizer.parser.util;

import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.FullName;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.contains;

/**
 * Утилитарный класс для вспомогательных методов необходимых для парсинга excel файлов
 */
public class ParserUtils {

    public static final String STATUS_IMPORTED = "imported";
    public static final String STATUS_COLUMN_NAME = "status"; //для первой заливки эта колонка должна быть пустая, в последующем здесь будет проставлен статус imported для семей, которые сохранились в БД
    public static final String WITHOUT_FIRST_NAME = "БФ";
    public static final Set<String> TOWN_LOCATION = Set.of("г.", "город", "города");
    public static final Set<String> VILLAGE_LOCATION = Set.of("с.", "село", "села");
    public static final Set<String> HAMLET_LOCATION = Set.of("д.", "деревня", "деревни");
    public static final String LOCATION_EXCLUDE = "того села";
    public static final String HYPHEN = "-";

    public static final Set<String> SETTLEMENT = Set.of("слободы", "уезда", "округа");

    private static final Logger LOGGER = LogManager.getLogger(ParserUtils.class);

    private static final String TWIN = "близнец";
    private static final Set<String> RELATIONSHIPS = Set.of(
            "жена", "брат", "сестра", "племянник", "сноха", "двоюродный", "двоюродная", "свекровь",
            "теща", "троюродный", "троюродная", "другая жена", "дочь", "сын", "пассынок", "внук", "внучка",
            "тесть", "второбрачная", "зять", "тетя", "мачеха", "тетка", "девица");
    private static final Set<String> ARRANGED_MARRIAGE = Set.of("от 1бр", "от 2бр", "от 3бр", "от 4бр", "от 5бр",
            "от 1 бр", "от 2 бр", "от 3 бр", "от 4 бр", "от 5 бр", "от1бр", "от2бр", "от3бр", "от4бр", "от5бр");
    private static final Set<String> ARRANGED_WIFE = Set.of("1-я жена", "2-я жена", "3-я жена", "4-я жена", "5-я жена");
    private static final Set<String> WIDOWS = Set.of("вд1", "вд2", "вд3", "вд4", "вд5", "водва(ец)", "вдова", "вдовец");
    private static final Set<String> FEMININE_COUNTER = Set.of("другая", "первая", "вторая", "третья", "четвертая",
            "пятая", "шестая", "седьмая", "восьмая", "девятая", "десятая");
    private static final Set<String> MASCULINE_COUNTER = Set.of("другой", "первый", "второй", "третий", "четвертый",
            "пятый", "шестой", "седьмой", "восьмой", "девятый", "десятый");
    private static final Set<String> ANOTHER = Set.of("умер", "не указан", "умерший", "солдатка", "солдатки", "незаконнорожденый",
            "незаконнорожденая", "пономарь", "дьякон", "солдат", "пахотный солдат", "иерей", "того округа", "церковника дочь",
            "церковник", "мещанин", "дьячек", "дьяконица", "дьякононица", "церковникова", "протопица", "того града",
            "кр-нин", "мещанка", "дьяконщица", "нижнего земского суда", "попадья", "прапорщик", "живущая крестьянка", "живущая",
            "крестьянка", "скопинский", "купчиха", "купец", "дьячиха", "пятницкий", "из стрельцов", "государственный",
            "священник", "пономариха", "мещанская", "священницкая", "девка", "крестьянин", "отставной", "дьяконова", "капитан",
            "служитель", "конюх");
    private static final Set<String> OBSCURE_DATA = Set.of("?", "-", ",", ".");
    private static final String NEWBORN = "н/р";
    private static final String HUSBAND = "муж";

    private static final Set<String> CHECK_LIST = new HashSet<>();
    private static final Set<String> LOCATION = new HashSet<>();

    static {
        CHECK_LIST.addAll(RELATIONSHIPS);
        CHECK_LIST.addAll(ARRANGED_MARRIAGE);
        CHECK_LIST.addAll(ARRANGED_WIFE);
        CHECK_LIST.addAll(WIDOWS);
        CHECK_LIST.addAll(FEMININE_COUNTER);
        CHECK_LIST.addAll(MASCULINE_COUNTER);
        CHECK_LIST.addAll(ANOTHER);
        CHECK_LIST.add(LOCATION_EXCLUDE);
        LOCATION.addAll(TOWN_LOCATION);
        LOCATION.addAll(VILLAGE_LOCATION);
        LOCATION.addAll(HAMLET_LOCATION);
    }

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
            headerData.put(getStringCellValue(cell), cell.getColumnIndex());
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
        if (age.toLowerCase().contains(NEWBORN)) return new Age(BigDecimal.ZERO, NEWBORN);
        if (age.endsWith("д") || age.endsWith("дней")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("дней|д$", "").trim());
            return new Age(formatAge, "дни");
        }
        if (age.endsWith("н") || age.endsWith("недель")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("недель|н$", "").trim());
            return new Age(formatAge, "недели");
        }
        if (age.endsWith("м") || age.endsWith("месяцев")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("месяцев|м$", "").trim());
            return new Age(formatAge, "месяцы");
        }
        return new Age(new BigDecimal(age), "года");
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
            case CellType.STRING -> cell.getStringCellValue();
            case CellType.NUMERIC -> {
                String value = String.valueOf(cell.getNumericCellValue());
                if (value.endsWith(".0")) {
                    yield value.substring(0, value.length() - 2);
                }
                yield value;
            }
            case CellType.BLANK -> null;
            case CellType.BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> throw new IllegalArgumentException("Параметр в excel задан в неверном формате");
        };
    }

    /**
     * Parse string to map with full name and status
     *
     * @param notParsingFullName string for parsing
     * @return map with full name and status
     */
    public static FullName parseFullNameCell(String notParsingFullName) {
        if (StringUtils.isEmpty(notParsingFullName)) return null;
        if (HYPHEN.equals(notParsingFullName)) return null;
        FullName fullName = new FullName();
        notParsingFullName = notParsingFullName.replaceAll("его|^|\\(|\\)$", "");
        if (contains(notParsingFullName, TWIN)) {
            String twinName = StringUtils.substringAfter(notParsingFullName, TWIN);
            notParsingFullName = StringUtils.substringBefore(notParsingFullName, TWIN);
            fullName.setStatus(TWIN + " " + twinName);
        }
        if (contains(notParsingFullName, HUSBAND)) {
            String husbandName = StringUtils.substringAfter(notParsingFullName, HUSBAND);
            notParsingFullName = StringUtils.substringBefore(notParsingFullName, HUSBAND);
            fullName.setStatus(HUSBAND + " " + husbandName);
        }
        notParsingFullName = removeSubstringAndUpdateStatus(fullName, CHECK_LIST, notParsingFullName);
        for (String settlement : SETTLEMENT) {
            if (contains(notParsingFullName, settlement)) {
                String subString = StringUtils.substringBetween(" ", " " + settlement);
                String status = fullName.getStatus();
                subString = subString + " " + settlement;
                status = status != null ? status + ", " + subString : subString;
                fullName.setStatus(status);
                notParsingFullName = notParsingFullName.replaceAll(subString, "");
            }
        }
        for (String locate : LOCATION) {
            if (notParsingFullName.contains(locate)) {
                String status = fullName.getStatus();
                String location = locate + " " + StringUtils.substringBetween(locate + " ", " ");
                status = status != null ? status + ", " + location : location;
                fullName.setStatus(status);
                notParsingFullName = notParsingFullName.replaceAll(location, "");
                break;
            }
        }
        String[] separateFullName = StringUtils.split(notParsingFullName.replaceAll("\\s+", " ").trim(), " ");
        Iterator<String> iterator = Arrays.stream(separateFullName).iterator();
        if (iterator.hasNext()) {
            fullName.setName(StringUtils.capitalize(iterator.next()));
        }
        if (iterator.hasNext()) {
            fullName.setSurname(StringUtils.capitalize(iterator.next()));
        }
        if (iterator.hasNext()) {
            fullName.setLastName(StringUtils.capitalize(iterator.next()));
        }
        if (iterator.hasNext()) {
            String status = fullName.getStatus();
            String remaining = iterator.next();
            status = status != null ? status + ", " + remaining : remaining;
            fullName.setStatus(status);
        }
        return fullName;
    }

    /**
     * Update status column in excel
     *
     * @param excelSheet               excel
     * @param successParsingRowNumbers success parsing row numbers
     */
    public static void updateStatus(Sheet excelSheet, List<Integer> successParsingRowNumbers, int statusColumnNumber) {
        CellStyle cellStyle = excelSheet.getRow(0).getCell(1).getCellStyle();
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

    private static String removeSubstringAndUpdateStatus(FullName fullName, Set<String> subStrings, String notParsingFullName) {
        for (String subString : subStrings) {
            if (contains(notParsingFullName.toLowerCase(), subString)) {
                notParsingFullName = StringUtils.remove(notParsingFullName, subString);
                String status = fullName.getStatus();
                status = status != null ? status + ", " + subString : subString;
                fullName.setStatus(status);
            }
        }
        return notParsingFullName.trim();
    }

    private static Cell checkAndReturnFromRow(Row row, Integer cellNum) {
        if (row == null) return null;
        if (cellNum == null) return null;
        return row.getCell(cellNum);
    }
}
