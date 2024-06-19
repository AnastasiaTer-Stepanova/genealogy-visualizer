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
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    minHeapSize = "512m"
    maxHeapSize = "2048m"
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
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
    testImplementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.mapstruct:mapstruct:1.5.5.Final") // для избавлени от warning: unknown enum constant ReportingPolicy.ERROR
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.2.5")
    testImplementation("org.springframework.data:spring-data-jpa:3.2.5")
    testImplementation("org.springframework.security:spring-security-crypto:6.2.4")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    testImplementation("org.testcontainers:testcontainers:1.19.8")
}

tasks.named<BootJar>("bootJar") {
    archiveFileName = "controller.jar"
    mainClass = "genealogy.visualizer.Application"
}