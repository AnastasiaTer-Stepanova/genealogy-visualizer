package genealogy.visualizer.parser;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.GodParent;
import genealogy.visualizer.parser.impl.ChristeningSheetParser;
import genealogy.visualizer.service.ChristeningDAO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.entity.enums.Sex.MALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class ChristeningSheetParserTest extends AbstractTest {

    private static final String sheetName = "Christening";
    private static final String MALE_COLUMN_NAME = "Male";
    private static final String FEMALE_COLUMN_NAME = "Female";
    private static final String BIRTH_COLUMN_NAME = "Birth";
    private static final String CHRISTENING_COLUMN_NAME = "Christening";
    private static final String LOCALITY_COLUMN_NAME = "Locality";
    private static final String NAME_COLUMN_NAME = "Name";
    private static final String FATHER_COLUMN_NAME = "Father";
    private static final String MOTHER_COLUMN_NAME = "Mather";
    private static final String FIRST_GOD_PARENT_COLUMN_NAME = "GodParent1";
    private static final String SECOND_GOD_PARENT_COLUMN_NAME = "GodParen2";
    private static final String COMMENT_COLUMN_NAME = "Comment";

    private static Map<String, Integer> headers;

    @Mock
    private ChristeningDAO christeningDAO;

    private SheetParser sheetParser;

    private ArchiveDocument archiveDocument;

    private List<Christening> christenings;

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setArchive(archive);
        christenings = generateChristening();
        headers = new HashMap<>();
        headers.put(MALE_COLUMN_NAME, 0);
        headers.put(FEMALE_COLUMN_NAME, 1);
        headers.put(BIRTH_COLUMN_NAME, 2);
        headers.put(CHRISTENING_COLUMN_NAME, 3);
        headers.put(LOCALITY_COLUMN_NAME, 4);
        headers.put(NAME_COLUMN_NAME, 5);
        headers.put(FATHER_COLUMN_NAME, 6);
        headers.put(MOTHER_COLUMN_NAME, 7);
        headers.put(FIRST_GOD_PARENT_COLUMN_NAME, 8);
        headers.put(SECOND_GOD_PARENT_COLUMN_NAME, 9);
        headers.put(COMMENT_COLUMN_NAME, 10);
        sheetParser = new ChristeningSheetParser(christeningDAO);
    }

    @Test
    void checkParseTest() throws IOException {
        doNothing().when(christeningDAO).save(any());
        Sheet sheet = createXSSFWorkbook(christenings);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        sheetParser.parse(result, archiveDocument);

        assertSheet(result, sheet, true, false);
    }

    @Test
    void checkParseExceptSaveTest() throws IOException {
        doThrow(new RuntimeException()).when(christeningDAO).save(any());
        Sheet sheet = createXSSFWorkbook(christenings);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        sheetParser.parse(result, archiveDocument);

        assertSheet(result, sheet, true, true);
    }

    private List<Christening> generateChristening() {
        List<Christening> christenings = generator.objects(Christening.class, generator.nextInt(5, 15)).toList();
        for (Christening christening : christenings) {
            int count = generator.nextInt(3);
            if (count == 0) {
                christening.setGodParents(null);
            } else {
                christening.setGodParents(generator.objects(GodParent.class, generator.nextInt(count)).toList());
            }
        }
        return christenings;
    }

    private Sheet createXSSFWorkbook(List<Christening> christenings) throws IOException {
        Sheet sheet = createXSSFWorkbook(headers, sheetName);
        for (Christening christening : christenings) {
            addRow(sheet, christening);
        }
        return sheet;
    }

    private void addRow(Sheet sheet, Christening christening) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        if (MALE.equals(christening.getSex())) {
            row.createCell(headers.get(MALE_COLUMN_NAME)).setCellValue("*");
            row.createCell(headers.get(FEMALE_COLUMN_NAME)).setCellValue("");
        } else {
            row.createCell(headers.get(MALE_COLUMN_NAME)).setCellValue("");
            row.createCell(headers.get(FEMALE_COLUMN_NAME)).setCellValue("*");
        }
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        row.createCell(headers.get(BIRTH_COLUMN_NAME)).setCellValue(dateFormat.format(christening.getBirthDate()));
        row.createCell(headers.get(CHRISTENING_COLUMN_NAME)).setCellValue(dateFormat.format(christening.getChristeningDate()));
        row.createCell(headers.get(LOCALITY_COLUMN_NAME)).setCellValue(christening.getLocality().getName());
        row.createCell(headers.get(NAME_COLUMN_NAME)).setCellValue(christening.getName());
        row.createCell(headers.get(FATHER_COLUMN_NAME)).setCellValue(getFullName(christening.getFather()));
        row.createCell(headers.get(MOTHER_COLUMN_NAME)).setCellValue(getFullName(christening.getMother()));
        List<GodParent> godParents = christening.getGodParents();
        if (godParents != null && !godParents.isEmpty()) {
            GodParent firstGodParent = godParents.getFirst();
            String firstGodParentName = getFullName(firstGodParent.getFullName());
            firstGodParentName = firstGodParentName + " " + firstGodParent.getLocality().getType().getName() + " " + firstGodParent.getLocality().getName();
            row.createCell(headers.get(FIRST_GOD_PARENT_COLUMN_NAME)).setCellValue(firstGodParentName);
            if (godParents.size() > 1) {
                GodParent secondGodParent = godParents.get(1);
                String secondGodParentName = getFullName(secondGodParent.getFullName());
                secondGodParentName = secondGodParentName + " " + secondGodParent.getLocality().getType().getName() + " " + secondGodParent.getLocality().getName();
                row.createCell(headers.get(SECOND_GOD_PARENT_COLUMN_NAME)).setCellValue(secondGodParentName);
            }
        }
        row.createCell(headers.get(COMMENT_COLUMN_NAME)).setCellValue(christening.getComment());
    }

}