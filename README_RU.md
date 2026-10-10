# API-автотесты + работа с БД через модели (CRUD)

## Описание решения

Проект состоит из двух частей, собранных в одном Maven-модуле:

1. **API-автотесты** публичного REST-стенда **JSONPlaceholder** (<https://jsonplaceholder.typicode.com/>) — Java + Rest Assured; результат каждого теста автоматически пишется в БД (**Тест-кейс 1**).
2. **Работа с базой данных** `union_reporting` через JPA/Hibernate: сущности-модели, репозитории с CRUD-методами и тест, воспроизводящий сценарий задания (**Тест-кейс 2**).

Никаких «сырых» SQL-запросов в тестах нет: взаимодействие с БД инкапсулировано в классах-репозиториях (`get`, `add`, `update`, `delete`, `findAll`, `getOrCreate`), а тест вызывает готовые методы.

### Тест-кейс 1. Добавление новой записи

Реализован как JUnit 5 extension **`DbResultExtension`**, подключённый к `ApiTest` (`@ExtendWith`).

| Шаг | Ожидаемый результат | Реализация |
|---|---|---|
| 1. Запустить любой тест | Тест пройден | `ApiTest.shouldPassApiResponses()` — 6 шагов проверок |
| Постусловие: добавить результат теста новой записью в соответствующую таблицу | Информация добавлена | `DbResultExtension.afterEach` → `TestRepository.addTestResult` (`INSERT` в `test`) |

Что попадает в запись: имя теста, `метод` (класс + метод), статус (`PASSED` / `FAILED` / `SKIPPED` — определяется по `TestExecutionException`), время старта/окончания, проект, автор и сессия прогона. Недостающие справочные записи (`project`, `author`, `session`) создаются автоматически через `getOrCreate`.

### Тест-кейс 2. Обработка тестовых данных

Реализован в `DatabaseTest`.

| Фаза | Шаг | Реализация |
|---|---|---|
| Предусловие | Выбрать тесты, где ID содержит две повторяющиеся цифры (`11`, `77`, …), но не более 10 | `TestRepository.findRandomIdsWithRepeatingDigits` — нативный запрос с `REGEXP '00\|11\|…\|99'`, `ORDER BY RAND()`, `LIMIT :limit` |
| Предусловие | Скопировать эти тесты с указанием текущего проекта и автора | `TestRepository.copyTests` → `cloneWithNewOwner` (копия получает текущие `project`, `author` и `session`) |
| Шаг | Имитировать запуск каждого теста мок-утилитой и обновить информацию в БД | `TestRunner.runTest` — проставляет случайные `start_time` / `end_time` (1–20 c) и статус `PASSED`; `TestRepository.updateAll` — `merge` одной транзакцией |
| Проверка | Тесты пройдены, информация обновлена | `assertThat(oldResults).usingRecursiveComparison().isNotEqualTo(newResults)` |
| Постусловие | Удалить созданные записи | `TestRepository.deleteAllByIds` в `@AfterEach` |

## Как устроено

### Слой БД

- **`Repository<T, ID>`** — базовый абстрактный репозиторий: `inTransaction`, `save`, `update`, `delete`, `findById`. Транзакция открывается на операцию, при `RuntimeException` — `rollback`.
- **`HibernateUtil`** — singleton `SessionFactory`; читает `hibernate.cfg.xml` и подменяет URL/логин/пароль значениями из `.env` (или из переменных окружения).
- **Сущности-модели** — `TestModel` (`test`), `Project` (`project`), `Author` (`author`), `Status` (`status`), `SessionModel` (`session`); связи `@ManyToOne` (LAZY) на статус, проект, сессию и автора.
- **`TestStatus`** — enum статусов с именем в БД (`PASSED` / `FAILED` / `SKIPPED`); `StatusRepository` резолвит статус по имени и падает с явной ошибкой, если такого статуса в дампе нет.
- **`getOrCreate`** во всех справочных репозиториях — автоматизация добавления недостающей информации (проект, автор, сессия), как требуется в FAQ задания.
- **Подмена автозаполнения** — `SessionModelUtil` создаёт сессию прогона с номером сборки из `BuildNumberUtil` (формат `yyyyMMddHHmm`).
- **Тестовые данные** — `db/test-data.json` (автор, проект, лимит ID), читается через `JsonUtil` в `DbTestData`.


### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | HTTP-клиент и DSL для API-запросов |
| [Hibernate ORM](https://hibernate.org/orm/) | 6.5.2.Final | JPA-модели и работа с MySQL |
| [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) | 26.7.0 | JDBC-драйвер MySQL |
| [Lombok](https://projectlombok.org/) | 1.18.48 | геттеры/сеттеры/конструкторы сущностей |
| [java-dotenv](https://github.com/cdimascio/dotenv-java) | 5.2.2 | чтение `.env` |
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | единая среда логирования (обёртка над Log4j2) |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк и extension API |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги, тест-данные, десериализация ответов) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | генерация случайных тестовых данных |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | логирование (транзитивно через Aquality) |

Сборка проекта — **Apache Maven**.

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- MySQL Server: **8.x** + клиент `mysql`
- IDE (опционально): IntelliJ IDEA

## Структура проекта

```text
<корень репозитория>/
├── pom.xml
├── .env.example
├── db-dump/                      # дамп БД (клонируется отдельно, в .gitignore)
│   └── dump.sql
├── logs/
│   └── test.log
└── src/
    └── test/
        ├── java/ru/kaelth4s/
        │   ├── api/
        │   │   ├── client/
        │   │   │   ├── PostClient.java
        │   │   │   └── UserClient.java
        │   │   ├── config/
        │   │   │   ├── ApiEnvironmentDataManager.java
        │   │   │   ├── ApiTestDataManager.java
        │   │   │   └── SpecificationsManager.java
        │   │   ├── dto/
        │   │   │   ├── ApiEnvironmentData.java
        │   │   │   ├── ApiTestData.java
        │   │   │   ├── posts/
        │   │   │   │   ├── CreatePostRequest.java
        │   │   │   │   └── PostResponse.java
        │   │   │   └── users/
        │   │   │       ├── UserAddress.java
        │   │   │       ├── UserAddressGeo.java
        │   │   │       ├── UserCompany.java
        │   │   │       └── UserResponse.java
        │   │   ├── enums/
        │   │   │   ├── ContentType.java
        │   │   │   └── Endpoint.java
        │   │   └── test/
        │   │       └── ApiTest.java
        │   ├── db/
        │   │   ├── base/
        │   │   │   ├── DbAware.java
        │   │   │   ├── DbResultExtension.java
        │   │   │   └── Repository.java
        │   │   ├── config/
        │   │   │   ├── DbEnvironmentDataManager.java
        │   │   │   └── DbTestDataManager.java
        │   │   ├── dto/
        │   │   │   ├── DbEnvironmentData.java
        │   │   │   └── DbTestData.java
        │   │   ├── enums/
        │   │   │   ├── DbEnvironmentKey.java
        │   │   │   └── TestStatus.java
        │   │   ├── mock/
        │   │   │   └── TestRunner.java
        │   │   ├── model/
        │   │   │   ├── Author.java
        │   │   │   ├── Project.java
        │   │   │   ├── SessionModel.java
        │   │   │   ├── Status.java
        │   │   │   └── TestModel.java
        │   │   ├── repository/
        │   │   │   ├── AuthorRepository.java
        │   │   │   ├── ProjectRepository.java
        │   │   │   ├── SessionModelRepository.java
        │   │   │   ├── StatusRepository.java
        │   │   │   └── TestRepository.java
        │   │   ├── test/
        │   │   │   └── DatabaseTest.java
        │   │   └── util/
        │   │       ├── BuildNumberUtil.java
        │   │       ├── HibernateUtil.java
        │   │       └── SessionModelUtil.java
        │   └── framework/
        │       ├── base/
        │       │   └── BaseTest.java
        │       ├── client/
        │       │   ├── ApiClient.java
        │       │   └── ApiResponse.java
        │       ├── spec/
        │       │   ├── AqualityLoggingFilter.java
        │       │   └── Specifications.java
        │       └── util/
        │           ├── JsonUtil.java
        │           └── RandomUtil.java
        └── resources/
            ├── api/
            │   ├── environment.json
            │   └── test-data.json
            ├── db/
            │   └── test-data.json
            ├── hibernate.cfg.xml
            └── log4j2.properties
```

## Как запустить

> Все команды выполняются в **cmd**. После установки Java/Maven/MySQL перезапустите окно cmd, чтобы обновился `PATH`.

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

### Шаг 3. Установите MySQL Server и клиент `mysql`

**Вариант А — установщик с официального сайта (рекомендуется):**

1. Откройте страницу загрузки MySQL Installer: <https://dev.mysql.com/downloads/installer/>
   (страница загрузки самого сервера: <https://dev.mysql.com/downloads/mysql/>).
2. Скачайте **mysql-installer-community-8.x.msi** (подойдёт и «web»-вариант небольшого размера).
3. Запустите установщик и выберите тип установки **Custom** (или **Server only**):
   - **MySQL Server 8.x** — сам сервер;
   - **MySQL Shell** / **Client Programs** — консольный клиент `mysql.exe`;
   - **MySQL Workbench** (опционально) — GUI для просмотра таблиц.
4. На шаге **Accounts and Roles** задайте пароль пользователя `root` — он понадобится для `.env`.
5. На шаге **Windows Service** оставьте `MySQL80` с автозапуском (**Start the MySQL Server at System Startup**).
6. Завершите установку и проверьте, что клиент и сервер доступны:

   ```bat
   mysql --version
   sc query MySQL80
   ```

**Вариант Б — установка одной командой через winget:**

```bat
winget install --id Oracle.MySQL --source winget --accept-package-agreements --accept-source-agreements
```

### Шаг 4. Восстановите базу из дампа

1. Склонируйте репозиторий с дампом в папку `db-dump` в корне проекта:

   ```bat
   cd /d <путь к репозиторию>
   git clone https://github.com/tquality-education/db_task_dump.git db-dump
   ```

   Проверьте, что появился файл `db-dump\dump.sql` (папка `db-dump/` добавлена в `.gitignore`, поэтому клонируется отдельно).

2. Скопируйте `.env.example` в `.env` и заполните его своими значениями (подробнее — в разделе «Конфигурация»):

   ```bat
   copy .env.example .env
   ```

   В `.env` укажите пользователя `root` и пароль, заданный при установке MySQL, например:

   ```properties
   DB_HOST=localhost
   DB_PORT=3306
   DB_SCHEMA=union_reporting
   DB_USER=root
   DB_PASSWORD=change_me
   DB_PARAMS=useSSL=false&serverTimezone=UTC&characterEncoding=utf8
   ```

3. Восстановите базу из дампа. Подставьте имя пользователя из `.env` (`DB_USER`; в примере — `root`); дамп сам создаёт схему `union_reporting`:

   ```bat
   mysql -u root -p < db-dump\dump.sql
   ```

   Команда запросит пароль (тот же, что в `DB_PASSWORD`). Для неинтерактивного запуска пароль можно передать ключом `-p<пароль>` без пробела:

   ```bat
   mysql -u root -pchange_me < db-dump\dump.sql
   ```

4. Проверьте, что база и таблицы восстановились:

   ```bat
   mysql -u root -p -e "USE union_reporting; SHOW TABLES; SELECT COUNT(*) FROM test;"
   ```

   Ожидаемые таблицы: `test`, `project`, `author`, `status`, `session`, `api_key`, `attachment`, `dev_info`, `fail_reason`, `log`, `rel_fail_reason_test`, `token`, `variant`. В таблице `status` должны быть значения `PASSED`, `FAILED`, `SKIPPED` (используются тестами).

### Шаг 5. Запустите тесты

После выполнения подготовительных шагов:

```bat
cd /d <путь к репозиторию>
mvn clean test
```

Maven скачает зависимости и запустит тесты: `ApiTest` (6 шагов проверок + запись результата в БД) и `DatabaseTest` (Тест-кейс 2). API-стенд публичный, дополнительных настроек не требуется; для БД нужен работающий MySQL и заполненный `.env`.

**Логи** пишутся в `logs/test.log` и дублируются в консоль. Файл перезаписывается на каждом запуске.

### Запуск через IntelliJ IDEA

1. Установите Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open** и выберите корень репозитория.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Выполните `ApiTest` (`src/test/java/ru/kaelth4s/api/test/`) или `DatabaseTest` (`src/test/java/ru/kaelth4s/db/test/`) → ПКМ → **Run**.

## Конфигурация

### `.env` (подключение к MySQL)

Файл создаётся копированием `.env.example` и в git не попадает. Ключи читает `DbEnvironmentDataManager` (java-dotenv, с fallback на переменные окружения и значения по умолчанию из `DbEnvironmentKey`).

```properties
DB_HOST=localhost
DB_PORT=3306
DB_SCHEMA=union_reporting
DB_USER=root
DB_PASSWORD=change_me
DB_PARAMS=useSSL=false&serverTimezone=UTC&characterEncoding=utf8
```

Из этих значений собирается JDBC-URL `jdbc:mysql://<DB_HOST>:<DB_PORT>/<DB_SCHEMA>?<DB_PARAMS>`.

### `hibernate.cfg.xml`

Диалект, драйвер `com.mysql.cj.jdbc.Driver`, вывод SQL в лог и список mapping-классов (`TestModel`, `Project`, `Author`, `Status`, `SessionModel`). URL/пользователь/пароль в рантайме подменяются значениями из `.env` (см. `HibernateUtil`).

### `api/environment.json`

URL тестируемого стенда:

```json
{
  "baseUrl": "https://jsonplaceholder.typicode.com"
}
```

### `api/test-data.json`

Тестовые данные, разбитые по шагам `ApiTest`:

- `expectedPostGetRequest` — ожидаемые `id` и `userId` для шага 2 (`GET /posts/99`);
- `badPostGetRequestId` — несуществующий `id` поста для шага 3 (`GET /posts/150` → 404);
- `creatingPostRequest.userId` — `userId` для шага 4 (`POST /posts`); `title` и `body` генерируются динамически через `RandomUtil`;
- `expectedUserGetRequest` — полный ожидаемый объект пользователя с `id = 5` для шагов 5 и 6.

### `db/test-data.json`

Данные для `DatabaseTest`: автор, проект (в него копируются тесты) и лимит выбираемых ID.

```json
{
  "author": {
    "name": "Artem",
    "login": "kaelth4s",
    "email": "kaelth4s.dev@gmail.com"
  },
  "project": {
    "name": "task/database"
  },
  "idsLimit": 10
}
```

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