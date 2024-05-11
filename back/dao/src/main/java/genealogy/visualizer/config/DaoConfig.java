package genealogy.visualizer.config;

import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.DeathDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.LocalityDAO;
import genealogy.visualizer.service.MarriageDAO;
import genealogy.visualizer.service.PersonDAO;
import genealogy.visualizer.service.impl.ArchiveDocumentDAOImpl;
import genealogy.visualizer.service.impl.ChristeningDAOImpl;
import genealogy.visualizer.service.impl.DeathDAOImpl;
import genealogy.visualizer.service.impl.FamilyRevisionDAOImpl;
import genealogy.visualizer.service.impl.LocalityDAOImpl;
import genealogy.visualizer.service.impl.MarriageDAOImpl;
import genealogy.visualizer.service.impl.PersonDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan("genealogy.visualizer.repository")
@EntityScan("genealogy.visualizer.entity")
@EnableJpaRepositories(basePackages = {"genealogy.visualizer.repository", "genealogy.visualizer.entity"})
public class DaoConfig {

    @Bean
    public ArchiveDocumentDAO archiveDocumentDAO(@Autowired ArchiveDocumentRepository archiveDocumentRepository,
                                                 @Autowired ArchiveRepository archiveRepository) {
        return new ArchiveDocumentDAOImpl(archiveDocumentRepository, archiveRepository);
    }

    @Bean
    public FamilyRevisionDAO familyRevisionDAO(@Autowired FamilyRevisionRepository familyRevisionRepository,
                                               ArchiveDocumentDAO archiveDocumentDAO) {
        return new FamilyRevisionDAOImpl(familyRevisionRepository, archiveDocumentDAO);
    }

    @Bean
    public ChristeningDAO christeningDAO(@Autowired ChristeningRepository christeningRepository,
                                         LocalityDAO localityDAO,
                                         ArchiveDocumentDAO archiveDocumentDAO) {
        return new ChristeningDAOImpl(christeningRepository, localityDAO, archiveDocumentDAO);
    }

    @Bean
    public LocalityDAO localityDAO(@Autowired LocalityRepository localityRepository) {
        return new LocalityDAOImpl(localityRepository);
    }

    @Bean
    public MarriageDAO marriageDAO(@Autowired MarriageRepository marriageRepository,
                                   LocalityDAO localityDAO,
                                   ArchiveDocumentDAO archiveDocumentDAO) {
        return new MarriageDAOImpl(marriageRepository, localityDAO, archiveDocumentDAO);
    }

    @Bean
    public DeathDAO deathDAO(@Autowired DeathRepository deathRepository,
                             ArchiveDocumentDAO archiveDocumentDAO,
                             LocalityDAO localityDAO) {
        return new DeathDAOImpl(deathRepository, archiveDocumentDAO, localityDAO);
    }

    @Bean
    public PersonDAO personDAO(@Autowired PersonRepository personRepository) {
        return new PersonDAOImpl(personRepository);
    }

//    @Bean
//    public DataSource getDataSource() {
//        return DataSourceBuilder.create()
//                .driverClassName("org.postgresql.Driver")
//                .url("jdbc:postgresql://localhost:5432/genealogy_visualizer")
//                .username("root")
//                .password("rootp4ss")
//                .build();
//    }
}
