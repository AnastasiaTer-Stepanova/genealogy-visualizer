plugins {
    id("java-conventions")
    application
}

application {
    mainClass.set("genealogy.visualizer.Application")
}


dependencies {
    project(":back:service")

    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")
}
