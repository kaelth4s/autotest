[Русский](README_RU.md) | **English**

# API autotests + database work through models (CRUD)

## Solution overview

The project consists of two parts collected in a single Maven module:

1. **API autotests** for the public REST sandbox **JSONPlaceholder** (<https://jsonplaceholder.typicode.com/>) — Java + Rest Assured; the result of every test is automatically written to the database (**Test case 1**).
2. **Database work** with `union_reporting` through JPA/Hibernate: entity models, repositories with CRUD methods, and a test that reproduces the task scenario (**Test case 2**).

There are no raw SQL queries in the tests: database interaction is encapsulated in repository classes (`get`, `add`, `update`, `delete`, `findAll`, `getOrCreate`), and the test calls ready-made methods.

### Test case 1. Adding a new record

Implemented as a JUnit 5 extension **`DbResultExtension`** attached to `ApiTest` (`@ExtendWith`).

| Step | Expected result | Implementation |
|---|---|---|
| 1. Run any test | Test passed | `ApiTest.shouldPassApiResponses()` — 6 verification steps |
| Postcondition: add the test result as a new record to the corresponding table | Information added | `DbResultExtension.afterEach` → `TestRepository.addTestResult` (`INSERT` into `test`) |

What goes into the record: test name, `method` (class + method), status (`PASSED` / `FAILED` / `SKIPPED` — determined from `TestExecutionException`), start/end time, project, author and run session. Missing reference records (`project`, `author`, `session`) are created automatically through `getOrCreate`.

### Test case 2. Test data handling

Implemented in `DatabaseTest`.

| Phase | Step | Implementation |
|---|---|---|
| Precondition | Select tests whose ID contains two repeated digits (`11`, `77`, …), but no more than 10 | `TestRepository.findRandomIdsWithRepeatingDigits` — a native query with `REGEXP '00\|11\|…\|99'`, `ORDER BY RAND()`, `LIMIT :limit` |
| Precondition | Copy those tests, specifying the current project and author | `TestRepository.copyTests` → `cloneWithNewOwner` (the copy gets the current `project`, `author` and `session`) |
| Step | Simulate running each test with a mock utility and update the information in the database | `TestRunner.runTest` — sets random `start_time` / `end_time` (1–20 s) and the `PASSED` status; `TestRepository.updateAll` — `merge` in a single transaction |
| Verification | Tests passed, information updated | `assertThat(oldResults).usingRecursiveComparison().isNotEqualTo(newResults)` |
| Postcondition | Delete the created records | `TestRepository.deleteAllByIds` in `@AfterEach` |

## How it works

### Database layer

- **`Repository<T, ID>`** — a base abstract repository: `inTransaction`, `save`, `update`, `delete`, `findById`. A transaction is opened per operation, and on `RuntimeException` it performs a `rollback`.
- **`HibernateUtil`** — a singleton `SessionFactory`; it reads `hibernate.cfg.xml` and substitutes the URL/login/password with values from `.env` (or from environment variables).
- **Entity models** — `TestModel` (`test`), `Project` (`project`), `Author` (`author`), `Status` (`status`), `SessionModel` (`session`); `@ManyToOne` (LAZY) associations to status, project, session and author.
- **`TestStatus`** — an enum of statuses with their database names (`PASSED` / `FAILED` / `SKIPPED`); `StatusRepository` resolves a status by name and fails with an explicit error if the dump has no such status.
- **`getOrCreate`** in every reference repository — automation of adding missing information (project, author, session), as required by the task FAQ.
- **Auto-fill substitution** — `SessionModelUtil` creates a run session with a build number from `BuildNumberUtil` (format `yyyyMMddHHmm`).
- **Test data** — `db/test-data.json` (author, project, ID limit), read through `JsonUtil` into `DbTestData`.


### Libraries used

| Library | Version | Purpose |
|---|---|---|
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | HTTP client and DSL for API requests |
| [Hibernate ORM](https://hibernate.org/orm/) | 6.5.2.Final | JPA models and MySQL work |
| [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) | 26.7.0 | MySQL JDBC driver |
| [Lombok](https://projectlombok.org/) | 1.18.48 | getters/setters/constructors for entities |
| [java-dotenv](https://github.com/cdimascio/dotenv-java) | 5.2.2 | reading `.env` |
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | unified logging environment (wrapper over Log4j2) |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | test framework and extension API |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | reading JSON (configs, test data, response deserialization) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | generating random test data |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | logging (transitively through Aquality) |

The project is built with **Apache Maven**.

### Environment

- OS: Windows 10/11 (the instructions assume `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- MySQL Server: **8.x** + the `mysql` client
- IDE (optional): IntelliJ IDEA

## Project structure

```text
<repository root>/
├── pom.xml
├── .env.example
├── db-dump/                      # DB dump (cloned separately, kept in .gitignore)
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

## How to run

> All commands are executed in **cmd**. After installing Java/Maven/MySQL, restart the cmd window so that `PATH` is refreshed.

### Step 1. Install Java 25 (Eclipse Temurin)

If it is not installed yet. `winget` is available right in cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Restart cmd and verify the installation:

```bat
java -version
```

### Step 2. Install Maven 3.9.16

**Option A — automatic installation with a single command (from cmd):**

```bat
powershell -Command "$url='https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip'; $dir='%USERPROFILE%\maven'; New-Item -ItemType Directory -Force -Path $dir | Out-Null; $zip=Join-Path $dir 'maven.zip'; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dir -Force; Remove-Item $zip; $mvn=Join-Path $dir 'apache-maven-3.9.16'; [Environment]::SetEnvironmentVariable('MAVEN_HOME', $mvn, 'User'); $path=[Environment]::GetEnvironmentVariable('Path', 'User'); if ($path -notlike '*'+$mvn+'\bin*') { [Environment]::SetEnvironmentVariable('Path', $path+';'+$mvn+'\bin', 'User') }"
```

**Option B — manual installation:**

1. Download the archive manually: <https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip>
2. Unpack it into `%USERPROFILE%\maven` (for example, `C:\Users\<UserName>\maven`; create that folder if it does not exist).
3. Run the following in cmd:

   ```bat
   setx MAVEN_HOME "%USERPROFILE%\maven\apache-maven-3.9.16"
   setx Path "%Path%;%USERPROFILE%\maven\apache-maven-3.9.16\bin"
   ```

4. Restart cmd and verify:

   ```bat
   mvn -version
   ```

### Step 3. Install MySQL Server and the `mysql` client

**Option A — the installer from the official website (recommended):**

1. Open the MySQL Installer download page: <https://dev.mysql.com/downloads/installer/>
   (the server download page itself: <https://dev.mysql.com/downloads/mysql/>).
2. Download **mysql-installer-community-8.x.msi** (the small "web" variant works too).
3. Run the installer and choose the installation type **Custom** (or **Server only**):
   - **MySQL Server 8.x** — the server itself;
   - **MySQL Shell** / **Client Programs** — the `mysql.exe` console client;
   - **MySQL Workbench** (optional) — a GUI for browsing tables.
4. At the **Accounts and Roles** step, set the password for the `root` user — it will be needed for `.env`.
5. At the **Windows Service** step, keep `MySQL80` with autostart (**Start the MySQL Server at System Startup**).
6. Finish the installation and verify that the client and the server are available:

   ```bat
   mysql --version
   sc query MySQL80
   ```

**Option B — installation with a single command through winget:**

```bat
winget install --id Oracle.MySQL --source winget --accept-package-agreements --accept-source-agreements
```

### Step 4. Restore the database from the dump

1. Clone the dump repository into the `db-dump` folder in the project root:

   ```bat
   cd /d <path to repository>
   git clone https://github.com/tquality-education/db_task_dump.git db-dump
   ```

   Make sure the file `db-dump\dump.sql` has appeared (the `db-dump/` folder is listed in `.gitignore`, so it is cloned separately).

2. Copy `.env.example` to `.env` and fill it in with your own values (see the "Configuration" section for details):

   ```bat
   copy .env.example .env
   ```

   In `.env`, specify the `root` user and the password you set during the MySQL installation, for example:

   ```properties
   DB_HOST=localhost
   DB_PORT=3306
   DB_SCHEMA=union_reporting
   DB_USER=root
   DB_PASSWORD=change_me
   DB_PARAMS=useSSL=false&serverTimezone=UTC&characterEncoding=utf8
   ```

3. Restore the database from the dump. Substitute the user name from `.env` (`DB_USER`; in the example — `root`); the dump creates the `union_reporting` schema itself:

   ```bat
   mysql -u root -p < db-dump\dump.sql
   ```

   The command will prompt for the password (the same one as in `DB_PASSWORD`). For a non-interactive run, the password can be passed with the `-p<password>` flag without a space:

   ```bat
   mysql -u root -pchange_me < db-dump\dump.sql
   ```

4. Verify that the database and tables have been restored:

   ```bat
   mysql -u root -p -e "USE union_reporting; SHOW TABLES; SELECT COUNT(*) FROM test;"
   ```

   Expected tables: `test`, `project`, `author`, `status`, `session`, `api_key`, `attachment`, `dev_info`, `fail_reason`, `log`, `rel_fail_reason_test`, `token`, `variant`. The `status` table must contain the values `PASSED`, `FAILED`, `SKIPPED` (they are used by the tests).

### Step 5. Run the tests

After completing the preparation steps:

```bat
cd /d <path to repository>
mvn clean test
```

Maven downloads the dependencies and runs the tests: `ApiTest` (6 verification steps + writing the result to the database) and `DatabaseTest` (Test case 2). The API sandbox is public and needs no extra configuration; the database requires a running MySQL and a filled-in `.env`.

**Logs** are written to `logs/test.log` and duplicated to the console. The file is overwritten on every run.

### Running through IntelliJ IDEA

1. Install Java 25 through winget (see Step 1).
2. Open the project: **File → Open** and select the repository root.
3. Wait for the dependencies to load and click **Load Maven** in the dialog that appears.
4. Select the JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Run `ApiTest` (`src/test/java/ru/kaelth4s/api/test/`) or `DatabaseTest` (`src/test/java/ru/kaelth4s/db/test/`) → right-click → **Run**.

## Configuration

### `.env` (MySQL connection)

The file is created by copying `.env.example` and never enters git. Its keys are read by `DbEnvironmentDataManager` (java-dotenv, with a fallback to environment variables and default values from `DbEnvironmentKey`).

```properties
DB_HOST=localhost
DB_PORT=3306
DB_SCHEMA=union_reporting
DB_USER=root
DB_PASSWORD=change_me
DB_PARAMS=useSSL=false&serverTimezone=UTC&characterEncoding=utf8
```

The JDBC URL `jdbc:mysql://<DB_HOST>:<DB_PORT>/<DB_SCHEMA>?<DB_PARAMS>` is assembled from these values.

### `hibernate.cfg.xml`

The dialect, the `com.mysql.cj.jdbc.Driver` driver, SQL output to the log, and the list of mapping classes (`TestModel`, `Project`, `Author`, `Status`, `SessionModel`). At runtime the URL/user/password are substituted with the values from `.env` (see `HibernateUtil`).

### `api/environment.json`

The URL of the sandbox under test:

```json
{
  "baseUrl": "https://jsonplaceholder.typicode.com"
}
```

### `api/test-data.json`

Test data split by `ApiTest` steps:

- `expectedPostGetRequest` — the expected `id` and `userId` for step 2 (`GET /posts/99`);
- `badPostGetRequestId` — a non-existent post `id` for step 3 (`GET /posts/150` → 404);
- `creatingPostRequest.userId` — the `userId` for step 4 (`POST /posts`); `title` and `body` are generated dynamically through `RandomUtil`;
- `expectedUserGetRequest` — the complete expected user object with `id = 5` for steps 5 and 6.

### `db/test-data.json`

Data for `DatabaseTest`: the author, the project (tests are copied into it) and the limit on the number of IDs to select.

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

A single log format for the console and the `logs/test.log` file:

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
