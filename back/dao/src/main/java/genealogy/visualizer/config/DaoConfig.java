package genealogy.visualizer.config;

import genealogy.visualizer.repository.ArchiveDocumentRepository;
import genealogy.visualizer.repository.ArchiveRepository;
import genealogy.visualizer.repository.ChristeningRepository;
import genealogy.visualizer.repository.DeathRepository;
import genealogy.visualizer.repository.FamilyRevisionRepository;
import genealogy.visualizer.repository.GodParentRepository;
import genealogy.visualizer.repository.LocalityRepository;
import genealogy.visualizer.repository.MarriageRepository;
import genealogy.visualizer.repository.ParamRepository;
import genealogy.visualizer.repository.PersonRepository;
import genealogy.visualizer.repository.UserRepository;
import genealogy.visualizer.repository.WitnessRepository;
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
import genealogy.visualizer.service.impl.ArchiveDAOImpl;
import genealogy.visualizer.service.impl.ArchiveDocumentDAOImpl;
import genealogy.visualizer.service.impl.ChristeningDAOImpl;
import genealogy.visualizer.service.impl.DeathDAOImpl;
import genealogy.visualizer.service.impl.FamilyRevisionDAOImpl;
import genealogy.visualizer.service.impl.LocalityDAOImpl;
import genealogy.visualizer.service.impl.MarriageDAOImpl;
import genealogy.visualizer.service.impl.ParamDAOImpl;
import genealogy.visualizer.service.impl.PersonDAOImpl;
import genealogy.visualizer.service.impl.UserDAOImpl;
import jakarta.persistence.EntityManager;
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
    public ArchiveDocumentDAO archiveDocumentDAO(@Autowired ArchiveRepository archiveRepository,
                                                 @Autowired ChristeningRepository christeningRepository,
                                                 @Autowired DeathRepository deathRepository,
                                                 @Autowired EntityManager entityManager,
                                                 @Autowired FamilyRevisionRepository familyRevisionRepository,
                                                 @Autowired MarriageRepository marriageRepository,
                                                 @Autowired ArchiveDocumentRepository archiveDocumentRepository) {
        return new ArchiveDocumentDAOImpl(archiveRepository, christeningRepository, deathRepository, entityManager, familyRevisionRepository,
                marriageRepository, archiveDocumentRepository);
    }

    @Bean
    public FamilyRevisionDAO familyRevisionDAO(@Autowired FamilyRevisionRepository familyRevisionRepository,
                                               @Autowired ArchiveDocumentRepository archiveDocumentRepository,
                                               @Autowired PersonRepository personRepository,
                                               @Autowired EntityManager entityManager) {
        return new FamilyRevisionDAOImpl(familyRevisionRepository, archiveDocumentRepository, personRepository, entityManager);
    }

    @Bean
    public ChristeningDAO christeningDAO(@Autowired ChristeningRepository christeningRepository,
                                         @Autowired PersonRepository personRepository,
                                         @Autowired LocalityRepository localityRepository,
                                         @Autowired ArchiveDocumentRepository archiveDocumentRepository,
                                         @Autowired GodParentRepository godParentRepository,
                                         @Autowired EntityManager entityManager) {
        return new ChristeningDAOImpl(christeningRepository, personRepository, localityRepository, archiveDocumentRepository,
                godParentRepository, entityManager);
    }

    @Bean
    public LocalityDAO localityDAO(@Autowired LocalityRepository localityRepository,
                                   @Autowired ChristeningRepository christeningRepository,
                                   @Autowired DeathRepository deathRepository,
                                   @Autowired PersonRepository personRepository,
                                   @Autowired MarriageRepository marriageRepository,
                                   @Autowired WitnessRepository witnessRepository,
                                   @Autowired GodParentRepository godParentRepository,
                                   @Autowired EntityManager entityManager) {
        return new LocalityDAOImpl(localityRepository, christeningRepository, deathRepository, personRepository,
                marriageRepository, witnessRepository, godParentRepository, entityManager);
    }

    @Bean
    public MarriageDAO marriageDAO(@Autowired MarriageRepository marriageRepository,
                                   @Autowired PersonRepository personRepository,
                                   @Autowired LocalityRepository localityRepository,
                                   @Autowired ArchiveDocumentRepository archiveDocumentRepository,
                                   @Autowired WitnessRepository witnessRepository,
                                   @Autowired EntityManager entityManager) {
        return new MarriageDAOImpl(marriageRepository, personRepository, localityRepository, archiveDocumentRepository,
                witnessRepository, entityManager);
    }

    @Bean
    public DeathDAO deathDAO(@Autowired DeathRepository deathRepository,
                             @Autowired PersonRepository personRepository,
                             @Autowired LocalityRepository localityRepository,
                             @Autowired ArchiveDocumentRepository archiveDocumentRepository,
                             @Autowired EntityManager entityManager) {
        return new DeathDAOImpl(deathRepository, personRepository, localityRepository, archiveDocumentRepository, entityManager);
    }

    @Bean
    public ArchiveDAO archiveDAO(@Autowired ArchiveRepository archiveRepository,
                                 @Autowired ArchiveDocumentRepository archiveDocumentRepository,
                                 @Autowired EntityManager entityManager) {
        return new ArchiveDAOImpl(archiveRepository, archiveDocumentRepository, entityManager);
    }

    @Bean
    public PersonDAO personDAO(@Autowired PersonRepository personRepository,
                               @Autowired LocalityRepository localityRepository,
                               @Autowired ChristeningRepository christeningRepository,
                               @Autowired DeathRepository deathRepository,
                               @Autowired MarriageRepository marriageRepository,
                               @Autowired FamilyRevisionRepository familyRevisionRepository,
                               @Autowired EntityManager entityManager) {
        return new PersonDAOImpl(personRepository,
                localityRepository,
                christeningRepository,
                deathRepository,
                marriageRepository,
                familyRevisionRepository,
                entityManager);
    }

    @Bean
    public ParamDAO paramDAO(@Autowired ParamRepository paramRepository) {
        return new ParamDAOImpl(paramRepository);
    }

    @Bean
    public UserDAO userDAO(@Autowired UserRepository userRepository) {
        return new UserDAOImpl(userRepository);
    }

}
