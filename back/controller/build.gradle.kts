import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    id("java-conventions")
    id("java-test-fixtures")
    id("org.springframework.boot") version "3.2.4"
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

    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")

    testImplementation(project(":back:dao"))
    testImplementation(testFixtures(project(":back:dao")))
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.2.5")
    testImplementation("org.springframework.data:spring-data-jpa:3.2.5")
}

tasks.named<BootJar>("bootJar") {
    archiveFileName = "controller.jar"
    mainClass = "genealogy.visualizer.Application"
}