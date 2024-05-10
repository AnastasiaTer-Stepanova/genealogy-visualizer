package genealogy.visualizer.parser;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.parser.impl.FamilyRevisionSheetParser;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.entity.enums.Sex.MALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class FamilyRevisionSheetParserTest extends AbstractTest {

    private static final String sheetName = "FamilyRevision";
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
    private static final String MALE_COLUMN_NAME = "Male";
    private static final String FEMALE_COLUMN_NAME = "Female";

    private static Map<String, Integer> headers;

    @Mock
    private FamilyRevisionDAO familyRevisionDAO;

    private SheetParser sheetParser;

    private ArchiveDocument archiveDocument;

    private List<FamilyRevision> familyRevisions;

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setArchive(archive);
        familyRevisions = generateFamilyRevisions();
        headers = new HashMap<>();
        headers.put(FAMILY_REVISION_NUMBER_COLUMN_NAME, 0);
        headers.put(PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME, 1);
        headers.put(NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME, 2);
        headers.put(LIST_NUMBER_COLUMN_NAME, 3);
        headers.put(HEAD_OF_YARD_LAST_NAME_COLUMN_NAME, 4);
        headers.put(LAST_NAME_COLUMN_NAME, 5);
        headers.put(LAST_NAME_ANOTHER_COLUMN_NAME, 6);
        headers.put(FULL_NAME_COLUMN_NAME, 7);
        headers.put(AGE_COLUMN_NAME, 8);
        headers.put(AGE_IN_PREVIOUS_REVISION_COLUMN_NAME, 9);
        headers.put(AGE_IN_NEXT_REVISION_COLUMN_NAME, 10);
        headers.put(DEPARTED_COLUMN_NAME, 11);
        headers.put(ARRIVED_COLUMN_NAME, 12);
        headers.put(COMMENT_COLUMN_NAME, 13);
        headers.put(FAMILY_GENERATION_COLUMN_NAME_PREFIX + 1, 14);
        headers.put(FAMILY_GENERATION_COLUMN_NAME_PREFIX + 2, 15);
        headers.put(FAMILY_GENERATION_COLUMN_NAME_PREFIX + 3, 16);
        headers.put(FAMILY_GENERATION_COLUMN_NAME_PREFIX + 4, 17);
        headers.put(FAMILY_GENERATION_COLUMN_NAME_PREFIX + 5, 18);
        headers.put(MALE_COLUMN_NAME, 19);
        headers.put(FEMALE_COLUMN_NAME, 20);
        sheetParser = new FamilyRevisionSheetParser(familyRevisionDAO);
    }

    @Test
    void checkParseTest() throws IOException {
        doNothing().when(familyRevisionDAO).saveBatch(any());
        Sheet sheet = createXSSFWorkbook(familyRevisions);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        sheetParser.parse(result, archiveDocument);

        assertSheet(result, sheet, true, false);
    }

    @Test
    void checkParseExceptSaveTest() throws IOException {
        doThrow(new RuntimeException()).when(familyRevisionDAO).saveBatch(any());
        Sheet sheet = createXSSFWorkbook(familyRevisions);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        sheetParser.parse(result, archiveDocument);

        assertSheet(result, sheet, true, true);
    }

    private List<FamilyRevision> generateFamilyRevisions() {
        List<FamilyRevision> familyRevisions = generator.objects(FamilyRevision.class, generator.nextInt(5, 15)).toList();
        for (FamilyRevision familyRevision : familyRevisions) {
            if (generator.nextBoolean()) {
                List<String> anotherNamesInRevision = generator.objects(String.class, generator.nextInt(1, 3)).toList();
                familyRevision.setAnotherNames(anotherNamesInRevision);
            }
            familyRevision.setFamilyGeneration((byte) generator.nextInt(1, 6));
        }
        return familyRevisions;
    }

    private Sheet createXSSFWorkbook(List<FamilyRevision> familyRevisions) throws IOException {
        Sheet sheet = createXSSFWorkbook(headers, sheetName);
        for (FamilyRevision familyRevision : familyRevisions) {
            addRow(sheet, familyRevision);
        }
        return sheet;
    }

    private void addRow(Sheet sheet, FamilyRevision familyRevision) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        if (MALE.equals(familyRevision.getSex())) {
            row.createCell(headers.get(MALE_COLUMN_NAME)).setCellValue("*");
            row.createCell(headers.get(FEMALE_COLUMN_NAME)).setCellValue("");
        } else {
            row.createCell(headers.get(MALE_COLUMN_NAME)).setCellValue("");
            row.createCell(headers.get(FEMALE_COLUMN_NAME)).setCellValue("*");
        }
        row.createCell(headers.get(FAMILY_REVISION_NUMBER_COLUMN_NAME)).setCellValue(familyRevision.getFamilyRevisionNumber());
        row.createCell(headers.get(PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME))
                .setCellValue(familyRevision.getPreviousFamilyRevisionNumber());
        row.createCell(headers.get(NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME)).setCellValue(familyRevision.getNextFamilyRevisionNumber());
        row.createCell(headers.get(LIST_NUMBER_COLUMN_NAME)).setCellValue(familyRevision.getListNumber());
        row.createCell(headers.get(HEAD_OF_YARD_LAST_NAME_COLUMN_NAME)).setCellValue(familyRevision.getHeadOfYard());
        row.createCell(headers.get(LAST_NAME_COLUMN_NAME)).setCellValue(familyRevision.getFullName().getLastName());
        row.createCell(headers.get(LAST_NAME_ANOTHER_COLUMN_NAME)).setCellValue(getAnotherName(familyRevision.getAnotherNames()));
        row.createCell(headers.get(FULL_NAME_COLUMN_NAME))
                .setCellValue(getFullName(familyRevision.getFullName()) + " " + getFullName(familyRevision.getRelative()));
        row.createCell(headers.get(AGE_COLUMN_NAME))
                .setCellValue(familyRevision.getAge().getAge() + familyRevision.getAge().getType().getName());
        row.createCell(headers.get(AGE_IN_PREVIOUS_REVISION_COLUMN_NAME))
                .setCellValue(familyRevision.getAgeInPreviousRevision().getAge() + familyRevision.getAgeInPreviousRevision().getType().getName());
        row.createCell(headers.get(AGE_IN_NEXT_REVISION_COLUMN_NAME))
                .setCellValue(familyRevision.getAgeInNextRevision().getAge() + familyRevision.getAgeInNextRevision().getType().getName());
        row.createCell(headers.get(DEPARTED_COLUMN_NAME)).setCellValue(familyRevision.getDeparted());
        row.createCell(headers.get(ARRIVED_COLUMN_NAME)).setCellValue(familyRevision.getArrived());
        row.createCell(headers.get(COMMENT_COLUMN_NAME)).setCellValue(familyRevision.getComment());
        for (int i = 1; i <= 5; i++) {
            Cell familyGenerationCell = row.createCell(headers.get(FAMILY_GENERATION_COLUMN_NAME_PREFIX + i));
            if (familyRevision.getFamilyGeneration() == i) {
                familyGenerationCell.setCellValue("*");
            }
        }
    }

    private static String getAnotherName(List<String> anotherNames) {
        if (anotherNames == null) return null;
        StringBuilder anotherNameString = new StringBuilder();
        for (String anotherNameInRevision : anotherNames) {
            anotherNameString.append(anotherNameInRevision).append(", ");
        }
        return anotherNameString.toString();
    }
}