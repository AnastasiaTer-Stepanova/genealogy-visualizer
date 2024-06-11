plugins {
    id("java-conventions")
    id("java-test-fixtures")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.5")

    testFixturesImplementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // для избавлени от warning: unknown enum constant RequiredMode.NOT_REQUIRED
    testFixturesImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.2.5")
}
