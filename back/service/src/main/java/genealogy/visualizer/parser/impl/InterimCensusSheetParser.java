package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

public class InterimCensusSheetParser extends FamilyRevisionSheetParser {

    public InterimCensusSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        super(familyRevisionDAO, archiveDocumentDAO);
    }

    @Override
    public void parse(Sheet excelSheet, Map<String, String> parsingParams) {
        super.parse(excelSheet, parsingParams);
    }

    @Override
    public String type() {
        return ArchiveDocumentType.IC.getName();
    }
}
