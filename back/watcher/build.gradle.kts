plugins {
    application
    id("java-conventions")
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
