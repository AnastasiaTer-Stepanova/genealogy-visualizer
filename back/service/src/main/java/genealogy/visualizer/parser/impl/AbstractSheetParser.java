package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.service.ArchiveDocumentDAO;

import java.util.Map;

import static java.lang.Short.valueOf;

public class AbstractSheetParser {

    public static final String DOCUMENT_TYPE_PARAM_NAME = "Type";

    private static final String ARCHIVE_PARAM_NAME = "Archive";
    private static final String FUND_PARAM_NAME = "Fund";
    private static final String CATALOG_PARAM_NAME = "Catalog";
    private static final String INSTANCE_PARAM_NAME = "Instance";
    private static final String BUNCH_PARAM_NAME = "Bunch";
    private static final String YEAR_PARAM_NAME = "YearOfDocument";

    private final ArchiveDocumentDAO archiveDocumentDAO;

    public AbstractSheetParser(ArchiveDocumentDAO archiveDocumentDAO) {
        this.archiveDocumentDAO = archiveDocumentDAO;
    }

    protected ArchiveDocument getArchiveDocument(Map<String, String> parsingParams) {
        return archiveDocumentDAO.saveOrFindIfExistDocument(createArchiveDocument(parsingParams));
    }

    public static ArchiveDocument createArchiveDocument(Map<String, String> parsingParams) {
        return new ArchiveDocument(
                ArchiveDocumentType.of(parsingParams.get(DOCUMENT_TYPE_PARAM_NAME)),
                valueOf(parsingParams.get(YEAR_PARAM_NAME)),
                parsingParams.get(FUND_PARAM_NAME),
                parsingParams.get(CATALOG_PARAM_NAME),
                parsingParams.get(INSTANCE_PARAM_NAME),
                parsingParams.get(BUNCH_PARAM_NAME),
                new Archive(parsingParams.get(ARCHIVE_PARAM_NAME))
        );
    }
}
