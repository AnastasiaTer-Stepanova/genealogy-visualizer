package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.AnotherNameInRevision;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.ArchiveDocumentType;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.FullName;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.util.ParserUtils;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.parser.util.ParserUtils.FIRST_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.LAST_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.PERSON_STATUS;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_COLUMN_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_IMPORTED;
import static genealogy.visualizer.parser.util.ParserUtils.SURNAME;
import static genealogy.visualizer.parser.util.ParserUtils.WITHOUT_FIRST_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.getHeaderWithStatusColumn;
import static genealogy.visualizer.parser.util.ParserUtils.getShortCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.parseAge;
import static genealogy.visualizer.parser.util.ParserUtils.parseFullNameCell;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;

/**
 * Парсер для {@link FamilyRevision}
 */
public class FamilyRevisionSheetParser implements SheetParser {

    private static final Logger LOGGER = LogManager.getLogger(FamilyRevisionSheetParser.class);

    private static final String FAMILY_REVISION_NUMBER_COLUMN_NAME = "familyRevisionNumber"; //номер семьи в данной ревизии
    private static final String PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME = "previousFamilyRevisionNumber"; //номер семьи в предыдущей ревизии
    private static final String NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME = "nextFamilyRevisionNumber"; //номер семьи в следующей ревизии
    private static final String LIST_NUMBER_COLUMN_NAME = "listNumber"; //номер страницы в деле на котором указана семья
    private static final String HEAD_OF_YARD_LAST_NAME_COLUMN_NAME = "headOfYardLastName"; //фамилия главы дома
    private static final String LAST_NAME_COLUMN_NAME = "lastName"; //фамилия
    private static final String LAST_NAME_ANOTHER_COLUMN_NAME = "lastNameAnother"; //другие возможные фамилиии
    private static final String FULL_NAME_COLUMN_NAME = "fullName"; //ФИО
    private static final String AGE_COLUMN_NAME = "age"; //возраст на мемент записи в ревизию
    private static final String AGE_IN_PREVIOUS_REVISION_COLUMN_NAME = "ageInPreviousRevision"; //возраст на момент предыдущей ревизии
    private static final String AGE_IN_NEXT_REVISION_COLUMN_NAME = "ageInNextRevision"; //возраст на момент следующей ревизии
    private static final String DEPARTED_COLUMN_NAME = "departed"; //комментарий о выбытии/смерти/рекрутинга в армию
    private static final String ARRIVED_COLUMN_NAME = "arrived"; //комментарий о том откуда прибыли

    private final FamilyRevisionDAO familyRevisionDAO;

    public FamilyRevisionSheetParser(FamilyRevisionDAO familyRevisionDAO) {
        this.familyRevisionDAO = familyRevisionDAO;
    }

    @Override
    public void parse(Sheet excelSheet, ArchiveDocument archive) {
        if (excelSheet == null) throw new NullPointerException("ExcelSheet is null");
        Map<String, Integer> header = getHeaderWithStatusColumn(excelSheet);
        List<FamilyRevision> familyRevision = new ArrayList<>();
        List<Integer> successParsingRowNumbers = new ArrayList<>();
        List<Integer> rowNumbers = new ArrayList<>();
        int lastRowNum = excelSheet.getLastRowNum();
        for (Row row : excelSheet) {
            String status = getStringCellValue(row, header.get(STATUS_COLUMN_NAME));
            int rowNum = row.getRowNum();
            if (rowNum == 0 || STATUS_IMPORTED.equals(status)) {
                continue;
            }
            Short currentFamilyRevisionNumber = getShortCellValue(row, header.get(FAMILY_REVISION_NUMBER_COLUMN_NAME));
            if (currentFamilyRevisionNumber == null) {
                LOGGER.error("Failed to parse row because {} is blank in {} row", FAMILY_REVISION_NUMBER_COLUMN_NAME, row.getRowNum());
                continue;
            }
            Map<String, String> fullName = getFullName(row, header);
            FamilyRevision familyRevisionPerson = new FamilyRevision(
                    null,
                    currentFamilyRevisionNumber,
                    getShortCellValue(row, header.get(PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME)),
                    getShortCellValue(row, header.get(NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME)),
                    getShortCellValue(row, header.get(LIST_NUMBER_COLUMN_NAME)),
                    getStringCellValue(row, header.get(HEAD_OF_YARD_LAST_NAME_COLUMN_NAME)) != null ? Boolean.TRUE : Boolean.FALSE,
                    fullName.get(PERSON_STATUS),
                    new FullName(fullName.get(LAST_NAME), fullName.get(FIRST_NAME), fullName.get(SURNAME)),
                    parseAge(getStringCellValue(row, header.get(AGE_COLUMN_NAME))),
                    parseAge(getStringCellValue(row, header.get(AGE_IN_PREVIOUS_REVISION_COLUMN_NAME))),
                    parseAge(getStringCellValue(row, header.get(AGE_IN_NEXT_REVISION_COLUMN_NAME))),
                    getStringCellValue(row, header.get(DEPARTED_COLUMN_NAME)),
                    getStringCellValue(row, header.get(ARRIVED_COLUMN_NAME)),
                    getAnotherNamesFromCell(row.getCell(header.get(LAST_NAME_ANOTHER_COLUMN_NAME))),
                    archive
            );
            familyRevision.add(familyRevisionPerson);
            rowNumbers.add(rowNum);

            if (lastRowNum != rowNum) {
                Short nextFamilyRevisionNumber = getShortCellValue(
                        excelSheet.getRow(rowNum + 1), header.get(FAMILY_REVISION_NUMBER_COLUMN_NAME));
                if (nextFamilyRevisionNumber.equals(currentFamilyRevisionNumber)) {
                    continue;
                }
            }

            try {
                familyRevisionDAO.saveBatch(familyRevision);
                successParsingRowNumbers.addAll(rowNumbers);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to save family revision with number: %s", currentFamilyRevisionNumber), e);
            }
            rowNumbers.clear();
            familyRevision.clear();
        }
        ParserUtils.updateStatus(excelSheet, successParsingRowNumbers, header.get(STATUS_COLUMN_NAME));
    }

    @Override
    public ArchiveDocumentType type() {
        return ArchiveDocumentType.RL;
    }

    private static Map<String, String> getFullName(Row row, Map<String, Integer> header) {
        Map<String, String> fullName = parseFullNameCell(getStringCellValue(row, header.get(FULL_NAME_COLUMN_NAME)));
        String firstNameFromFullName = fullName.get(LAST_NAME);
        String lastName;
        if (firstNameFromFullName != null) {
            lastName = firstNameFromFullName;
        } else {
            lastName = getStringCellValue(row, header.get(LAST_NAME_COLUMN_NAME));
            lastName = WITHOUT_FIRST_NAME.equals(lastName) ? null : lastName;
        }
        fullName.put(LAST_NAME, StringUtils.capitalize(lastName));
        return fullName;
    }

    private static List<AnotherNameInRevision> getAnotherNamesFromCell(Cell cell) {
        List<AnotherNameInRevision> anotherNames = new ArrayList<>();
        String anotherNameString = cell.getStringCellValue();
        if (isNotBlank(anotherNameString)) {
            String[] names = split(anotherNameString, " /");
            for (byte i = 0; i < names.length; i++) {
                anotherNames.add(new AnotherNameInRevision(null, (byte) (i + 1), StringUtils.capitalize(names[i].replaceAll("^\\(|\\)$", "").trim()), null));
            }
        }
        return anotherNames;
    }
}
