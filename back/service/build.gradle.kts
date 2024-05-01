plugins {
    application
    id("java-conventions")
    id("java-test-fixtures")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":back:dao"))

    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.23.1")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.springframework.boot:spring-boot-starter:3.2.5")
    implementation("org.springframework.data:spring-data-commons:3.2.5")


    testImplementation(testFixtures(project(":back:dao")))
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
}
