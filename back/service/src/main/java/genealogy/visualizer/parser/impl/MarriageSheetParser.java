package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.entity.enums.WitnessType;
import genealogy.visualizer.entity.model.FullName;
import genealogy.visualizer.entity.model.Witness;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.service.MarriageDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static genealogy.visualizer.parser.util.ParserUtils.STATUS_COLUMN_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_IMPORTED;
import static genealogy.visualizer.parser.util.ParserUtils.getDateCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.getHeaderWithStatusColumn;
import static genealogy.visualizer.parser.util.ParserUtils.getLocality;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.parseAge;
import static genealogy.visualizer.parser.util.ParserUtils.parseFullNameCell;
import static genealogy.visualizer.parser.util.ParserUtils.parseLocality;
import static genealogy.visualizer.parser.util.ParserUtils.updateStatus;

public class MarriageSheetParser implements SheetParser {

    private static final Logger LOGGER = LogManager.getLogger(MarriageSheetParser.class);

    private static final String DATE_COLUMN_NAME = "Date";
    private static final String HUSBAND_LOCALITY_COLUMN_NAME = "HusbandLocality";
    private static final String HUSBAND_FATHER_COLUMN_NAME = "HusbandFather";
    private static final String HUSBAND_COLUMN_NAME = "Husband";
    private static final String HUSBAND_MARRIAGE_NUMBER_COLUMN_NAME = "HusbandMarriageNumber";
    private static final String HUSBAND_AGE_COLUMN_NAME = "HusbandAge";
    private static final String WIFE_LOCALITY_COLUMN_NAME = "WifeLocality";
    private static final String WIFE_FATHER_COLUMN_NAME = "WifeFather";
    private static final String WIFE_COLUMN_NAME = "Wife";
    private static final String WIFE_MARRIAGE_NUMBER_COLUMN_NAME = "WifeMarriageNumber";
    private static final String WIFE_AGE_COLUMN_NAME = "WifeAge";
    private static final String FIRST_HUSBAND_WITNESS_COLUMN_NAME = "HusbandWitness1";
    private static final String SECOND_HUSBAND_WITNESS_COLUMN_NAME = "HusbandWitness2";
    private static final String THIRD_HUSBAND_WITNESS_COLUMN_NAME = "HusbandWitness3";
    private static final String FIRST_WIFE_WITNESS_COLUMN_NAME = "WifeWitness1";
    private static final String SECOND_WIFE_WITNESS_COLUMN_NAME = "WifeWitness2";
    private static final String THIRD_WIFE_WITNESS_COLUMN_NAME = "WifeWitness3";
    private static final String COMMENT_COLUMN_NAME = "Comment";

    private final MarriageDAO marriageDAO;

    public MarriageSheetParser(MarriageDAO marriageDAO) {
        this.marriageDAO = marriageDAO;
    }

    @Override
    public void parse(Sheet excelSheet, ArchiveDocument archive) {
        if (excelSheet == null) throw new NullPointerException("ExcelSheet is null");
        Map<String, Integer> header = getHeaderWithStatusColumn(excelSheet);
        List<Integer> successParsingRowNumbers = new ArrayList<>();
        for (Row row : excelSheet) {
            String status = getStringCellValue(row, header.get(STATUS_COLUMN_NAME));
            int rowNum = row.getRowNum();
            if (rowNum == 0 || STATUS_IMPORTED.equals(status)) {
                continue;
            }
            Date marriageDate = getDateCellValue(row, header.get(DATE_COLUMN_NAME));
            if (marriageDate == null) {
                LOGGER.error("Marriage date is null for row {}", rowNum);
                continue;
            }
            String wife = getStringCellValue(row, header.get(WIFE_COLUMN_NAME));
            if (wife == null) {
                LOGGER.error("Wife is null for row {}", rowNum);
                continue;
            }
            String husband = getStringCellValue(row, header.get(HUSBAND_COLUMN_NAME));
            if (husband == null) {
                LOGGER.error("Husband is null for row {}", rowNum);
                continue;
            }
            String husbandMarriageNum = getStringCellValue(row, header.get(HUSBAND_MARRIAGE_NUMBER_COLUMN_NAME));
            String wifeMarriageNum = getStringCellValue(row, header.get(WIFE_MARRIAGE_NUMBER_COLUMN_NAME));
            Marriage marriage;
            try {
                marriage = new Marriage(
                        null,
                        marriageDate,
                        getLocality(row.getCell(header.get(HUSBAND_LOCALITY_COLUMN_NAME))),
                        parseFullNameCell(getStringCellValue(row, header.get(HUSBAND_FATHER_COLUMN_NAME))),
                        parseFullNameCell(husband),
                        parseAge(getStringCellValue(row, header.get(HUSBAND_AGE_COLUMN_NAME))),
                        husbandMarriageNum != null ? Byte.parseByte(husbandMarriageNum) : null,
                        getLocality(row.getCell(header.get(WIFE_LOCALITY_COLUMN_NAME))),
                        parseFullNameCell(getStringCellValue(row, header.get(WIFE_FATHER_COLUMN_NAME))),
                        parseFullNameCell(wife),
                        parseAge(getStringCellValue(row, header.get(WIFE_AGE_COLUMN_NAME))),
                        wifeMarriageNum != null ? Byte.parseByte(wifeMarriageNum) : null,
                        getStringCellValue(row, header.get(COMMENT_COLUMN_NAME)),
                        getWitnesses(row, header),
                        archive,
                        null);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to create marriage entity from row: %s", rowNum), e);
                continue;
            }
            try {
                marriageDAO.save(marriage);
                successParsingRowNumbers.add(rowNum);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to save marriage from row: %s", rowNum), e);
            }
        }
        updateStatus(excelSheet, successParsingRowNumbers, header.get(STATUS_COLUMN_NAME));
    }

    @Override
    public ArchiveDocumentType type() {
        return ArchiveDocumentType.PR_MRG;
    }

    private List<Witness> getWitnesses(Row row, Map<String, Integer> header) {
        List<Witness> witnesses = new ArrayList<>();
        witnesses.add(getWitness(row, header.get(FIRST_HUSBAND_WITNESS_COLUMN_NAME), WitnessType.HUSBAND));
        witnesses.add(getWitness(row, header.get(SECOND_HUSBAND_WITNESS_COLUMN_NAME), WitnessType.HUSBAND));
        witnesses.add(getWitness(row, header.get(THIRD_HUSBAND_WITNESS_COLUMN_NAME), WitnessType.HUSBAND));
        witnesses.add(getWitness(row, header.get(FIRST_WIFE_WITNESS_COLUMN_NAME), WitnessType.HUSBAND));
        witnesses.add(getWitness(row, header.get(SECOND_WIFE_WITNESS_COLUMN_NAME), WitnessType.WIFE));
        witnesses.add(getWitness(row, header.get(THIRD_WIFE_WITNESS_COLUMN_NAME), WitnessType.WIFE));
        return witnesses.stream().filter(Objects::nonNull).toList();
    }

    private Witness getWitness(Row row, int cellNum, WitnessType type) {
        String witness = getStringCellValue(row.getCell(cellNum));
        if (witness != null) {
            Witness result = getWitness(witness);
            if (result != null && result.getFullName() != null && result.getFullName().getName() != null) {
                result.setWitnessType(type);
                return result;
            }
        }
        return null;
    }

    private Witness getWitness(String witness) {
        if (witness == null || witness.isEmpty()) return null;
        Locality locality = new Locality();
        witness = parseLocality(locality, witness);
        if (locality.getName() == null || locality.getName().isEmpty()) locality = null;
        FullName fullName = parseFullNameCell(witness);
        if (fullName == null) return null;
        Witness result = new Witness();
        result.setLocality(locality);
        result.setFullName(fullName);
        return result;
    }
}
