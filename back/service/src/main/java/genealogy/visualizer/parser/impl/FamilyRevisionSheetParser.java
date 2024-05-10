package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.entity.model.FullName;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.util.ParserUtils;
import genealogy.visualizer.parser.util.StringParserHelper;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.parser.util.ParserUtils.STATUS_COLUMN_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_IMPORTED;
import static genealogy.visualizer.parser.util.ParserUtils.WITHOUT_LAST_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.getHeaderWithStatusColumn;
import static genealogy.visualizer.parser.util.ParserUtils.getSex;
import static genealogy.visualizer.parser.util.ParserUtils.getShortCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.parseAge;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;

/**
 * Парсер для {@link FamilyRevision}
 */
public class FamilyRevisionSheetParser implements SheetParser {

    private static final Logger LOGGER = LogManager.getLogger(FamilyRevisionSheetParser.class);

    private static final String MALE_COLUMN_NAME = "Male";
    private static final String FEMALE_COLUMN_NAME = "Female";
    private static final String FAMILY_REVISION_NUMBER_COLUMN_NAME = "FamilyRevisionNumber";
    private static final String PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME = "PreviousFamilyRevisionNumber";
    private static final String NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME = "NextFamilyRevisionNumber";
    private static final String LIST_NUMBER_COLUMN_NAME = "ListNumber";
    private static final String HEAD_OF_YARD_LAST_NAME_COLUMN_NAME = "HeadOfYardLastName";
    private static final String LAST_NAME_COLUMN_NAME = "LastName";
    private static final String LAST_NAME_ANOTHER_COLUMN_NAME = "LastNameAnother";
    private static final String FULL_NAME_COLUMN_NAME = "FullName";
    private static final String AGE_COLUMN_NAME = "Age";
    private static final String AGE_IN_PREVIOUS_REVISION_COLUMN_NAME = "AgeInPreviousRevision";
    private static final String AGE_IN_NEXT_REVISION_COLUMN_NAME = "AgeInNextRevision";
    private static final String DEPARTED_COLUMN_NAME = "Departed";
    private static final String ARRIVED_COLUMN_NAME = "Arrived";
    private static final String COMMENT_COLUMN_NAME = "Comment";
    private static final String FAMILY_GENERATION_COLUMN_NAME_PREFIX = "G";
    private static final String WIFE_PREFIX = "жена";

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
        FamilyRevision previousPerson = null;
        int lastRowNum = excelSheet.getLastRowNum();
        for (Row row : excelSheet) {
            String status = getStringCellValue(row, header.get(STATUS_COLUMN_NAME));
            int rowNum = row.getRowNum();
            if (rowNum == 0 || STATUS_IMPORTED.equals(status)) {
                continue;
            }
            Short currentFamilyRevisionNumber;
            try {
                currentFamilyRevisionNumber = getShortCellValue(row, header.get(FAMILY_REVISION_NUMBER_COLUMN_NAME));
                if (currentFamilyRevisionNumber == null) {
                    throw new NullPointerException("CurrentFamilyRevisionNumber is null");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse cell {} in {} row", FAMILY_REVISION_NUMBER_COLUMN_NAME, row.getRowNum());
                continue;
            }
            String fullName = getStringCellValue(row, header.get(FULL_NAME_COLUMN_NAME));
            if (StringUtils.isBlank(fullName)) {
                LOGGER.error("FullName is empty in row {}", row.getRowNum());
                continue;
            }
            FamilyRevision partner = null;
            if (previousPerson != null && fullName.startsWith(WIFE_PREFIX) &&
                    previousPerson.getFamilyRevisionNumber().equals(currentFamilyRevisionNumber)) {
                partner = previousPerson;
            }
            StringParserHelper fullNameHelper = new StringParserHelper(fullName);
            FamilyRevision familyRevisionPerson;
            try {
                familyRevisionPerson = new FamilyRevision(
                        null,
                        partner,
                        currentFamilyRevisionNumber,
                        getShortCellValue(row, header.get(PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME)),
                        getShortCellValue(row, header.get(NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME)),
                        getShortCellValue(row, header.get(LIST_NUMBER_COLUMN_NAME)),
                        getStringCellValue(row, header.get(HEAD_OF_YARD_LAST_NAME_COLUMN_NAME)) != null ? Boolean.TRUE : Boolean.FALSE,
                        getFullName(row, header, fullNameHelper.getFullName()),
                        parseAge(getStringCellValue(row, header.get(AGE_COLUMN_NAME))),
                        parseAge(getStringCellValue(row, header.get(AGE_IN_PREVIOUS_REVISION_COLUMN_NAME))),
                        parseAge(getStringCellValue(row, header.get(AGE_IN_NEXT_REVISION_COLUMN_NAME))),
                        getStringCellValue(row, header.get(DEPARTED_COLUMN_NAME)),
                        getStringCellValue(row, header.get(ARRIVED_COLUMN_NAME)),
                        getFamilyGeneration(row, header),
                        getStringCellValue(row, header.get(COMMENT_COLUMN_NAME)),
                        getSex(getStringCellValue(row, header.get(MALE_COLUMN_NAME)), getStringCellValue(row, header.get(FEMALE_COLUMN_NAME))),
                        fullNameHelper.getRelative(),
                        getAnotherNamesFromCell(row.getCell(header.get(LAST_NAME_ANOTHER_COLUMN_NAME))),
                        archive,
                        null);
            } catch (Exception e) {
                LOGGER.error("Failed to parse row {}", row.getRowNum(), e);
                continue;
            }
            familyRevision.add(familyRevisionPerson);
            previousPerson = familyRevisionPerson;
            rowNumbers.add(rowNum);
            if (partner != null && familyRevision.contains(partner)) {
                familyRevision.remove(partner);
                partner.setPartner(familyRevisionPerson);
                familyRevision.add(partner);
            }

            if (lastRowNum != rowNum) {
                Short nextFamilyRevisionNumber = getShortCellValue(
                        excelSheet.getRow(rowNum + 1), header.get(FAMILY_REVISION_NUMBER_COLUMN_NAME));
                if (nextFamilyRevisionNumber != null && nextFamilyRevisionNumber.equals(currentFamilyRevisionNumber)) {
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

    private byte getFamilyGeneration(Row row, Map<String, Integer> header) {
        try {
            return (byte) header.entrySet().stream()
                    .filter(map -> map.getKey() != null && map.getKey().matches("^" + FAMILY_GENERATION_COLUMN_NAME_PREFIX + "\\d$"))
                    .mapToInt(map -> {
                        if (row.getCell(map.getValue()) != null && getStringCellValue(row.getCell(map.getValue())) != null) {
                            return Integer.parseInt(StringUtils.substringAfter(map.getKey(), FAMILY_GENERATION_COLUMN_NAME_PREFIX));
                        }
                        return 1;
                    })
                    .max()
                    .orElse(1);
        } catch (Exception e) {
            LOGGER.error("Failed to parse family generation in row {}", row.getRowNum(), e);
            return 1;
        }
    }

    private static FullName getFullName(Row row, Map<String, Integer> header, FullName fullName) {
        if (fullName == null) return null;
        String firstNameFromFullName = fullName.getLastName();
        String lastName;
        if (firstNameFromFullName != null) {
            lastName = firstNameFromFullName;
        } else {
            lastName = getStringCellValue(row, header.get(LAST_NAME_COLUMN_NAME));
        }
        if (lastName != null) {
            lastName = WITHOUT_LAST_NAME.equalsIgnoreCase(lastName) ? null : lastName;
        }
        fullName.setLastName(StringUtils.capitalize(lastName));
        return fullName;
    }

    private static List<String> getAnotherNamesFromCell(Cell cell) {
        List<String> anotherNames = new ArrayList<>();
        String anotherNameString = cell.getStringCellValue();
        if (isNotBlank(anotherNameString)) {
            return Arrays.stream(split(anotherNameString, " /"))
                    .map(name -> StringUtils.capitalize(name.replaceAll("^\\(|\\)$", "").trim()))
                    .toList();
        }
        return anotherNames;
    }
}
