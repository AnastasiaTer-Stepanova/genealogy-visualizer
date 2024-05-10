package genealogy.visualizer.config;

import genealogy.visualizer.controller.FamilyRevisionController;
import genealogy.visualizer.service.family.revision.FamilyRevisionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class ControllerConfig {

    @Bean
    public FamilyRevisionController familyRevisionController(FamilyRevisionService familyRevisionService) {
        return new FamilyRevisionController(familyRevisionService);
    }

}
