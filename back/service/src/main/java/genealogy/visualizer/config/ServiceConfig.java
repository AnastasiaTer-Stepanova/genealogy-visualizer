package genealogy.visualizer.config;

import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.ArchiveMapper;
import genealogy.visualizer.mapper.ChristeningMapper;
import genealogy.visualizer.mapper.DeathMapper;
import genealogy.visualizer.mapper.EasyArchiveDocumentMapper;
import genealogy.visualizer.mapper.EasyArchiveMapper;
import genealogy.visualizer.mapper.EasyChristeningMapper;
import genealogy.visualizer.mapper.EasyDeathMapper;
import genealogy.visualizer.mapper.EasyFamilyRevisionMapper;
import genealogy.visualizer.mapper.EasyLocalityMapper;
import genealogy.visualizer.mapper.EasyMarriageMapper;
import genealogy.visualizer.mapper.EasyPersonMapper;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.mapper.LocalityMapper;
import genealogy.visualizer.mapper.MarriageMapper;
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
import genealogy.visualizer.service.LocalityDAO;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.ParamDAO;
import genealogy.visualizer.service.PersonDAO;
import genealogy.visualizer.service.UserDAO;
import genealogy.visualizer.service.archive.ArchiveDocumentService;
import genealogy.visualizer.service.archive.ArchiveDocumentServiceImpl;
import genealogy.visualizer.service.archive.ArchiveService;
import genealogy.visualizer.service.archive.ArchiveServiceImpl;
import genealogy.visualizer.service.authorization.AuthorizationService;
import genealogy.visualizer.service.authorization.AuthorizationServiceImpl;
import genealogy.visualizer.service.authorization.JwtService;
import genealogy.visualizer.service.authorization.JwtServiceImpl;
import genealogy.visualizer.service.authorization.UserService;
import genealogy.visualizer.service.authorization.UserServiceImpl;
import genealogy.visualizer.service.christening.ChristeningService;
import genealogy.visualizer.service.christening.ChristeningServiceImpl;
import genealogy.visualizer.service.death.DeathService;
import genealogy.visualizer.service.death.DeathServiceImpl;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import genealogy.visualizer.service.family.revision.FamilyRevisionServiceImpl;
import genealogy.visualizer.service.graph.GenealogyVisualizeService;
import genealogy.visualizer.service.graph.GenealogyVisualizeServiceImpl;
import genealogy.visualizer.service.locality.LocalityService;
import genealogy.visualizer.service.locality.LocalityServiceImpl;
import genealogy.visualizer.service.marriage.MarriageService;
import genealogy.visualizer.service.marriage.MarriageServiceImpl;
import genealogy.visualizer.service.person.PersonService;
import genealogy.visualizer.service.person.PersonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
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
                                                       EasyFamilyRevisionMapper easyFamilyRevisionMapper,
                                                       EasyArchiveDocumentMapper easyArchiveDocumentMapper) {
        return new FamilyRevisionServiceImpl(familyRevisionDAO, archiveDocumentDAO, familyRevisionMapper, easyFamilyRevisionMapper, easyArchiveDocumentMapper);
    }

    @Bean
    public SheetParser familyRevisionParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new FamilyRevisionSheetParser(familyRevisionDAO, archiveDocumentDAO, paramDAO);
    }

    @Bean
    public SheetParser christeningSheetParser(ChristeningDAO christeningDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new ChristeningSheetParser(christeningDAO, archiveDocumentDAO, paramDAO);
    }

    @Bean
    public SheetParser marriageSheetParser(MarriageDAO marriageDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new MarriageSheetParser(marriageDAO, archiveDocumentDAO, paramDAO);
    }

    @Bean
    public SheetParser deathSheetParser(DeathDAO deathDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new DeathSheetParser(deathDAO, archiveDocumentDAO, paramDAO);
    }

    @Bean
    public SheetParser censusBookSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new CensusBookSheetParser(familyRevisionDAO, archiveDocumentDAO, paramDAO);
    }

    @Bean
    public SheetParser confessionSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new ConfessionSheetParser(familyRevisionDAO, archiveDocumentDAO, paramDAO);
    }

    @Bean
    public SheetParser interimCensusSheetParser(FamilyRevisionDAO familyRevisionDAO, ArchiveDocumentDAO archiveDocumentDAO, ParamDAO paramDAO) {
        return new InterimCensusSheetParser(familyRevisionDAO, archiveDocumentDAO, paramDAO);
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
    public GenealogyVisualizeService genealogyVisualize(PersonDAO personDAO, EasyPersonMapper easyPersonMapper) {
        return new GenealogyVisualizeServiceImpl(personDAO, easyPersonMapper);
    }

    @Bean
    public PersonService personService(PersonDAO personDAO, PersonMapper personMapper, EasyPersonMapper easyPersonMapper) {
        return new PersonServiceImpl(personDAO, personMapper, easyPersonMapper);
    }

    @Bean
    public ArchiveService archiveService(ArchiveDAO archiveDAO, ArchiveMapper archiveMapper, EasyArchiveMapper easyArchiveMapper) {
        return new ArchiveServiceImpl(archiveDAO, archiveMapper, easyArchiveMapper);
    }

    @Bean
    public ArchiveDocumentService archiveDocumentService(ArchiveDocumentDAO archiveDocumentDAO,
                                                         ArchiveDocumentMapper archiveDocumentMapper,
                                                         EasyArchiveDocumentMapper easyArchiveDocumentMapper) {
        return new ArchiveDocumentServiceImpl(archiveDocumentDAO, archiveDocumentMapper, easyArchiveDocumentMapper);
    }

    @Bean
    public ChristeningService christeningService(ChristeningDAO christeningDAO,
                                                 ChristeningMapper christeningMapper,
                                                 EasyChristeningMapper easyChristeningMapper) {
        return new ChristeningServiceImpl(christeningDAO, christeningMapper, easyChristeningMapper);
    }

    @Bean
    public DeathService deathService(DeathDAO deathDAO,
                                     DeathMapper deathMapper,
                                     EasyDeathMapper easyDeathMapper) {
        return new DeathServiceImpl(deathDAO, deathMapper, easyDeathMapper);
    }

    @Bean
    public MarriageService marriageService(MarriageDAO marriageDAO,
                                           MarriageMapper marriageMapper,
                                           EasyMarriageMapper easyMarriageMapper) {
        return new MarriageServiceImpl(marriageDAO, marriageMapper, easyMarriageMapper);
    }

    @Bean
    public LocalityService localityService(LocalityDAO localityDAO,
                                           LocalityMapper localityMapper,
                                           EasyLocalityMapper easyLocalityMapper) {
        return new LocalityServiceImpl(localityDAO, localityMapper, easyLocalityMapper);
    }

    @Bean
    public AuthorizationService authorizationService(ParamDAO paramDAO,
                                                     UserService userDetailsService,
                                                     JwtService jwtService,
                                                     AuthenticationManager authenticationManager) {
        return new AuthorizationServiceImpl(paramDAO, userDetailsService, jwtService, authenticationManager);
    }

    @Bean
    public UserService userDetailsService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        return new UserServiceImpl(userDAO, passwordEncoder);
    }

    @Bean
    public JwtService jwtService(@Value("${jwt.secret}") String secret,
                                 @Value("${jwt.duration}") Duration duration) {
        return new JwtServiceImpl(secret, duration);
    }
}
