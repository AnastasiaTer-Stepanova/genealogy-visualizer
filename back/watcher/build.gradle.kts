import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    id("java-conventions")
    id("org.springframework.boot") version "3.2.5"
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("genealogy.visualizer.Application")
}

dependencies {
    implementation(project(":back:service"))

    implementation("org.springframework.boot:spring-boot-starter:3.2.5")

}

tasks.named<BootJar>("bootJar") {
    archiveFileName = "watcher.jar"
    mainClass = "genealogy.visualizer.Application"
}