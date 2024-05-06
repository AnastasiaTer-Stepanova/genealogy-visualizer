package genealogy.visualizer.parser.impl;

import genealogy.visualizer.entity.ArchiveDocument;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.service.FamilyRevisionDAO;
import org.apache.poi.ss.usermodel.Sheet;

public class ConfessionSheetParser extends FamilyRevisionSheetParser {

    public ConfessionSheetParser(FamilyRevisionDAO familyRevisionDAO) {
        super(familyRevisionDAO);
    }

    @Override
    public void parse(Sheet excelSheet, ArchiveDocument archive) {
        super.parse(excelSheet, archive);
    }

    @Override
    public ArchiveDocumentType type() {
        return ArchiveDocumentType.CS;
    }
}
