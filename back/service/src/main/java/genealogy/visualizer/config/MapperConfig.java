package genealogy.visualizer.config;

import genealogy.visualizer.mapper.AgeMapper;
import genealogy.visualizer.mapper.AgeMapperImpl;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.ArchiveDocumentMapperImpl;
import genealogy.visualizer.mapper.ArchiveMapper;
import genealogy.visualizer.mapper.ArchiveMapperImpl;
import genealogy.visualizer.mapper.ChristeningMapper;
import genealogy.visualizer.mapper.ChristeningMapperImpl;
import genealogy.visualizer.mapper.DeathMapper;
import genealogy.visualizer.mapper.DeathMapperImpl;
import genealogy.visualizer.mapper.FamilyRevisionMapper;
import genealogy.visualizer.mapper.FamilyRevisionMapperImpl;
import genealogy.visualizer.mapper.FullNameMapper;
import genealogy.visualizer.mapper.FullNameMapperImpl;
import genealogy.visualizer.mapper.LocalityMapper;
import genealogy.visualizer.mapper.LocalityMapperImpl;
import genealogy.visualizer.mapper.MarriageMapper;
import genealogy.visualizer.mapper.MarriageMapperImpl;
import genealogy.visualizer.mapper.PersonMapper;
import genealogy.visualizer.mapper.PersonMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfig {

    @Bean
    @Primary
    public AgeMapper ageMapper() {
        return new AgeMapperImpl();
    }

    @Bean
    @Primary
    public ArchiveDocumentMapper archiveDocumentMapper() {
        return new ArchiveDocumentMapperImpl();
    }

    @Bean
    @Primary
    public ArchiveMapper archiveMapper() {
        return new ArchiveMapperImpl();
    }

    @Bean
    @Primary
    public FamilyRevisionMapper familyRevisionMapper() {
        return new FamilyRevisionMapperImpl();
    }

    @Bean
    @Primary
    public FullNameMapper fullNameMapper() {
        return new FullNameMapperImpl();
    }

    @Bean
    @Primary
    public PersonMapper personMapper() {
        return new PersonMapperImpl();
    }

    @Bean
    @Primary
    public ChristeningMapper christeningMapper() {
        return new ChristeningMapperImpl();
    }

    @Bean
    @Primary
    public DeathMapper deathMapper() {
        return new DeathMapperImpl();
    }

    @Bean
    @Primary
    public LocalityMapper localityMapper() {
        return new LocalityMapperImpl();
    }

    @Bean
    @Primary
    public MarriageMapper marriageMapper() {
        return new MarriageMapperImpl();
    }
}
