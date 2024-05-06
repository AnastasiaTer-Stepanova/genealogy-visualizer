package genealogy.visualizer.parser.util;

import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.enums.LocalityType;
import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.FullName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.contains;

/**
 * Утилитарный класс для вспомогательных методов необходимых для парсинга excel файлов
 */
public class ParserUtils {

    public static final String STATUS_IMPORTED = "imported";
    public static final String STATUS_COLUMN_NAME = "Status"; //для первой заливки эта колонка должна быть пустая, в последующем здесь будет проставлен статус imported для семей, которые сохранились в БД
    public static final String WITHOUT_LAST_NAME = "БФ";
    public static final String HYPHEN = "-";

    private static final Logger LOGGER = LogManager.getLogger(ParserUtils.class);

    private static final String LOCATION_EXCLUDE = "того села";
    private static final String TWIN = "близнец";
    private static final String NEWBORN = "н/р";
    private static final Set<String> SETTLEMENT = Set.of("слободы", "уезда", "округа");
    private static final Set<String> TOWN_LOCATION = Set.of("г.", "города", "городя");
    private static final Set<String> VILLAGE_LOCATION = Set.of("с.", "село", "села");
    private static final Set<String> HAMLET_LOCATION = Set.of("д.", "деревня", "деревни");
    private static final Set<String> PHRASES = Set.of("не указан", "церковника дочь", "церковникова дочь", "незаконнорожденные близнецы",
            "того града", "того округа", "нижнего земского суда", "из стрельцов", "живущая крестьянка", "незаконнорожденный сын",
            "у солдатки", "у нее", "незаконнорожденый сын", "у пашенного солдата", "водва(ец)", "церковникова жена",
            "дьякона жена", "дьячкова дочь", "государственный мещанин", "отставной солдат", "отставной конюх", "ученик философии",
            "отставной пономарь", "священницкая девка", "пахотный солдат", "не разборчиво", "скопинский мещанин", "скопинская мещанка",
            "крепостной работник", "первым браком", "вторым браком", "третим браком", "чертвертым браком", "сноха их",
            "сын ее", "дочь ее", "внук ее", "внучка ее", "внук их", "внучка их", "от 1 брака", "от 2 брака", "от 3 брака", "от 4 брака", "от 5 брака",
            "по 1 браку", "по 2 браку", "по 3 браку", "по 4 браку", "по 5 браку", "их мачеха", "от 1 мужа", "от 2 мужа", "от 3 мужа",
            "от 4 мужа", "от 5 мужа", "от 1 жены", "от 2 жены", "от 3 жены", "от 4 жены", "от 5 жены");
    private static final Set<String> RELATIONSHIPS = Set.of("падчерица", "племянница", "шурин", "жена", "брат", "сестра",
            "племянник", "сноха", "двоюродный", "двоюродная", "свекровь", "приемный", "теща", "троюродный", "троюродная",
            "другая жена", "дочь", "внук", "внучка", "тесть", "второбрачная", "зять", "тетя", "мачеха", "тетка", "девица",
            "приемыш", "сват", "племяник");
    private static final Set<String> ARRANGED_MARRIAGE = Set.of("по 1бр", "по 2бр", "по 3бр", "по 4бр", "по 5бр",
            "от 1го брака", "от 2го брака", "от 3го брака", "от 4го брака", "от 5го брака", "от 1бр", "от 2бр", "от 3бр",
            "от 4бр", "от 5бр", "от 1 бр", "от 2 бр", "от 3 бр", "от 4 бр", "от 5 бр", "от1бр", "от2бр", "от3бр", "от4бр", "от5бр");
    private static final Set<String> ARRANGED_WIFE = Set.of("1-я жена", "2-я жена", "3-я жена", "4-я жена", "5-я жена",
            "1 жена", "2 жена", "3 жена", "4 жена", "5 жена");
    private static final Set<String> WIDOWS = Set.of("вд1", "вд2", "вд3", "вд4", "вд5", "водва(ец)", "вдова", "вдовец",
            "вд 1", "вд 2", "вд 3", "вд 4", "вд 5", "вд.");
    private static final Set<String> FEMININE_COUNTER = Set.of("другая", "первая", "вторая", "третья", "четвертая",
            "пятая", "шестая", "седьмая", "восьмая", "девятая", "десятая", "старшая", "младшая");
    private static final Set<String> MASCULINE_COUNTER = Set.of("другой", "первый", "второй", "третий", "четвертый",
            "пятый", "шестой", "седьмой", "восьмой", "девятый", "десятый", "старший", "старшой", "младший", "младшой", "меньшой");
    private static final Set<String> ANOTHER = Set.of("умер", "дьякон ", "солдат", "пономарь", "иерей", "церковник", "мещанин",
            "дьячек", "прапорщик", "купец", "кр-нин", "священник", "крестьянин", "служитель", "конюх", "капитан", "вдов", "сын");
    private static final Set<String> ANOTHER_WITH_SUFFIX = Set.of("умерший", "солдатка", "солдатки", "незаконнорожденый",
            "незаконнорожденая", "дьяконица", "дьякононица", "протопица", "мещанка", "дьяконщица", "попадья", "живущая",
            "крестьянка", "скопинский", "скопинская", "купчиха", "дьячиха", "пятницкий", "государственный", "пономариха", "мещанская",
            "священницкая", "девка", "отставной", "дьяконова", "капитан", "вознесенский", "незаконнорожденный",
            "незаконнорожденная", "пападья", "дьяек", "помещик", "пасынок", "пассынок");
    private static final Set<String> OBSCURE_DATA = Set.of("?", "-", ",", ".");
    private static final Set<String> TRUSTEE = Set.of("муж", "отец", "брат");
    private static final Set<String> EXCLUDE_STATUS = Set.of("его ", "\\...", "\\(", "\\)");

    private static final Set<String> CHECK_LIST = new HashSet<>();

    static {
        CHECK_LIST.addAll(RELATIONSHIPS);
        CHECK_LIST.addAll(ARRANGED_MARRIAGE);
        CHECK_LIST.addAll(WIDOWS);
        CHECK_LIST.addAll(FEMININE_COUNTER);
        CHECK_LIST.addAll(MASCULINE_COUNTER);
        CHECK_LIST.addAll(ANOTHER_WITH_SUFFIX);
        CHECK_LIST.add(LOCATION_EXCLUDE);
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
        if (age.toLowerCase().contains(NEWBORN)) return new Age(BigDecimal.ZERO, NEWBORN);
        String prefixRegexp = "\\d+(\\.\\d+)?\\s*";
        if (age.matches(prefixRegexp + "(дней$|дн$|д$)")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("(дней$|дн$|д$)", "").trim());
            return new Age(formatAge, "дни");
        }
        if (age.matches(prefixRegexp + "(недель$|нед$|н$)")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("(недель$|нед$|н$)", "").trim());
            return new Age(formatAge, "недели");
        }
        if (age.matches(prefixRegexp + "(месяцев$|мес$|м$)")) {
            BigDecimal formatAge = new BigDecimal(age.replaceAll("(месяцев$|мес$|м$)", "").trim());
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
    public static Date getDateCellValue(Row row, Integer cellNum) {
        Cell cell = checkAndReturnFromRow(row, cellNum);
        return getDateCellValue(cell);
    }

    public static Date getDateCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case CellType.NUMERIC, CellType.BLANK -> null;
            case CellType.STRING -> {
                try {
                    String cellValue = cell.getStringCellValue();
                    if (HYPHEN.equals(cellValue)) yield null;
                    yield DateUtils.parseDate(cellValue, "dd.MM.yyyy");
                } catch (ParseException e) {
                    LOGGER.error(String.format("Error parsing date %s", cell.getStringCellValue()), e);
                    yield null;
                }
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
     * Get {@link Locality} from string
     *
     * @param notParsingFullName string for parsing
     * @return locality
     */
    public static String parseLocality(Locality locality, String notParsingFullName) {
        if (StringUtils.isEmpty(notParsingFullName)) return null;
        if (HYPHEN.equals(notParsingFullName)) return null;
        for (String settlement : SETTLEMENT) {
            if (contains(notParsingFullName, settlement)) {
                String subString = StringUtils.substringBetween(notParsingFullName, " ", " " + settlement);
                if (subString == null) {
                    subString = StringUtils.substringBefore(notParsingFullName, " " + settlement);
                }
                subString = subString + " " + settlement;
                if (locality != null) {
                    String address = locality.getAddress();
                    locality.setAddress(address != null ? address + StringUtils.capitalize(subString) : StringUtils.capitalize(subString));
                }
                notParsingFullName = notParsingFullName.replaceAll(subString, "");
            }
        }
        for (String location : TOWN_LOCATION) {
            notParsingFullName = updateFullNameStringAndLocality(notParsingFullName, location, locality, LocalityType.TOWN);
        }
        for (String location : VILLAGE_LOCATION) {
            notParsingFullName = updateFullNameStringAndLocality(notParsingFullName, location, locality, LocalityType.VILLAGE);
        }
        for (String location : HAMLET_LOCATION) {
            notParsingFullName = updateFullNameStringAndLocality(notParsingFullName, location, locality, LocalityType.HAMLET);
        }
        return notParsingFullName;
    }

    private static String updateFullNameStringAndLocality(String notParsingFullName, String locate, Locality locality, LocalityType localityType) {
        if (notParsingFullName.toLowerCase().contains(locate) && !notParsingFullName.toLowerCase().contains(LOCATION_EXCLUDE)) {
            String location = StringUtils.substringBetween(notParsingFullName, locate + " ", " ");
            if (location == null) {
                location = StringUtils.substringAfter(notParsingFullName, locate + " ");
            }
            if (locality != null) {
                locality.setName(StringUtils.capitalize(location));
                locality.setType(localityType);
            }
            location = locate + " " + location;
            notParsingFullName = notParsingFullName.replaceAll(location, "").trim();
        }
        return notParsingFullName;
    }

    /**
     * Parse string to {@link FullName} if string has locality info delete this
     *
     * @param notParsingFullName string for parsing
     * @return full name
     */
    public static FullName parseFullNameCell(String notParsingFullName) {
        if (notParsingFullName == null || StringUtils.isEmpty(notParsingFullName)) return null;
        if (HYPHEN.equals(notParsingFullName)) return null;
        notParsingFullName = notParsingFullName.toLowerCase();
        notParsingFullName = parseLocality(null, notParsingFullName); //ignore result, need only delete location info from string
        if (notParsingFullName == null) return null;
        FullName fullName = new FullName();
        notParsingFullName = notParsingFullName.replaceAll("\\(|\\)$", "");
        notParsingFullName = removeSubstringAndUpdateStatus(fullName, PHRASES, notParsingFullName);
        for (String exclude : EXCLUDE_STATUS) {
            notParsingFullName = notParsingFullName.replaceAll(exclude, "");
        }
        if (contains(notParsingFullName, TWIN)) {
            String twinName = StringUtils.substringAfter(notParsingFullName, TWIN);
            notParsingFullName = StringUtils.substringBefore(notParsingFullName, TWIN);
            fullName.setStatus(TWIN + " " + twinName);
        }
        for (String trustee : TRUSTEE) {
            if (contains(notParsingFullName, trustee) && notParsingFullName.matches("\\s" + trustee + "\\s")) {
                String trusteeName = capitalizeAllWord(StringUtils.substringAfter(notParsingFullName, trustee).trim());
                notParsingFullName = StringUtils.substringBefore(notParsingFullName, trustee);
                fullName.setStatus(trustee + " " + trusteeName);
            }
        }
        notParsingFullName = removeSubstringAndUpdateStatus(fullName, ARRANGED_WIFE, notParsingFullName);
        notParsingFullName = removeSubstringAndUpdateStatus(fullName, CHECK_LIST, notParsingFullName);
        notParsingFullName = removeSubstringAndUpdateStatus(fullName, ANOTHER, notParsingFullName);
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

    public static Locality getLocality(Cell cell) {
        String localityString = getStringCellValue(cell);
        if (localityString == null || StringUtils.isEmpty(localityString) || HYPHEN.equals(localityString)) return null;
        Locality locality = new Locality();
        parseLocality(locality, localityString);
        if (locality.getName() == null) {
            locality.setName(localityString);
        }
        return locality;
    }

    private static String capitalizeAllWord(String input) {
        return Arrays.stream(input.split("\\s+"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    private static String removeSubstringAndUpdateStatus(FullName fullName, Set<String> subStrings, String notParsingFullName) {
        for (String subString : subStrings) {
            if (contains(notParsingFullName, subString)) {
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
