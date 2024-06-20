package genealogy.visualizer.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi archive() {
        return GroupedOpenApi.builder()
                .group("archive") // Группа Swagger
                .pathsToMatch("/archive/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi archiveDocument() {
        return GroupedOpenApi.builder()
                .group("archive-document") // Группа Swagger
                .pathsToMatch("/archive-document/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi authorization() {
        return GroupedOpenApi.builder()
                .group("authorization") // Группа Swagger
                .pathsToMatch("/authorization/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi christening() {
        return GroupedOpenApi.builder()
                .group("christening") // Группа Swagger
                .pathsToMatch("/christening/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi death() {
        return GroupedOpenApi.builder()
                .group("death") // Группа Swagger
                .pathsToMatch("/death/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi familyRevision() {
        return GroupedOpenApi.builder()
                .group("family-revision") // Группа Swagger
                .pathsToMatch("/family-revision/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi genealogyVisualizer() {
        return GroupedOpenApi.builder()
                .group("genealogy-visualizer") // Группа Swagger
                .pathsToMatch("/genealogy-visualizer/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi locality() {
        return GroupedOpenApi.builder()
                .group("locality") // Группа Swagger
                .pathsToMatch("/locality/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi marriage() {
        return GroupedOpenApi.builder()
                .group("marriage") // Группа Swagger
                .pathsToMatch("/marriage/**") // Пути вашего API
                .build();
    }

    @Bean
    public GroupedOpenApi person() {
        return GroupedOpenApi.builder()
                .group("person") // Группа Swagger
                .pathsToMatch("/person/**") // Пути вашего API
                .build();
    }

}