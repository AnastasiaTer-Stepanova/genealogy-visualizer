plugins {
    application
    id("java-conventions")
    id("java-test-fixtures")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":back:api"))
    implementation(project(":back:dao"))

    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.0-rc2") // для избавлени от warning: unknown enum constant Shape.STRING
    implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.5")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.20") // для избавлени от warning: unknown enum constant RequiredMode.NOT_REQUIRED
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // для избавлени от warning: unknown enum constant RequiredMode.NOT_REQUIRED
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.23.1")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter:3.2.5")
    implementation("org.springframework.data:spring-data-commons:3.2.5")

    testImplementation(testFixtures(project(":back:dao")))
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // для избавлени от warning: unknown enum constant RequiredMode.NOT_REQUIRED
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
}
