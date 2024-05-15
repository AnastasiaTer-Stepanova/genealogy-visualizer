package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.Archive;
import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static java.lang.Short.valueOf;

public class AbstractSheetParser {

    protected static Logger LOGGER = LogManager.getLogger(AbstractSheetParser.class);

    private static final String DOCUMENT_TYPE_PARAM_NAME = "Type";
    private static final String ARCHIVE_PARAM_NAME = "Archive";
    protected static final String FUND_PARAM_NAME = "Fund";
    protected static final String CATALOG_PARAM_NAME = "Catalog";
    protected static final String INSTANCE_PARAM_NAME = "Instance";
    protected static final String BUNCH_PARAM_NAME = "Bunch";
    protected static final String YEAR_PARAM_NAME = "YearOfDocument";

    protected final ArchiveDocumentDAO archiveDocumentDAO;

    public AbstractSheetParser(ArchiveDocumentDAO archiveDocumentDAO) {
        this.archiveDocumentDAO = archiveDocumentDAO;
    }

    protected ArchiveDocument getArchiveDocument(Map<String, String> parsingParams) {
        return archiveDocumentDAO.saveOrFindIfExistDocument(createArchiveDocument(parsingParams));
    }

    public static ArchiveDocument createArchiveDocument(Map<String, String> parsingParams) {
        String bunch = parsingParams.get(BUNCH_PARAM_NAME);
        return new ArchiveDocument(
                ArchiveDocumentType.of(parsingParams.get(DOCUMENT_TYPE_PARAM_NAME)),
                valueOf(parsingParams.get(YEAR_PARAM_NAME)),
                parsingParams.get(FUND_PARAM_NAME),
                parsingParams.get(CATALOG_PARAM_NAME),
                parsingParams.get(INSTANCE_PARAM_NAME),
                bunch != null ? bunch : "0",
                new Archive(parsingParams.get(ARCHIVE_PARAM_NAME))
        );
    }
}
