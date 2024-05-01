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

    testFixturesImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.2.4")
}
