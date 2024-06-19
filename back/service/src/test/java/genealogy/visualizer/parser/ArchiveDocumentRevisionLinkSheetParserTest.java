package genealogy.visualizer.parser;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.parser.impl.ArchiveDocumentRevisionLinkSheetParser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class ArchiveDocumentRevisionLinkSheetParserTest extends AbstractTest {

    private static final String sheetName = "ArchiveDocumentRevisionLink";

    private static final String ARCHIVE_NAME_COLUMN_NAME = "ArchiveName";
    private static final String ARCHIVE_ABBREVIATION_COLUMN_NAME = "ArchiveAbbreviation";
    private static final String DOCUMENT_NAME_COLUMN_NAME = "DocumentName";
    private static final String ABBREVIATION_DOCUMENT_NAME_COLUMN_NAME = "AbbreviationDocumentName";
    private static final String NEXT_ARCHIVE_DOCUMENT_COLUMN_NAME = "NextArchiveDocumentNumber";

    private static Map<String, Integer> headers;

    private SheetParser sheetParser;

    private List<ArchiveDocument> archiveDocuments;

    @BeforeEach
    void setUp() {
        super.setUp();
        headers = new HashMap<>();
        archiveDocuments = generateArchiveDocuments();
        headers.put(ARCHIVE_NAME_COLUMN_NAME, 0);
        headers.put(ARCHIVE_ABBREVIATION_COLUMN_NAME, 1);
        headers.put(DOCUMENT_NAME_COLUMN_NAME, 2);
        headers.put(ABBREVIATION_DOCUMENT_NAME_COLUMN_NAME, 3);
        headers.put(NEXT_ARCHIVE_DOCUMENT_COLUMN_NAME, 4);
        headers.put(FUND_PARAM_NAME, 5);
        headers.put(CATALOG_PARAM_NAME, 6);
        headers.put(INSTANCE_PARAM_NAME, 7);
        headers.put(BUNCH_PARAM_NAME, 8);
        headers.put(YEAR_PARAM_NAME, 9);
        sheetParser = new ArchiveDocumentRevisionLinkSheetParser(archiveDocumentDAO);
    }

    @Test
    void checkParseTest() throws IOException {
        doReturn(archiveDocuments.getFirst()).when(archiveDocumentDAO).saveOrFindIfExistDocument(any(ArchiveDocument.class));
        Sheet sheet = createXSSFWorkbook(archiveDocuments);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        Map<String, String> parsingParams = getParsingParams();
        sheetParser.parse(result, parsingParams);

        assertSheet(result, sheet, true, false);
    }

    @Test
    void checkParseExceptSaveTest() throws IOException {
        doThrow(new RuntimeException()).when(archiveDocumentDAO).saveOrFindIfExistDocument(any());
        Sheet sheet = createXSSFWorkbook(archiveDocuments);
        Workbook workbook = sheet.getWorkbook();
        Sheet result = workbook.cloneSheet(0);
        Map<String, String> parsingParams = getParsingParams();
        sheetParser.parse(result, parsingParams);

        assertSheet(result, sheet, true, true);
    }

    private List<ArchiveDocument> generateArchiveDocuments() {
        List<ArchiveDocument> archiveDocuments = generator.objects(ArchiveDocument.class, generator.nextInt(5, 15)).toList();
        for (ArchiveDocument archiveDocument : archiveDocuments) {
            archiveDocument.setArchive(generator.nextObject(Archive.class));
            if (generator.nextBoolean()) {
                archiveDocument.setNextRevision(archiveDocuments.get(generator.nextInt(archiveDocuments.size())));
            }
        }
        return archiveDocuments;
    }

    private Sheet createXSSFWorkbook(List<ArchiveDocument> archiveDocuments) throws IOException {
        Sheet sheet = createXSSFWorkbook(headers, sheetName);
        for (ArchiveDocument archiveDocument : archiveDocuments) {
            addRow(sheet, archiveDocument);
        }
        return sheet;
    }

    private void addRow(Sheet sheet, ArchiveDocument archiveDocument) {
        if (sheet == null) throw new NullPointerException("Sheet is null, create Workbook first");
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        headers.put(NEXT_ARCHIVE_DOCUMENT_COLUMN_NAME, 4);
        row.createCell(headers.get(ARCHIVE_NAME_COLUMN_NAME)).setCellValue(archiveDocument.getArchive().getName());
        row.createCell(headers.get(ARCHIVE_ABBREVIATION_COLUMN_NAME)).setCellValue(archiveDocument.getArchive().getAbbreviation());
        row.createCell(headers.get(DOCUMENT_NAME_COLUMN_NAME)).setCellValue(archiveDocument.getName());
        row.createCell(headers.get(ABBREVIATION_DOCUMENT_NAME_COLUMN_NAME)).setCellValue(archiveDocument.getAbbreviation());
        row.createCell(headers.get(FUND_PARAM_NAME)).setCellValue(archiveDocument.getFund());
        row.createCell(headers.get(CATALOG_PARAM_NAME)).setCellValue(archiveDocument.getCatalog());
        row.createCell(headers.get(INSTANCE_PARAM_NAME)).setCellValue(archiveDocument.getInstance());
        row.createCell(headers.get(BUNCH_PARAM_NAME)).setCellValue(archiveDocument.getBunch());
        row.createCell(headers.get(YEAR_PARAM_NAME)).setCellValue(archiveDocument.getYear());
        if (archiveDocument.getNextRevision() != null) {
            row.createCell(headers.get(NEXT_ARCHIVE_DOCUMENT_COLUMN_NAME)).setCellValue(archiveDocument.getNextRevision().getAbbreviation());
        }
    }
}
