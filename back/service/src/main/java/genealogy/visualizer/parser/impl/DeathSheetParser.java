package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Death;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.entity.model.FullName;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.util.StringParserHelper;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.DeathDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.parser.util.ParserUtils.HYPHEN;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_COLUMN_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_IMPORTED;
import static genealogy.visualizer.parser.util.ParserUtils.getDateCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.getHeaderWithStatusColumn;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.parseAge;
import static genealogy.visualizer.parser.util.ParserUtils.updateStatus;

public class DeathSheetParser extends AbstractSheetParser implements SheetParser {

    private static final String DATE_COLUMN_NAME = "Date";
    private static final String LOCALITY_COLUMN_NAME = "Locality";
    private static final String RELATIVE_COLUMN_NAME = "Relative";
    private static final String FULL_NAME_COLUMN_NAME = "FullName";
    private static final String AGE_COLUMN_NAME = "Age";
    private static final String CAUSE_COLUMN_NAME = "Cause";
    private static final String BURIAL_PLACE_COLUMN_NAME = "BurialPlace";
    private static final String COMMENT_COLUMN_NAME = "Comment";

    private final DeathDAO deathDAO;

    public DeathSheetParser(DeathDAO deathDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        super(archiveDocumentDAO);
        this.deathDAO = deathDAO;
        LOGGER = LogManager.getLogger(DeathSheetParser.class);
    }

    @Override
    public void parse(Sheet excelSheet, Map<String, String> parsingParams) {
        if (excelSheet == null) throw new NullPointerException("ExcelSheet is null");
        ArchiveDocument archive = super.getArchiveDocument(parsingParams);
        Map<String, Integer> header = getHeaderWithStatusColumn(excelSheet);
        List<Integer> successParsingRowNumbers = new ArrayList<>();
        for (Row row : excelSheet) {
            String status = getStringCellValue(row, header.get(STATUS_COLUMN_NAME));
            int rowNum = row.getRowNum();
            if (rowNum == 0 || STATUS_IMPORTED.equals(status)) {
                continue;
            }
            Date deathDate = getDateCellValue(row, header.get(DATE_COLUMN_NAME));
            if (deathDate == null) {
                LOGGER.error("Death date is null for row {}", rowNum);
                continue;
            }
            FullName fullName = new StringParserHelper(getStringCellValue(row, header.get(FULL_NAME_COLUMN_NAME))).getFullName();
            if (fullName == null) {
                LOGGER.error("Full name is null for row {}", rowNum);
                continue;
            }
            Death death;
            try {
                death = new Death(
                        null,
                        deathDate,
                        fullName,
                        new StringParserHelper(getStringCellValue(row, header.get(RELATIVE_COLUMN_NAME))).getFullName(),
                        parseAge(getStringCellValue(row, header.get(AGE_COLUMN_NAME))),
                        getCause(row, header),
                        getStringCellValue(row, header.get(BURIAL_PLACE_COLUMN_NAME)),
                        getStringCellValue(row, header.get(COMMENT_COLUMN_NAME)),
                        new StringParserHelper(getStringCellValue(row.getCell(header.get(LOCALITY_COLUMN_NAME)))).getLocality(),
                        archive,
                        null);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to create death entity from row: %s", rowNum), e);
                continue;
            }
            try {
                deathDAO.save(death);
                successParsingRowNumbers.add(rowNum);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to save death from row: %s", rowNum), e);
            }
        }
        updateStatus(excelSheet, successParsingRowNumbers, header.get(STATUS_COLUMN_NAME));
    }

    @Override
    public String type() {
        return ArchiveDocumentType.PR_DTH.getName();
    }

    private String getCause(Row row, Map<String, Integer> header) {
        String cause = getStringCellValue(row, header.get(CAUSE_COLUMN_NAME));
        return HYPHEN.equals(cause) ? null : cause;
    }
}
