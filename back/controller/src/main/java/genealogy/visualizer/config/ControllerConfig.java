package genealogy.visualizer.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import genealogy.visualizer.controller.ArchiveController;
import genealogy.visualizer.controller.ArchiveDocumentController;
import genealogy.visualizer.controller.ChristeningController;
import genealogy.visualizer.controller.DeathController;
import genealogy.visualizer.controller.FamilyRevisionController;
import genealogy.visualizer.controller.GenealogyVisualizeController;
import genealogy.visualizer.controller.MarriageController;
import genealogy.visualizer.controller.PersonController;
import genealogy.visualizer.service.archive.ArchiveDocumentService;
import genealogy.visualizer.service.archive.ArchiveService;
import genealogy.visualizer.service.christening.ChristeningService;
import genealogy.visualizer.service.death.DeathService;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import genealogy.visualizer.service.graph.GenealogyVisualizeService;
import genealogy.visualizer.service.marriage.MarriageService;
import genealogy.visualizer.service.person.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
@ComponentScan
public class ControllerConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
        return objectMapper;
    }

    @Bean
    public FamilyRevisionController familyRevisionController(FamilyRevisionService familyRevisionService) {
        return new FamilyRevisionController(familyRevisionService);
    }

    @Bean
    public GenealogyVisualizeController genealogyVisualizeController(GenealogyVisualizeService genealogyVisualizeService) {
        return new GenealogyVisualizeController(genealogyVisualizeService);
    }

    @Bean
    public PersonController personController(PersonService personService) {
        return new PersonController(personService);
    }

    @Bean
    public ArchiveController archiveController(ArchiveService archiveService) {
        return new ArchiveController(archiveService);
    }

    @Bean
    public ArchiveDocumentController archiveDocumentController(ArchiveDocumentService archiveDocumentService) {
        return new ArchiveDocumentController(archiveDocumentService);
    }

    @Bean
    public ChristeningController christeningController(ChristeningService christeningService) {
        return new ChristeningController(christeningService);
    }

    @Bean
    public DeathController deathController(DeathService deathService) {
        return new DeathController(deathService);
    }

    @Bean
    public MarriageController marriageController(MarriageService marriageService) {
        return new MarriageController(marriageService);
    }

}
