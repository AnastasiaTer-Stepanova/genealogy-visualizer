import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    application
    id("java-conventions")
    id("org.openapi.generator") version "7.5.0"
}

application {
    mainClass.set("genealogy.visualizer.Application")
}

val yamlFiles = File("$rootDir/openapi/").listFiles()?.filter { file ->
    file.name.endsWith(".yaml")
}

val generateDir = "${layout.projectDirectory}/build/generated/openapi"
val generateJavaOpenApiTasks = yamlFiles?.map { file ->
    val apiName = file.name.removeSuffix(".yaml").uppercaseFirstChar()
    tasks.register<GenerateTask>("openApiGenerate$apiName") {
        group = "openapi tools"
        generatorName.set("spring")
        inputSpec.set(file.absolutePath)
        outputDir.set(generateDir)
        apiPackage.set("genealogy.visualizer.api")
        modelPackage.set("genealogy.visualizer.api.model")
        apiFilesConstrainedTo.set(listOf(""))
        modelFilesConstrainedTo.set(listOf(""))
        validateSpec.set(true)
        additionalProperties.set(
            mapOf(
                "interfaceOnly" to "true",
            )
        )
        configOptions.set(
            mapOf(
                "library" to "spring-boot",
                "useTags" to "true",
                "annotationLibrary" to "swagger2",
                "skipDefaultInterface" to "true",
                "generateSupportingFiles" to "false",
            )
        )
    }
}


val generateJavaOpenApi by tasks.registering {
    group = "openapi tools"
    dependsOn(generateJavaOpenApiTasks)
}

tasks.compileJava {
    dependsOn(generateJavaOpenApi)
}

sourceSets {
    main {
        java {
            srcDirs(File("$generateDir/src/main/java"))
        }
    }
}

dependencies {
    implementation(project(":back:service"))

    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")
}
