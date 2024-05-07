package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.entity.enums.Sex;
import genealogy.visualizer.entity.model.GodParent;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.util.StringParserHelper;
import genealogy.visualizer.service.ChristeningDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.parser.util.ParserUtils.STATUS_COLUMN_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_IMPORTED;
import static genealogy.visualizer.parser.util.ParserUtils.getDateCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.getHeaderWithStatusColumn;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.updateStatus;

public class ChristeningSheetParser implements SheetParser {
    private static final Logger LOGGER = LogManager.getLogger(ChristeningSheetParser.class);

    private static final String MALE_COLUMN_NAME = "Male";
    private static final String FEMALE_COLUMN_NAME = "Female";
    private static final String BIRTH_COLUMN_NAME = "Birth";
    private static final String CHRISTENING_COLUMN_NAME = "Christening";
    private static final String LOCALITY_COLUMN_NAME = "Locality";
    private static final String NAME_COLUMN_NAME = "Name";
    private static final String FATHER_COLUMN_NAME = "Father";
    private static final String MOTHER_COLUMN_NAME = "Mather";
    private static final String FIRST_GOD_PARENT_COLUMN_NAME = "GodParent1";
    private static final String SECOND_GOD_PARENT_COLUMN_NAME = "GodParent2";
    private static final String COMMENT_COLUMN_NAME = "Comment";
    private static final String LEGITIMACY_COLUMN_NAME = "Legitimacy";
    private static final String ILLEGITIMATE = "незаконнорожденный";

    private final ChristeningDAO christeningDAO;

    public ChristeningSheetParser(ChristeningDAO christeningDAO) {
        this.christeningDAO = christeningDAO;
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
            Date birthDate = getDateCellValue(row, header.get(BIRTH_COLUMN_NAME));
            if (birthDate == null) {
                LOGGER.error("Birth date is null for row {}", rowNum);
                continue;
            }
            Christening christening;
            try {
                christening = new Christening(
                        null,
                        getDateCellValue(row, header.get(BIRTH_COLUMN_NAME)),
                        getDateCellValue(row, header.get(CHRISTENING_COLUMN_NAME)),
                        getSex(row, header),
                        getStringCellValue(row, header.get(NAME_COLUMN_NAME)),
                        new StringParserHelper(getStringCellValue(row, header.get(FATHER_COLUMN_NAME))).getFullName(),
                        new StringParserHelper(getStringCellValue(row, header.get(MOTHER_COLUMN_NAME))).getFullName(),
                        getStringCellValue(row, header.get(COMMENT_COLUMN_NAME)),
                        getLegitimacy(getStringCellValue(row, header.get(LEGITIMACY_COLUMN_NAME))),
                        new StringParserHelper(getStringCellValue(row, header.get(LOCALITY_COLUMN_NAME))).getLocality(),
                        getGodParents(row, header),
                        null,
                        archive);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to create christening entity from row: %s", rowNum), e);
                continue;
            }
            try {
                christeningDAO.save(christening);
                successParsingRowNumbers.add(rowNum);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to save christening from row: %s", rowNum), e);
            }
        }
        updateStatus(excelSheet, successParsingRowNumbers, header.get(STATUS_COLUMN_NAME));
    }

    @Override
    public ArchiveDocumentType type() {
        return ArchiveDocumentType.PR_CHR;
    }

    private Boolean getLegitimacy(String value) {
        if (ILLEGITIMATE.equals(value)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private List<GodParent> getGodParents(Row row, Map<String, Integer> header) {
        List<GodParent> godParents = new ArrayList<>();
        String firstGodParent = getStringCellValue(row, header.get(FIRST_GOD_PARENT_COLUMN_NAME));
        String secondGodParent = getStringCellValue(row, header.get(SECOND_GOD_PARENT_COLUMN_NAME));
        if (firstGodParent != null) {
            GodParent godParent = getGodParent(firstGodParent);
            if (godParent != null && godParent.getFullName() != null && godParent.getFullName().getName() != null)
                godParents.add(godParent);
        }
        if (secondGodParent != null) {
            GodParent godParent = getGodParent(secondGodParent);
            if (godParent != null && godParent.getFullName() != null && godParent.getFullName().getName() != null)
                godParents.add(godParent);
        }
        return godParents;
    }

    private GodParent getGodParent(String godParentString) {
        if (godParentString == null || godParentString.isEmpty()) return null;
        StringParserHelper helper = new StringParserHelper(godParentString);
        GodParent result = new GodParent();
        result.setLocality(helper.getLocality());
        result.setFullName(helper.getFullName());
        return result;
    }

    private Sex getSex(Row row, Map<String, Integer> header) {
        String male = getStringCellValue(row, header.get(MALE_COLUMN_NAME));
        if (male != null) {
            return Sex.MALE;
        } else {
            String female = getStringCellValue(row, header.get(FEMALE_COLUMN_NAME));
            if (female != null) {
                return Sex.FEMALE;
            }
        }
        throw new IllegalArgumentException("Sex doesn't exist");
    }
}
