package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.ParamDAO;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

public class CensusBookSheetParser extends FamilyRevisionSheetParser {

    public CensusBookSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        super(familyRevisionDAO, archiveDocumentDAO, paramDAO);
    }

    @Override
    public void parse(Sheet excelSheet, Map<String, String> parsingParams) {
        super.parse(excelSheet, parsingParams);
    }

    @Override
    public String type() {
        return ArchiveDocumentType.CB.getName();
    }
}
