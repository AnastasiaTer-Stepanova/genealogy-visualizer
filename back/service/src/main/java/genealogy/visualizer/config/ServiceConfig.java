package genealogy.visualizer.config;

import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.ErrorMapper;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.parser.FileParser;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.impl.ArchiveDocumentExcelParser;
import genealogy.visualizer.parser.impl.CensusBookSheetParser;
import genealogy.visualizer.parser.impl.ChristeningSheetParser;
import genealogy.visualizer.parser.impl.ConfessionSheetParser;
import genealogy.visualizer.parser.impl.DeathSheetParser;
import genealogy.visualizer.parser.impl.FamilyRevisionSheetParser;
import genealogy.visualizer.parser.impl.MarriageSheetParser;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.DeathDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.PersonDAO;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import genealogy.visualizer.service.family.revision.FamilyRevisionServiceImpl;
import genealogy.visualizer.service.graph.GenealogyVisualizeService;
import genealogy.visualizer.service.graph.GenealogyVisualizeServiceImpl;
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
    public FamilyRevisionService familyRevisionService(FamilyRevisionDAO familyRevisionDAO,
                                                       ArchiveDocumentDAO archiveDocumentDAO,
                                                       FamilyRevisionMapper familyRevisionMapper,
                                                       ArchiveDocumentMapper archiveDocumentMapper,
                                                       ErrorMapper errorMapper) {
        return new FamilyRevisionServiceImpl(familyRevisionDAO, archiveDocumentDAO, familyRevisionMapper,
                archiveDocumentMapper, errorMapper);
    }

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
    public SheetParser deathSheetParser(DeathDAO deathDAO) {
        return new DeathSheetParser(deathDAO);
    }

    @Bean
    public SheetParser censusBookSheetParser(FamilyRevisionDAO familyRevisionDAO) {
        return new CensusBookSheetParser(familyRevisionDAO);
    }

    @Bean
    public SheetParser confessionSheetParser(FamilyRevisionDAO familyRevisionDAO) {
        return new ConfessionSheetParser(familyRevisionDAO);
    }

    @Bean
    public Map<ArchiveDocumentType, SheetParser> parserMap(@Autowired List<SheetParser> sheetParsers) {
        return sheetParsers.stream().collect(toMap(SheetParser::type, Function.identity()));
    }

    @Bean
    public FileParser archiveDocumentExcelParser(ArchiveDocumentDAO archiveDocumentDAO, Map<ArchiveDocumentType, SheetParser> parserMap) {
        return new ArchiveDocumentExcelParser(parserMap, archiveDocumentDAO);
    }

    @Bean
    public GenealogyVisualizeService genealogyVisualize(PersonDAO personDAO, PersonMapper personMapper, ErrorMapper errorMapper) {
        return new GenealogyVisualizeServiceImpl(personDAO, personMapper, errorMapper);
    }
}
