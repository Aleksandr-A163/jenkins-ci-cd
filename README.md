# Jenkins CI/CD для UI и mobile тестов

Этот репозиторий содержит конфигурации Jenkins Job Builder и pipeline-скрипты для запуска UI и mobile автотестов в Jenkins с публикацией Allure-отчётов.

## Используемые репозитории

- UI тесты: playwright_otus
- Mobile тесты: mobile-appium

## Что реализовано

Проект настраивает Jenkins job для:

- загрузки и обновления Jenkins job из YAML через Jenkins Job Builder
- запуска UI тестов с выбором браузера
- запуска mobile тестов с автоматическим скачиванием APK
- публикации Allure-отчётов для UI и mobile прогонов
- использования общей job-обёртки для запуска выбранных test job

## Job-ы

### jobs_uploader
Служебная job, которая читает YAML-конфигурации из этого репозитория и создаёт или обновляет Jenkins job в Jenkins.

Что делает:
- клонирует этот репозиторий
- создаёт config.ini для Jenkins Job Builder
- выполняет jenkins-jobs update ./jobs

### ui_tests
Pipeline job для запуска Playwright UI тестов из репозитория playwright_otus.

Возможности:
- клонирует репозиторий с UI тестами
- запускает Gradle тесты через wrapper
- поддерживает выбор браузера
- поддерживает headless-режим
- публикует Allure-отчёт

Параметры:
- BRANCH — ветка Git
- BROWSER — chromium / firefox / webkit
- HEADLESS — запуск в headless-режиме
- BASE_URL — базовый URL для page-specific тестов
- SITE_URL — основной URL сайта

### mobile_tests
Pipeline job для запуска Appium mobile тестов из репозитория mobile-appium.

Возможности:
- клонирует репозиторий с mobile тестами
- автоматически скачивает APK перед запуском
- поднимает Android emulator и Appium в Docker
- ждёт готовности эмулятора и Appium
- запускает Gradle тесты
- публикует Allure-отчёт

Параметры:
- APK_URL — прямая ссылка на APK
- DB_USERNAME — имя пользователя БД
- DB_PASSWORD — пароль БД

### running_tests
Дополнительная job-обёртка для запуска выбранных test job из одной точки.

Можно использовать для:
- запуска только UI тестов
- запуска только mobile тестов
- запуска UI и mobile тестов одной job

## Структура проекта

`text
jobs/
  tests/
    jobs_uploader.yaml
    ui_tests.yaml
    mobile_tests.yaml
    running_tests.yaml

pipeline/
  jobs_uploader.groovy