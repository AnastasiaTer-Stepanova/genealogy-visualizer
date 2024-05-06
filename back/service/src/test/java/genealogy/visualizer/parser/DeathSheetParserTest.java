package genealogy.visualizer.parser;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.parser.impl.DeathSheetParser;
import genealogy.visualizer.service.impl.DeathDAOImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class DeathSheetParserTest extends AbstractTest {

    private static final String sheetName = "Death";

    private static final String DATE_COLUMN_NAME = "Date";
    private static final String LOCALITY_COLUMN_NAME = "Locality";
    private static final String RELATIVE_COLUMN_NAME = "Relative";
    private static final String FULL_NAME_COLUMN_NAME = "FullName";
    private static final String AGE_COLUMN_NAME = "Age";
    private static final String CAUSE_COLUMN_NAME = "Cause";
    private static final String BURIAL_PLACE_COLUMN_NAME = "BurialPlace";
    private static final String COMMENT_COLUMN_NAME = "Comment";

    private static Map<String, Integer> headers;

    @Mock
    private DeathDAOImpl deathDAO;

    private SheetParser sheetParser;

    private ArchiveDocument archiveDocument;

    private List<Death> deaths;

    @BeforeEach
    void setUp() {
        Archive archive = generator.nextObject(Archive.class);
        archiveDocument = generator.nextObject(ArchiveDocument.class);
        archiveDocument.setArchive(archive);
        deaths = generator.objects(Death.class, generator.nextInt(5, 15)).toList();
        headers = new HashMap<>();
        headers.put(DATE_COLUMN_NAME, 0);
        headers.put(LOCALITY_COLUMN_NAME, 1);
        headers.put(RELATIVE_COLUMN_NAME, 2);
        headers.put(FULL_NAME_COLUMN_NAME, 3);
        headers.put(AGE_COLUMN_NAME, 4);
        headers.put(CAUSE_COLUMN_NAME, 5);
        headers.put(BURIAL_PLACE_COLUMN_NAME, 6);
        headers.put(COMMENT_COLUMN_NAME, 7);
        sheetParser = new DeathSheetParser(deathDAO);
    }

    @Test
    void checkParseTest() throws IOException {
        doNothing().when(deathDAO).save(any());
        Sheet sheet = createXSSFWorkbook(deaths);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        sheetParser.parse(result, archiveDocument);

        assertSheet(result, sheet, true, false);
    }

    @Test
    void checkParseExceptSaveTest() throws IOException {
        doThrow(new RuntimeException()).when(deathDAO).save(any());
        Sheet sheet = createXSSFWorkbook(deaths);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        sheetParser.parse(result, archiveDocument);

        assertSheet(result, sheet, true, true);
    }

    private Sheet createXSSFWorkbook(List<Death> deaths) throws IOException {
        Sheet sheet = createXSSFWorkbook(headers, sheetName);
        for (Death death : deaths) {
            addRow(sheet, death);
        }
        return sheet;
    }

    private void addRow(Sheet sheet, Death death) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(headers.get(DATE_COLUMN_NAME)).setCellValue(dateFormat.format(death.getDate()));
        row.createCell(headers.get(LOCALITY_COLUMN_NAME)).setCellValue(death.getLocality().getName());
        row.createCell(headers.get(RELATIVE_COLUMN_NAME)).setCellValue(getFullName(death.getRelative()));
        row.createCell(headers.get(FULL_NAME_COLUMN_NAME)).setCellValue(getFullName(death.getFullName()));
        row.createCell(headers.get(AGE_COLUMN_NAME)).setCellValue(death.getAge().getAge() + death.getAge().getAgeType());
        row.createCell(headers.get(CAUSE_COLUMN_NAME)).setCellValue(death.getCause());
        row.createCell(headers.get(BURIAL_PLACE_COLUMN_NAME)).setCellValue(death.getBurialPlace());
        row.createCell(headers.get(COMMENT_COLUMN_NAME)).setCellValue(death.getComment());
    }

}