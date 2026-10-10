**Русский** | [English](README.md)

# Сквозной UI + API + БД тест

## Описание решения

Автоматизированный тест **`ExamTest`** проходит полный сценарий: получает токен через API, авторизуется на UI-стенде и передаёт токен cookie, сверяет таблицу тестов проекта с данными из БД, создаёт проект через интерфейс и тест — напрямую в БД (вместе с логом и скриншотом), после чего проверяет страницу созданного теста.

В решении три слоя, работающие в одном тесте:

| Слой | Что делает | Реализация |
|---|---|---|
| API | получает токен по номеру варианта | Rest Assured (`TokenGetClient` → `POST /token/get?variant=`) |
| UI | работает со страницами проектов, проекта и теста, cookie и JS-методом | Aquality Selenium (Page Object + Form) |
| БД | читает список тестов проекта, создаёт тест, лог и вложение | JPA/Hibernate через репозитории (`Repository<T>`) |

### Тест-кейс и реализация

| Шаг | Ожидаемый результат | Реализация в `ExamTest` |
|---|---|---|
| 1. Запросом к апи получить токен согласно номеру варианта | Токен сгенерирован | `tokenGetClient.getToken(...)` → `POST {baseUrl}/token/get?variant={variant}`, ассерт `isNotBlank()` |
| 2. Перейти на сайт. Пройти необходимую авторизацию. С помощью cookie передать сгенерированный на шаге 1 токен (параметр `token`). Обновить страницу | Открылась страница проектов. После обновления страницы в футере указан верный номер варианта | HTTP Basic Auth (`addBasicAuthentication`) → `CookieManager.save(...)` с cookie `token` → `goTo` → `CookieManager.load()` → `TabUtil.refreshTab()` → `FooterForm.isVersionEqual(variant)` |
| 3. Перейти на страницу проекта Nexage. Запросом к БД получить список тестов проекта | Тесты первой страницы отсортированы по убыванию даты и соответствуют результату запроса к БД | `HomePage.clickProjectBy(...)` → `TestModelRepository.getAllByProjectSortedByStartTime(projectId, DESC)` → сравнение имён, методов и времени старта строк таблицы с БД |
| 4. Вернуться на предыдущую страницу в браузере (страница проектов). Нажать на `+Add`. Ввести название проекта и сохранить. Закрыть алерт. Для закрытия окна добавления проекта вызвать js-метод `closePopUp()`. Обновить страницу | Появился алерт об успешном сохранении. После вызова метода окно добавления проекта закрылось. После обновления страницы проект появился в списке | `navigate().back()` → `AddProjectForm.enterProjectName/clickSave` → `waitForSuccessAlert()` → `closePopUp()` (JS из `resources/js/closePopUp.js`) → `refreshTab()` → проверка списка проектов |
| 5. Перейти на страницу созданного проекта. Добавить тест через БД (вместе с логом и скриншотом текущей страницы) | Тест отобразился без обновления страницы | `TestModelRepository.create(...)`, `LogRepository.create(...)` (контент — `logs/test.log`), `AttachmentRepository.create(...)` (PNG-скриншот страницы) → `TestTableForm.waitForNewLastRow(...)` |
| 6. Перейти на страницу созданного теста | Все поля имеют верное значение. Скриншот соответствует отправленному | `TestPage.getData()` и `TestPage.getScreenshot()` сверяются с сохранённой моделью теста и отправленным скриншотом |

### Как устроено

- **Aquality Selenium как корпоративная среда** — браузер, элементы и ожидания берутся через `AqualityServices`; конфигурация среды и тестовых данных читается из JSON-ресурсов; логи пишутся в консоль и в `logs/test.log`;
- **Page Object + Form** — страницы (`HomePage`, `ProjectPage`, `TestPage`) и формы (`FooterForm`, `AddProjectForm`, `TestTableForm`) унаследованы от `Form`; локаторы вынесены в `private static final By`, названия элементов передаются в фабрику;
- **Cookie и авторизация** — `CookieManager` сохраняет набор cookie и переносит их в браузер после открытия сайта, `Browser.network().addBasicAuthentication(...)` закрывает HTTP Basic Auth; хост для Basic Auth вычисляется из `baseUrl` через `URI.getHost()` и не задаётся вручную;
- **JS-метод `closePopUp()`** — вызывается через `JsUtil` из файла `resources/js/closePopUp.js` (`jQuery(...).modal('hide')` с fallback-очисткой разметки); путь к скрипту описан enum'ом `TestJs`, а не строкой на месте вызова;
- **Работа с БД без «сырых» SQL** — HQL-шаблоны лежат в `resources/hql/`, подстановки и именованные параметры собираются билдером `HqlQuery`, репозитории (`Repository<T>`) инкапсулируют сессию, транзакцию и сохранение; тест вызывает только методы репозиториев;
- **ORM-модели** — `TestModel`, `Project`, `Status`, `Author`, `SessionModel`, `Log`, `Attachment`; meta-model классы (`TestModel_`, `Project_`, ...) генерирует `hibernate-jpamodelgen`, поэтому имена полей не дублируются строками;
- **DTO-разделение** — «сырые» данные из JSON (`ApiTestData`, `DbTestData`, `UiTestData`) и доменные модели данных страницы (`TestPageData`, `TestTableData`) разделены; `DbEnvironmentData` сам собирает JDBC-URL из `.env`;
- **Типобезопасные литералы** — вместо строк на месте вызова используются enum'ы: `Endpoint`, `CookieName`, `Attribute`, `TestJs`, `TestPageField`, `TestTableColumn`, `TestStatus`, `SortType`, `AttachmentContentType`, `TestArtifact`, `DbDotEnvKey`, `UiDotEnvKey`;

### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | корпоративная среда веб-автоматизации (обёртка над Selenium) |
| [Selenium WebDriver](https://www.selenium.dev/) | транзитивно | управление браузером |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-assertions |
| [REST Assured](https://rest-assured.io/) | 6.0.1 | вызовы API-стенда |
| [Hibernate ORM](https://hibernate.org/orm/) | 6.5.2.Final | JPA-модели и запросы к БД |
| [hibernate-jpamodelgen](https://hibernate.org/orm/) | 6.5.2.Final | генерация meta-model классов (annotation processor) |
| [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) | 26.7.0 | JDBC-драйвер MySQL |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги и тест-данные) |
| [java-dotenv](https://github.com/cdimascio/dotenv-java) | 5.2.2 | чтение `.env` |
| [Lombok](https://projectlombok.org/) | 1.18.48 | геттеры/сеттеры и билдеры моделей |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | генерация случайных данных |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | транзитивно | логирование (консоль + `logs/test.log`) |

Сборка проекта — **Apache Maven**.

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Docker (**Docker Desktop** + **Docker Compose**) — стенд и БД поднимаются контейнерами из [docker-kits](https://github.com/tquality-education-exam/docker-kits)
- БД: **MySQL 5.7** в контейнере (схема `union_reporting`, пользователь `login`)
- Стенд: приложение **union_reporting** (Tomcat в контейнере), UI — `http://localhost:8080/web`, API — `http://localhost:8080/api`
- Браузер: **Google Chrome** (профиль по умолчанию) или **Mozilla Firefox** — способ авторизации на стенде выбирается по браузеру: CDP для Chrome/Edge, WebDriver BiDi для Firefox
- IDE (опционально): IntelliJ IDEA

## Структура проекта

```text
<корень репозитория>/
├── pom.xml
├── .env.example
├── logs/
│   └── test.log
└── src/
    ├── main/
    │   ├── java/ru/kaelth4s/
    │   │   ├── framework/
    │   │   │   ├── api/
    │   │   │   │   ├── base/ApiResponse.java
    │   │   │   │   └── client/ApiClient.java
    │   │   │   ├── base/DotEnvKey.java
    │   │   │   ├── config/DotEnvManager.java
    │   │   │   ├── db/
    │   │   │   │   ├── base/HqlScript.java
    │   │   │   │   ├── enums/SortType.java
    │   │   │   │   └── query/HqlQuery.java
    │   │   │   ├── ui/
    │   │   │   │   ├── base/JsScript.java
    │   │   │   │   ├── config/CookieManager.java
    │   │   │   │   └── util/
    │   │   │   │       ├── FrameUtil.java
    │   │   │   │       ├── JsUtil.java
    │   │   │   │       ├── TabUtil.java
    │   │   │   │       └── WaitUtil.java
    │   │   │   └── util/
    │   │   │       ├── FileUtil.java
    │   │   │       └── JsonUtil.java
    │   │   └── tests/
    │   │       ├── api/
    │   │       │   ├── client/TokenGetClient.java
    │   │       │   ├── config/
    │   │       │   │   ├── ApiEnvironmentDataManager.java
    │   │       │   │   └── SpecificationsManager.java
    │   │       │   ├── dto/
    │   │       │   │   ├── ApiEnvironmentData.java
    │   │       │   │   └── token/get/
    │   │       │   │       ├── PostTokenGetRequest.java
    │   │       │   │       └── PostTokenGetResponse.java
    │   │       │   ├── enums/Endpoint.java
    │   │       │   └── spec/
    │   │       │       ├── AqualityLoggingFilter.java
    │   │       │       └── Specifications.java
    │   │       ├── db/
    │   │       │   ├── base/Repository.java
    │   │       │   ├── config/
    │   │       │   │   ├── DbEnvironmentDataManager.java
    │   │       │   │   └── HibernateConfig.java
    │   │       │   ├── dto/DbEnvironmentData.java
    │   │       │   ├── enums/
    │   │       │   │   ├── AttachmentContentType.java
    │   │       │   │   ├── DbDotEnvKey.java
    │   │       │   │   ├── TestArtifact.java
    │   │       │   │   ├── TestHql.java
    │   │       │   │   └── TestStatus.java
    │   │       │   ├── model/
    │   │       │   │   ├── Attachment.java
    │   │       │   │   ├── Author.java
    │   │       │   │   ├── Log.java
    │   │       │   │   ├── Project.java
    │   │       │   │   ├── SessionModel.java
    │   │       │   │   ├── Status.java
    │   │       │   │   └── TestModel.java
    │   │       │   ├── query/hql/
    │   │       │   │   ├── FindByHqlQuery.java
    │   │       │   │   └── test/FindAllByProjectSortedByHqlQuery.java
    │   │       │   ├── repository/
    │   │       │   │   ├── AttachmentRepository.java
    │   │       │   │   ├── LogRepository.java
    │   │       │   │   ├── ProjectRepository.java
    │   │       │   │   ├── SessionModelRepository.java
    │   │       │   │   ├── StatusRepository.java
    │   │       │   │   └── TestModelRepository.java
    │   │       │   └── util/
    │   │       │       ├── BuildNumberUtil.java
    │   │       │       └── HibernateUtil.java
    │   │       └── ui/
    │   │           ├── config/UiEnvironmentDataManager.java
    │   │           ├── dto/
    │   │           │   ├── TestPageData.java
    │   │           │   ├── TestTableData.java
    │   │           │   └── UiEnvironmentData.java
    │   │           ├── enums/
    │   │           │   ├── Attribute.java
    │   │           │   ├── CookieName.java
    │   │           │   ├── TestJs.java
    │   │           │   ├── TestPageField.java
    │   │           │   ├── TestTableColumn.java
    │   │           │   └── UiDotEnvKey.java
    │   │           ├── form/
    │   │           │   ├── AddProjectForm.java
    │   │           │   ├── FooterForm.java
    │   │           │   └── TestTableForm.java
    │   │           ├── page/
    │   │           │   ├── HomePage.java
    │   │           │   ├── ProjectPage.java
    │   │           │   └── TestPage.java
    │   │           └── util/WebUtil.java
    │   └── resources/
    │       ├── api/environment.json
    │       ├── hql/
    │       │   ├── findByTemplateParam.hql
    │       │   └── test/findAllByProjectSortedByTemplateParam.hql
    │       ├── js/closePopUp.js
    │       └── ui/environment.json
    └── test/
        ├── java/ru/kaelth4s/
        │   ├── framework/base/BaseUiTest.java
        │   └── tests/
        │       ├── api/
        │       │   ├── config/ApiTestDataManager.java
        │       │   └── dto/ApiTestData.java
        │       ├── db/
        │       │   ├── config/DbTestDataManager.java
        │       │   ├── dto/DbTestData.java
        │       │   └── util/SessionModelUtil.java
        │       ├── test/ExamTest.java
        │       └── ui/
        │           ├── config/UiTestDataManager.java
        │           └── dto/UiTestData.java
        └── resources/
            ├── api/test-data.json
            ├── db/test-data.json
            ├── ui/test-data.json
            ├── log4j2.properties
            ├── settings.json
            └── settings.firefox.json
```

## Как запустить

> Все команды выполняются в **cmd**. После установки Java/Maven перезапустите окно cmd, чтобы обновился `PATH`.

### Шаг 1. Установите Java 25 (Eclipse Temurin)

Если не установлена. `winget` доступен прямо в cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Перезапустите cmd и проверьте установку:

```bat
java -version
```

### Шаг 2. Установите Maven 3.9.16

**Вариант А — автоматическая установка одной командой (из cmd):**

```bat
powershell -Command "$url='https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip'; $dir='%USERPROFILE%\maven'; New-Item -ItemType Directory -Force -Path $dir | Out-Null; $zip=Join-Path $dir 'maven.zip'; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dir -Force; Remove-Item $zip; $mvn=Join-Path $dir 'apache-maven-3.9.16'; [Environment]::SetEnvironmentVariable('MAVEN_HOME', $mvn, 'User'); $path=[Environment]::GetEnvironmentVariable('Path', 'User'); if ($path -notlike '*'+$mvn+'\bin*') { [Environment]::SetEnvironmentVariable('Path', $path+';'+$mvn+'\bin', 'User') }"
```

**Вариант Б — ручная установка:**

1. Скачайте архив вручную: <https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip>
2. Распакуйте его в `%USERPROFILE%\maven` (например, `C:\Users\<ИмяПользователя>\maven`; создайте эту папку, если её нет).
3. В cmd выполните:

   ```bat
   setx MAVEN_HOME "%USERPROFILE%\maven\apache-maven-3.9.16"
   setx Path "%Path%;%USERPROFILE%\maven\apache-maven-3.9.16\bin"
   ```

4. Перезапустите cmd и проверьте:

   ```bat
   mvn -version
   ```

### Шаг 3. Установите браузер

Скачайте и установите один из поддерживаемых браузеров:

- **Google Chrome**: <https://www.google.com/chrome/>
- **Mozilla Firefox**: <https://www.firefox.com/thanks/>

Драйвер скачивается автоматически через Selenium Manager.

### Шаг 4. Установите Docker и Docker Compose

Тестируемое приложение (UI, API и MySQL) разворачивается в контейнерах готовым набором
[docker-kits](https://github.com/tquality-education-exam/docker-kits) — локально ставить MySQL и Tomcat не нужно.

1. Установите **Docker Desktop**: <https://docs.docker.com/desktop/install/windows-install/>
   (вариант для Linux: <https://docs.docker.com/engine/install/ubuntu/>).
2. **Docker Compose** идёт в комплекте с Docker Desktop. Проверьте, что команды доступны:

   ```bat
   docker --version
   docker-compose --version
   ```

   > **P.S.** При проблемах с Docker Desktop на Windows удобнее виртуальная машина с **Ubuntu Desktop**
   > ([установка Docker на Linux](https://docs.docker.com/engine/install/ubuntu/)).

### Шаг 5. Разверните стенд из docker-kits

1. Получите доступ к репозиторию с исходниками приложения
   [task-sources](https://github.com/tquality-education-exam/task-sources) — если доступа нет, запросите его у ревьюера.

2. Склонируйте набор контейнеров:

   ```bat
   cd /d <путь, где будет развёрнут стенд>
   git clone https://github.com/tquality-education-exam/docker-kits.git
   cd docker-kits
   ```

3. В `docker-compose.yml` укажите свой GitHub-логин и пароль (или personal access token) в аргументах сборки
   `GIT_USR` и `GIT_PWD` — по ним контейнер клонирует `task-sources` и собирает `.war`-файлы.

4. Поднимите контейнеры из корня `docker-kits`:

   ```bat
   docker-compose up -d
   ```

   Первый запуск занимает несколько минут: скачиваются образы Maven/Tomcat/MySQL и собирается приложение.
   В свежих версиях Docker Compose вызывается без дефиса — `docker compose up -d`; используйте тот вариант, который установлен.

5. Проверьте, что стенд поднялся:

   - `http://localhost:8080/web` — web-часть (закрыта HTTP Basic Auth);
   - `http://localhost:8080/api` — API;
   - `localhost:3306` — MySQL, схема `union_reporting`.

   Порты проброшены на `127.0.0.1`, поэтому стенд доступен только с машины, где запущен Docker. Если стенд развёрнут
   на другом хосте или порту, поменяйте `baseUrl` в `src/main/resources/ui/environment.json` и
   `src/main/resources/api/environment.json` (хост для Basic Auth вычисляется из `baseUrl` автоматически).

6. При первом старте схема `union_reporting` создаётся из `dump.sql`. Проверить таблицы можно так:

   ```bat
   docker-compose exec db mysql -u login -ppassword -e "USE union_reporting; SHOW TABLES; SELECT COUNT(*) FROM test;"
   ```

   Ожидаемые таблицы: `test`, `project`, `author`, `status`, `session`, `api_key`, `attachment`, `log`, `variant` и другие.
   В таблице `status` должны быть значения `PASSED`, `FAILED`, `SKIPPED`.

### Шаг 6. Заполните `.env`

Скопируйте `.env.example` в `.env` и укажите свои значения:

```bat
copy .env.example .env
```

Значения совпадают с настройками контейнеров из `docker-kits`: к БД можно подключаться как `root` / `root` (`MYSQL_ROOT_PASSWORD`) или как `login` / `password` (`MYSQL_USER` / `MYSQL_PASSWORD`), а `USER_LOGIN` / `USER_PASSWORD` — это `TOMCAT_USER` / `TOMCAT_PASSWORD` для Basic Auth UI-стенда.

```properties
# MySQL connection
DB_HOST=localhost
DB_PORT=3306
DB_SCHEMA=union_reporting
DB_USER=
DB_PASSWORD=
DB_PARAMS=
DB_DRIVER=com.mysql.cj.jdbc.Driver

# Hibernate behaviour
DB_SHOW_SQL=true
DB_FORMAT_SQL=true

# Web Auth
USER_LOGIN=
USER_PASSWORD=
```

Если в `docker-compose.yml` / `Dockerfile` набора `docker-kits` вы меняли `MYSQL_ROOT_PASSWORD`, `MYSQL_USER`, `MYSQL_PASSWORD`, `TOMCAT_USER` или `TOMCAT_PASSWORD` — укажите здесь свои значения.

### Шаг 7. Запустите тест

```bat
cd /d <путь к репозиторию>
mvn clean test
```

Запускается один тест — `ExamTest.shouldCreateTestWithLogAndScreenshot()`. Откроется окно браузера — не закрывайте его вручную во время прохождения теста.

**Запуск в Firefox:**

```bat
mvn clean test -Dprofile=firefox
```

Авторизация на стенде выполняется по-разному в зависимости от браузера (это инкапсулирует `BasicAuthUtil`):

- **Chrome / Edge** — Chrome DevTools Protocol: `Browser.network().addBasicAuthentication(...)`;
- **Firefox** — WebDriver BiDi: перехват фазы `authRequired` и `network.continueWithAuth` с логином и паролем. Для этого в `settings.firefox.json` в `driverSettings.firefox.capabilities` включён `webSocketUrl: true` — без него BiDi не включается, и утилита падает с явным сообщением.

**Логи** пишутся в `logs/test.log` и дублируются в консоль. Файл перезаписывается на каждом запуске, а его содержимое попадает в БД как лог созданного теста.

### Запуск через IntelliJ IDEA

1. Установите Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open** и выберите корень репозитория.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Выполните `ExamTest` (`src/test/java/ru/kaelth4s/tests/test/`) → ПКМ → **Run**.

## Конфигурация

### `.env`

Файл создаётся копированием `.env.example` и в git не попадает. Ключи читает `DotEnvManager` (java-dotenv, с fallback на переменные окружения и значения по умолчанию из enum'ов `DbDotEnvKey` и `UiDotEnvKey`).

### `ui/environment.json`

Адрес UI-стенда; из него же берётся хост для HTTP Basic Auth:

```json
{
  "baseUrl": "http://localhost:8080/web"
}
```

### `api/environment.json`

Адрес API-стенда:

```json
{
  "baseUrl": "http://localhost:8080/api"
}
```

### `api/test-data.json`

Номер варианта, по которому запрашивается токен (он же должен отображаться в футере сайта):

```json
{
  "postTokenGetRequest": {
    "variant": "1"
  }
}
```

### `ui/test-data.json`

Названия проектов: существующий проект для шага 3 и имя проекта, создаваемого на шаге 4:

```json
{
  "projectName": "Nexage",
  "newProjectName": "test11"
}
```

### `db/test-data.json`

Данные создаваемого теста: имя (к нему добавляется отметка времени запуска), полное имя тестового метода и окружение:

```json
{
  "test": {
    "name": "auto_test_",
    "methodName": "ru.kaelth4s.tests.test.ExamTest.shouldCreateTestWithLogAndScreenshot",
    "env": "local"
  }
}
```

### `settings.json` / `settings.firefox.json`

Настройки Aquality: браузер, таймауты, язык логов, поведение cookie. `settings.json` — профиль по умолчанию (Chrome), `settings.firefox.json` подключается ключом `-Dprofile=firefox`. В firefox-профиле в `capabilities` включён `webSocketUrl` — он нужен для BiDi-авторизации.

### `log4j2.properties`

Единый формат логов для консоли и файла `logs/test.log`:

```properties
appender.console.type=Console
appender.console.name=ConsoleAppender
appender.console.layout.type=PatternLayout
appender.console.layout.pattern=%d{yyyy-MM-dd HH:mm:ss} %-5level - %msg%n
appender.file.type=File
appender.file.name=FileAppender
appender.file.fileName=logs/test.log
appender.file.append=false
appender.file.layout.type=PatternLayout
appender.file.layout.pattern=%d{yyyy-MM-dd HH:mm:ss} %-5level - %msg%n
rootLogger.level=info
rootLogger.appenderRef.console.ref=ConsoleAppender
rootLogger.appenderRef.file.ref=FileAppender
```