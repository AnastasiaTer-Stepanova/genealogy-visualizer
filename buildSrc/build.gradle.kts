plugins {
    `kotlin-dsl`
}

kotlin { jvmToolchain(21) }

repositories {
    // Подключаем автоматическая работа с репозиторием Maven Central
    mavenCentral()
}
