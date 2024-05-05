package genealogy.visualizer.config;

import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.parser.FileParser;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.impl.ArchiveDocumentExcelParser;
import genealogy.visualizer.parser.impl.ChristeningSheetParser;
import genealogy.visualizer.parser.impl.FamilyRevisionSheetParser;
import genealogy.visualizer.parser.impl.MarriageSheetParser;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.MarriageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Configuration
@ComponentScan
public class ServiceConfig {

    @Bean
    public SheetParser familyRevisionParser(FamilyRevisionDAO familyRevisionDAO) {
        return new FamilyRevisionSheetParser(familyRevisionDAO);
    }

    @Bean
    public SheetParser christeningSheetParser(ChristeningDAO christeningDAO) {
        return new ChristeningSheetParser(christeningDAO);
    }

    @Bean
    public SheetParser marriageSheetParser(MarriageDAO marriageDAO) {
        return new MarriageSheetParser(marriageDAO);
    }

    @Bean
    public Map<ArchiveDocumentType, SheetParser> parserMap(@Autowired List<SheetParser> sheetParsers) {
        return sheetParsers.stream().collect(toMap(SheetParser::type, Function.identity()));
    }

    @Bean
    public FileParser archiveDocumentExcelParser(ArchiveDocumentDAO archiveDocumentDAO, Map<ArchiveDocumentType, SheetParser> parserMap) {
        return new ArchiveDocumentExcelParser(parserMap, archiveDocumentDAO);
    }
}
