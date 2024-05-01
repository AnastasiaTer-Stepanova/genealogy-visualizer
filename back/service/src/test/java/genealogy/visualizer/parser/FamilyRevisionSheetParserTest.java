package genealogy.visualizer.parser;

import genealogy.visualizer.entity.AnotherNameInRevision;
import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.FullName;
import genealogy.visualizer.parser.impl.FamilyRevisionSheetParser;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class FamilyRevisionSheetParserTest extends AbstractTest {

    private static final String sheetName = "FamilyRevision";
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
                List<AnotherNameInRevision> anotherNamesInRevision = generator.objects(AnotherNameInRevision.class, generator.nextInt(1, 3)).toList();
                familyRevision.setAnotherNames(anotherNamesInRevision);
            }
        }
        return familyRevisions;
    }

    private Sheet createXSSFWorkbook(List<FamilyRevision> familyRevisions) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        workbook.close();
        Sheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);
        for (Map.Entry<String, Integer> entry : headers.entrySet()) {
            row.createCell(entry.getValue()).setCellValue(entry.getKey());
        }
        for (FamilyRevision familyRevision : familyRevisions) {
            addRow(sheet, familyRevision);
        }
        return sheet;
    }

    private void addRow(Sheet sheet, FamilyRevision familyRevision) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(headers.get(FAMILY_REVISION_NUMBER_COLUMN_NAME)).setCellValue(familyRevision.getFamilyRevisionNumber());
        row.createCell(headers.get(PREVIOUS_FAMILY_REVISION_NUMBER_COLUMN_NAME))
                .setCellValue(familyRevision.getPreviousFamilyRevisionNumber());
        row.createCell(headers.get(NEXT_FAMILY_REVISION_NUMBER_COLUMN_NAME)).setCellValue(familyRevision.getNextFamilyRevisionNumber());
        row.createCell(headers.get(LIST_NUMBER_COLUMN_NAME)).setCellValue(familyRevision.getListNumber());
        row.createCell(headers.get(HEAD_OF_YARD_LAST_NAME_COLUMN_NAME)).setCellValue(familyRevision.getHeadOfYard());
        row.createCell(headers.get(LAST_NAME_COLUMN_NAME)).setCellValue(familyRevision.getFullName().getLastName());
        row.createCell(headers.get(LAST_NAME_ANOTHER_COLUMN_NAME)).setCellValue(getAnotherName(familyRevision.getAnotherNames()));
        row.createCell(headers.get(FULL_NAME_COLUMN_NAME)).setCellValue(getFullName(familyRevision));
        row.createCell(headers.get(AGE_COLUMN_NAME))
                .setCellValue(familyRevision.getAge().getAge() + familyRevision.getAge().getAgeType());
        row.createCell(headers.get(AGE_IN_PREVIOUS_REVISION_COLUMN_NAME))
                .setCellValue(familyRevision.getAgeInPreviousRevision().getAge() + familyRevision.getAgeInPreviousRevision().getAgeType());
        row.createCell(headers.get(AGE_IN_NEXT_REVISION_COLUMN_NAME))
                .setCellValue(familyRevision.getAgeInNextRevision().getAge() + familyRevision.getAgeInNextRevision().getAgeType());
        row.createCell(headers.get(DEPARTED_COLUMN_NAME)).setCellValue(familyRevision.getDeparted());
        row.createCell(headers.get(ARRIVED_COLUMN_NAME)).setCellValue(familyRevision.getArrived());
    }

    private static String getFullName(FamilyRevision familyRevision) {
        FullName fullName = familyRevision.getFullName();
        return familyRevision.getStatus() + " " +
                fullName.getName() + " " +
                fullName.getSurname() + " " +
                fullName.getLastName() + " ";
    }

    private static String getAnotherName(List<AnotherNameInRevision> anotherNames) {
        if (anotherNames == null) return null;
        StringBuilder anotherNameString = new StringBuilder();
        for (AnotherNameInRevision anotherNameInRevision : anotherNames) {
            anotherNameString.append(anotherNameInRevision).append(", ");
        }
        return anotherNameString.toString();
    }
}