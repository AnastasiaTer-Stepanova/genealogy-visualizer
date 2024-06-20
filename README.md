# Проект визуализации генеалогического исследования

Этот проект предоставляет инструменты для управления и визуализации данных генеалогического исследования с использованием
REST API, обработки данных из Excel файлов и их хранения в базе данных.

## Структура проекта

Проект состоит из следующих модулей:

1. **Controller**: Содержит все REST-контроллеры, которые предоставляют доступ к функциональности проекта через
   HTTP-запросы.

2. **Api**: В этом модуле находятся сгенерированные Java модели на основе спецификации OpenAPI. Эти модели используются
   для передачи данных между клиентом и сервером.

3. **Service**: Включает все сервисы и мапперы, которые преобразуют модели API в модели сущностей и наоборот. Эти
   сервисы реализуют бизнес-логику проекта.

4. **Dao**: Отвечает за работу с базой данных, включая CRUD операции для всех сущностей.

5. **Watcher**: Следит за указанной папкой и загружает данные из Excel файлов при их добавлении в папку.

## Установка и запуск

### Предварительные требования

- Java 21 или выше
- Gradle 8.7 или выше
- PostgreSQL 16.3

### Клонирование проекта

Склонируйте репозиторий:

```bash
git clone https://github.com/AnastasiaTer-Stepanova/genealogy-visualizer.git
```

### Конфигурация базы данных

Создайте базу данных для проекта и настройте подключение в файле application.properties, находящимся в ':back:dao':

```bash
spring.datasource.password=rootp4ss
spring.datasource.url=jdbc:postgresql://localhost:5432/genealogy_visualizer
spring.datasource.username=root
```

### Сборка приложения

Для сборки проекта выполните:

```bash
./gradlew clean build
```

### Запуск приложения

Для запуска приложения выполните:

```bash
./gradlew bootRun
```

### Запуск приложения watcher

Укажите папку в application.properties в параметре

```bash
params.path.file.excel=/Users/Anastasia/Downloads/
```

Выполните:

```bash
./gradlew :back:watcher:bootRun
```

### Использование API

Документация по API доступна по следующему URL после запуска приложения:

```bash
http://http://localhost:8080/swagger-ui/index.html
```

### Open-api

Open-api находится в папке openapi основного модуля проекта

- archive.yaml - операции по работе с данными архивов.
- archive-document.yaml - операции по работе с данными архивных документов.
- authorization.yaml - операции авторизации и регистрации.
- christening.yaml - операции по работе с данными по крещениям.
- death.yaml - операции по работе с данными о смертях.
- family-revision.yaml - операции по работе с данными по переписям/ревизиям.
- genealogy-visualize.yaml - операции по работе с генеалогическим исследованием. На данный момент там находится описание
  api для отдачи данных в формате, необходимом для UI-библиотеки https://github.com/vasturiano/3d-force-graph.
- locality.yaml - операции по работе с данными по населенным пунктам.
- marriage.yaml - операции по работе с данными по бракам.
- person.yaml - операции по работе с данными по людям.
