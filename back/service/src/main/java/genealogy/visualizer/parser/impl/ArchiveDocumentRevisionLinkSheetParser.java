package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static genealogy.visualizer.parser.util.ParserUtils.STATUS_COLUMN_NAME;
import static genealogy.visualizer.parser.util.ParserUtils.STATUS_IMPORTED;
import static genealogy.visualizer.parser.util.ParserUtils.getHeaderWithStatusColumn;
import static genealogy.visualizer.parser.util.ParserUtils.getShortCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.getStringCellValue;
import static genealogy.visualizer.parser.util.ParserUtils.updateStatus;

/**
 * Парсер для {@link FamilyRevision}
 */
public class ArchiveDocumentRevisionLinkSheetParser extends AbstractSheetParser implements SheetParser {

    private static final String ARCHIVE_NAME_COLUMN_NAME = "ArchiveName";
    private static final String ARCHIVE_ABBREVIATION_COLUMN_NAME = "ArchiveAbbreviation";
    private static final String DOCUMENT_NAME_COLUMN_NAME = "DocumentName";
    private static final String ABBREVIATION_DOCUMENT_NAME_COLUMN_NAME = "AbbreviationDocumentName";
    private static final String NEXT_ARCHIVE_DOCUMENT_COLUMN_NAME = "NextArchiveDocumentNumber";

    public ArchiveDocumentRevisionLinkSheetParser(ArchiveDocumentDAO archiveDocumentDAO) {
        super(archiveDocumentDAO);
        LOGGER = LogManager.getLogger(ArchiveDocumentRevisionLinkSheetParser.class);
    }

    @Override
    public void parse(Sheet excelSheet, Map<String, String> parsingParams) {
        if (excelSheet == null) throw new NullPointerException("ExcelSheet is null");
        Map<String, Integer> header = getHeaderWithStatusColumn(excelSheet);
        List<Integer> successParsingRowNumbers = new ArrayList<>();
        Map<String, ArchiveDocument> archiveRevisionDocuments = new HashMap<>();
        Map<ArchiveDocument, String> notFoundNextDocumentWithNextAbbreviationNameDocuments = new HashMap<>();
        for (Row row : excelSheet) {
            String status = getStringCellValue(row, header.get(STATUS_COLUMN_NAME));
            int rowNum = row.getRowNum();
            if (rowNum == 0 || STATUS_IMPORTED.equals(status)) {
                continue;
            }
            String abbreviationDocumentName = getStringCellValue(row, header.get(ABBREVIATION_DOCUMENT_NAME_COLUMN_NAME));
            if (abbreviationDocumentName == null || abbreviationDocumentName.isEmpty()) {
                LOGGER.error("No abbreviation document name found for row {}", rowNum);
                continue;
            }
            ArchiveDocumentType type;
            try {
                type = ArchiveDocumentType.of(abbreviationDocumentName.substring(0, 2).toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid abbreviation document name {} found for row {}", abbreviationDocumentName, rowNum);
                continue;
            }
            ArchiveDocument archiveDocument;
            try {
                Archive archive = new Archive(
                        getStringCellValue(row, header.get(ARCHIVE_NAME_COLUMN_NAME)),
                        getStringCellValue(row, header.get(ARCHIVE_ABBREVIATION_COLUMN_NAME)));
                archiveDocument = new ArchiveDocument(
                        type,
                        getStringCellValue(row, header.get(DOCUMENT_NAME_COLUMN_NAME)),
                        abbreviationDocumentName,
                        getShortCellValue(row, header.get(YEAR_PARAM_NAME)),
                        getStringCellValue(row, header.get(FUND_PARAM_NAME)),
                        getStringCellValue(row, header.get(CATALOG_PARAM_NAME)),
                        getStringCellValue(row, header.get(INSTANCE_PARAM_NAME)),
                        getBunch(row, header.get(BUNCH_PARAM_NAME)),
                        archive
                );
            } catch (Exception e) {
                LOGGER.error("Failed to parse row {}", rowNum, e);
                continue;
            }
            String nextAbbreviationDocumentName = getStringCellValue(row, header.get(NEXT_ARCHIVE_DOCUMENT_COLUMN_NAME));
            if (nextAbbreviationDocumentName != null && !nextAbbreviationDocumentName.isEmpty()) {
                ArchiveDocument nextArchiveDocument = archiveRevisionDocuments.get(nextAbbreviationDocumentName);
                if (nextArchiveDocument == null) {
                    LOGGER.error("No next abbreviation document found for row {} with abbreviation {}", rowNum, nextAbbreviationDocumentName);
                    notFoundNextDocumentWithNextAbbreviationNameDocuments.put(archiveDocument, nextAbbreviationDocumentName);
                }
                archiveDocument.setNextRevision(nextArchiveDocument);
            }
            ArchiveDocument savedArchiveDocument;
            try {
                savedArchiveDocument = archiveDocumentDAO.saveOrFindIfExistDocument(archiveDocument);
                successParsingRowNumbers.add(rowNum);
            } catch (Exception e) {
                LOGGER.error("Failed to save row {}", rowNum, e);
                continue;
            }
            if (ArchiveDocumentType.CS.equals(type) || ArchiveDocumentType.CB.equals(type) || ArchiveDocumentType.IC.equals(type) ||
                    ArchiveDocumentType.RL.equals(type)) {
                archiveRevisionDocuments.put(abbreviationDocumentName, savedArchiveDocument);
            }
        }
        if (!notFoundNextDocumentWithNextAbbreviationNameDocuments.isEmpty()) {
            notFoundNextDocumentWithNextAbbreviationNameDocuments.forEach((savedArchiveDocument, nextAbbreviationArchiveDocument) -> {
                ArchiveDocument nextArchiveDocument = archiveRevisionDocuments.get(nextAbbreviationArchiveDocument);
                if (nextArchiveDocument == null) {
                    LOGGER.error("Not found next abbreviation document with abbreviation {}, after save all documents info", nextAbbreviationArchiveDocument);
                    return;
                }
                savedArchiveDocument.setNextRevision(nextArchiveDocument);
                archiveDocumentDAO.update(savedArchiveDocument);
            });
        }
        updateStatus(excelSheet, successParsingRowNumbers, header.get(STATUS_COLUMN_NAME));
    }

    @Override
    public String type() {
        return "Связи документов";
    }

    private static String getBunch(Row row, int cellNum) {
        String bunch = getStringCellValue(row, cellNum);
        if (bunch == null) {
            return "-";
        }
        return bunch;
    }
}
