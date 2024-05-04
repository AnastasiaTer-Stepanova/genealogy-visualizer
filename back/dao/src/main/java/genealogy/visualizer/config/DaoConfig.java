package genealogy.visualizer.config;

import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.GodParentRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.ChristeningDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.GodParentDAO;
import genealogy.visualizer.service.LocalityDAO;
import genealogy.visualizer.service.impl.ArchiveDocumentDAOImpl;
import genealogy.visualizer.service.impl.ChristeningDAOImpl;
import genealogy.visualizer.service.impl.FamilyRevisionDAOImpl;
import genealogy.visualizer.service.impl.GodParentDAOImpl;
import genealogy.visualizer.service.impl.LocalityDAOImpl;
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
    public FamilyRevisionDAO familyRevisionDAO(@Autowired FamilyRevisionRepository familyRevisionRepository) {
        return new FamilyRevisionDAOImpl(familyRevisionRepository);
    }

    @Bean
    public GodParentDAO godParentDAO(@Autowired GodParentRepository godParentRepository,
                                     LocalityDAO localityDAO) {
        return new GodParentDAOImpl(godParentRepository, localityDAO);
    }

    @Bean
    public ChristeningDAO christeningDAO(@Autowired ChristeningRepository christeningRepository,
                                         LocalityDAO localityDAO,
                                         ArchiveDocumentDAO archiveDocumentDAO,
                                         GodParentDAO godParentDAO) {
        return new ChristeningDAOImpl(christeningRepository, localityDAO, archiveDocumentDAO, godParentDAO);
    }

    @Bean
    public LocalityDAO localityDAO(@Autowired LocalityRepository localityRepository) {
        return new LocalityDAOImpl(localityRepository);
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
