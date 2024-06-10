package genealogy.visualizer.config;

import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.ArchiveMapper;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.parser.FileParser;
import genealogy.visualizer.parser.SheetParser;
import genealogy.visualizer.parser.impl.ArchiveDocumentRevisionLinkSheetParser;
import genealogy.visualizer.parser.impl.CensusBookSheetParser;
import genealogy.visualizer.parser.impl.ChristeningSheetParser;
import genealogy.visualizer.parser.impl.ConfessionSheetParser;
import genealogy.visualizer.parser.impl.DeathSheetParser;
import genealogy.visualizer.parser.impl.FamilyRevisionSheetParser;
import genealogy.visualizer.parser.impl.FileExcelParser;
import genealogy.visualizer.parser.impl.InterimCensusSheetParser;
import genealogy.visualizer.parser.impl.MarriageSheetParser;
import genealogy.visualizer.service.ArchiveDAO;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.DeathDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.PersonDAO;
import genealogy.visualizer.service.archive.ArchiveService;
import genealogy.visualizer.service.archive.ArchiveServiceImpl;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import genealogy.visualizer.service.family.revision.FamilyRevisionServiceImpl;
import genealogy.visualizer.service.graph.GenealogyVisualizeService;
import genealogy.visualizer.service.graph.GenealogyVisualizeServiceImpl;
import genealogy.visualizer.service.person.PersonService;
import genealogy.visualizer.service.person.PersonServiceImpl;
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
                                                       ArchiveDocumentMapper archiveDocumentMapper) {
        return new FamilyRevisionServiceImpl(familyRevisionDAO, archiveDocumentDAO, familyRevisionMapper, archiveDocumentMapper);
    }

    @Bean
    public SheetParser familyRevisionParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new FamilyRevisionSheetParser(familyRevisionDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser christeningSheetParser(ChristeningDAO christeningDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new ChristeningSheetParser(christeningDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser marriageSheetParser(MarriageDAO marriageDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new MarriageSheetParser(marriageDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser deathSheetParser(DeathDAO deathDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new DeathSheetParser(deathDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser censusBookSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new CensusBookSheetParser(familyRevisionDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser confessionSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new ConfessionSheetParser(familyRevisionDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser interimCensusSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO) {
        return new InterimCensusSheetParser(familyRevisionDAO, archiveDocumentDAO);
    }

    @Bean
    public SheetParser archiveDocumentConnectionSheetParser(ArchiveDocumentDAO archiveDocumentDAO) {
        return new ArchiveDocumentRevisionLinkSheetParser(archiveDocumentDAO);
    }

    @Bean
    public FileParser fileExcelParser(@Autowired List<SheetParser> sheetParsers) {
        Map<String, SheetParser> parserMap = sheetParsers.stream().collect(toMap((SheetParser::type), Function.identity()));
        return new FileExcelParser(parserMap);
    }

    @Bean
    public GenealogyVisualizeService genealogyVisualize(PersonDAO personDAO, PersonMapper personMapper) {
        return new GenealogyVisualizeServiceImpl(personDAO, personMapper);
    }

    @Bean
    public PersonService personService(PersonDAO personDAO, PersonMapper personMapper) {
        return new PersonServiceImpl(personDAO, personMapper);
    }

    @Bean
    public ArchiveService archiveService(ArchiveDAO archiveDAO, ArchiveMapper archiveMapper) {
        return new ArchiveServiceImpl(archiveDAO, archiveMapper);
    }
}
