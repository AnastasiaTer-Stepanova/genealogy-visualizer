package genealogy.visualizer.parser;

import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.enums.WitnessType;
import genealogy.visualizer.entity.model.Witness;
import genealogy.visualizer.parser.impl.MarriageSheetParser;
import genealogy.visualizer.service.MarriageDAO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class MarriageSheetParserTest extends AbstractTest {

    private static final String sheetName = "Marriage";
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
    private static final String HUSBAND_WITNESS_COLUMN_NAME_PREFIX = "HusbandWitness";
    private static final String WIFE_WITNESS_COLUMN_NAME_PREFIX = "WifeWitness";
    private static final String COMMENT_COLUMN_NAME = "Comment";
    private static final String FIRST_HUSBAND_WITNESS_COLUMN_NAME = "HusbandWitness1";
    private static final String SECOND_HUSBAND_WITNESS_COLUMN_NAME = "HusbandWitness2";
    private static final String THIRD_HUSBAND_WITNESS_COLUMN_NAME = "HusbandWitness3";
    private static final String FIRST_WIFE_WITNESS_COLUMN_NAME = "WifeWitness1";
    private static final String SECOND_WIFE_WITNESS_COLUMN_NAME = "WifeWitness2";
    private static final String THIRD_WIFE_WITNESS_COLUMN_NAME = "WifeWitness3";

    private static Map<String, Integer> headers;

    @Mock
    private MarriageDAO marriageDAO;

    private SheetParser sheetParser;

    private List<Marriage> marriages;

    @BeforeEach
    void setUp() {
        super.setUp();
        marriages = generateMarriages();
        headers = new HashMap<>();
        headers.put(DATE_COLUMN_NAME, 0);
        headers.put(HUSBAND_LOCALITY_COLUMN_NAME, 1);
        headers.put(HUSBAND_FATHER_COLUMN_NAME, 2);
        headers.put(HUSBAND_COLUMN_NAME, 3);
        headers.put(HUSBAND_MARRIAGE_NUMBER_COLUMN_NAME, 4);
        headers.put(HUSBAND_AGE_COLUMN_NAME, 5);
        headers.put(WIFE_LOCALITY_COLUMN_NAME, 6);
        headers.put(WIFE_FATHER_COLUMN_NAME, 7);
        headers.put(WIFE_COLUMN_NAME, 8);
        headers.put(WIFE_MARRIAGE_NUMBER_COLUMN_NAME, 9);
        headers.put(WIFE_AGE_COLUMN_NAME, 10);
        headers.put(FIRST_HUSBAND_WITNESS_COLUMN_NAME, 11);
        headers.put(SECOND_HUSBAND_WITNESS_COLUMN_NAME, 12);
        headers.put(THIRD_HUSBAND_WITNESS_COLUMN_NAME, 13);
        headers.put(FIRST_WIFE_WITNESS_COLUMN_NAME, 14);
        headers.put(SECOND_WIFE_WITNESS_COLUMN_NAME, 15);
        headers.put(THIRD_WIFE_WITNESS_COLUMN_NAME, 16);
        headers.put(COMMENT_COLUMN_NAME, 17);
        sheetParser = new MarriageSheetParser(marriageDAO, archiveDocumentDAO);
    }

    @Test
    void checkParseTest() throws IOException {
        when(marriageDAO.save(any())).thenReturn(marriages.getFirst());
        Sheet sheet = createXSSFWorkbook(marriages);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        Map<String, String> parsingParams = getParsingParams();
        sheetParser.parse(result, parsingParams);

        assertSheet(result, sheet, true, false);
    }

    @Test
    void checkParseExceptSaveTest() throws IOException {
        doThrow(new RuntimeException()).when(marriageDAO).save(any());
        Sheet sheet = createXSSFWorkbook(marriages);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        Map<String, String> parsingParams = getParsingParams();
        sheetParser.parse(result, parsingParams);

        assertSheet(result, sheet, true, true);
    }

    private List<Marriage> generateMarriages() {
        List<Marriage> marriages = generator.objects(Marriage.class, generator.nextInt(5, 15)).toList();
        for (Marriage marriage : marriages) {
            marriage.setWitnesses(new ArrayList<>());
            int count = generator.nextInt(4);
            if (count > 0) {
                List<Witness> wifeWitnesses = generator.objects(Witness.class, generator.nextInt(count)).toList();
                wifeWitnesses.forEach(w -> w.setWitnessType(WitnessType.WIFE));
                List<Witness> husbandWitnesses = generator.objects(Witness.class, generator.nextInt(count)).toList();
                husbandWitnesses.forEach(w -> w.setWitnessType(WitnessType.HUSBAND));
                marriage.getWitnesses().addAll(wifeWitnesses);
                marriage.getWitnesses().addAll(husbandWitnesses);
            }
        }
        return marriages;
    }

    private Sheet createXSSFWorkbook(List<Marriage> marriages) throws IOException {
        Sheet sheet = createXSSFWorkbook(headers, sheetName);
        for (Marriage marriage : marriages) {
            addRow(sheet, marriage);
        }
        return sheet;
    }

    private void addRow(Sheet sheet, Marriage marriage) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(headers.get(DATE_COLUMN_NAME)).setCellValue(marriage.getDate().format(dateFormat));
        row.createCell(headers.get(HUSBAND_LOCALITY_COLUMN_NAME)).setCellValue(marriage.getHusbandLocality().getName());
        row.createCell(headers.get(HUSBAND_FATHER_COLUMN_NAME)).setCellValue(getFullName(marriage.getHusbandsFather()));
        row.createCell(headers.get(HUSBAND_COLUMN_NAME)).setCellValue(getFullName(marriage.getHusband()));
        row.createCell(headers.get(HUSBAND_MARRIAGE_NUMBER_COLUMN_NAME)).setCellValue(marriage.getHusbandMarriageNumber());
        row.createCell(headers.get(HUSBAND_AGE_COLUMN_NAME)).setCellValue(marriage.getHusbandAge().getAge().toString());
        row.createCell(headers.get(WIFE_LOCALITY_COLUMN_NAME)).setCellValue(marriage.getWifeLocality().getName());
        row.createCell(headers.get(WIFE_FATHER_COLUMN_NAME)).setCellValue(getFullName(marriage.getWifesFather()));
        row.createCell(headers.get(WIFE_COLUMN_NAME)).setCellValue(getFullName(marriage.getWife()));
        row.createCell(headers.get(WIFE_MARRIAGE_NUMBER_COLUMN_NAME)).setCellValue(marriage.getWifeMarriageNumber());
        row.createCell(headers.get(WIFE_AGE_COLUMN_NAME)).setCellValue(marriage.getWifeAge().getAge().toString());
        row.createCell(headers.get(COMMENT_COLUMN_NAME)).setCellValue(marriage.getComment());
        List<Witness> witnesses = marriage.getWitnesses();
        if (witnesses != null && !witnesses.isEmpty()) {
            int wifeWitnessesCount = 0;
            int husbandWitnessesCount = 0;
            for (Witness witness : witnesses) {
                if (witness.getWitnessType().equals(WitnessType.WIFE)) {
                    wifeWitnessesCount++;
                    row.createCell(headers.get(WIFE_WITNESS_COLUMN_NAME_PREFIX + wifeWitnessesCount))
                            .setCellValue(getLocality(witness.getLocality()) + " " + getFullName(witness.getFullName()));
                } else if (witness.getWitnessType().equals(WitnessType.HUSBAND)) {
                    husbandWitnessesCount++;
                    row.createCell(headers.get(HUSBAND_WITNESS_COLUMN_NAME_PREFIX + husbandWitnessesCount))
                            .setCellValue(getLocality(witness.getLocality()) + " " + getFullName(witness.getFullName()));
                }
            }
        }
    }

}