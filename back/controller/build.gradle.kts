import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    id("java-conventions")
    id("java-test-fixtures")
    id("org.springframework.boot") version "3.2.5"
}

application {
    mainClass.set("genealogy.visualizer.Application")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":back:api"))
    implementation(project(":back:service"))

    implementation("io.swagger.core.v3:swagger-annotations:2.2.20") // для избавлени от warning: unknown enum constant RequiredMode.NOT_REQUIRED
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")

    testImplementation(project(":back:dao"))
    testImplementation(testFixtures(project(":back:dao")))
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // для избавлени от warning: unknown enum constant RequiredMode.NOT_REQUIRED
    testImplementation("org.apache.commons:commons-lang3:3.14.0")
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.mapstruct:mapstruct:1.5.5.Final") // для избавлени от warning: unknown enum constant ReportingPolicy.ERROR
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.2.5")
    testImplementation("org.springframework.data:spring-data-jpa:3.2.5")
}

tasks.named<BootJar>("bootJar") {
    archiveFileName = "controller.jar"
    mainClass = "genealogy.visualizer.Application"
}