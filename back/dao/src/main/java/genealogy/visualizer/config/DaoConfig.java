package genealogy.visualizer.config;

import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import genealogy.visualizer.service.FamilyRevisionDAO;
import genealogy.visualizer.service.impl.ArchiveDocumentDAOImpl;
import genealogy.visualizer.service.impl.FamilyRevisionDAOImpl;
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
