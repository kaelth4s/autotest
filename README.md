[Русский](README_RU.md) | **English**

# Java Autotests — a Portfolio of Solutions

A repository compiling my **Java** autotests: UI, API and DB, standalone and as end-to-end scenarios.
The goal is to show how I design test frameworks, layers and test data.

Every solution lives in its **own branch** with its own README, tests and sources. `main`
deliberately holds no code — it is the repository index.

Documentation for every solution is bilingual: [`README.md`](../../tree/demo/demoqa-ui/README.md) — English
(primary), [`README_RU.md`](../../tree/demo/demoqa-ui/README_RU.md) — Russian; each file starts with a
language switcher.

Author: **kaelth4s** · License: [MIT](LICENSE)

## Repository layout

| Branch | What it demonstrates | Key tests | Stack |
|---|---|---|---|
| [`demo/demoqa-ui`](../../tree/demo/demoqa-ui) | A UI framework **built from scratch**: custom `BaseElement` / `BaseForm`, browser factory, driver singleton, custom waits | `AlertsTest`, `IframeTest`, `TablesTest`, `HandlesTest`, `SliderTest`, `DatePickerTest` | Selenium 4.47, JUnit 5, AssertJ, Jackson, DataFaker, Logback |
| [`demo/steam-ui`](../../tree/demo/steam-ui) | Page Object + page components, checkbox filters, result counters, comparing data across pages | `StoreAboutTest`, `TopSellersTest`, `MarketTest` | Selenium 4.47, WebDriverManager, JUnit 5, AssertJ, Jackson |
| [`demo/userinyerface-ui`](../../tree/demo/userinyerface-ui) | Working with **Aquality Selenium**: profile-based runs, Element Factory, custom checkboxes, file upload without the native dialog via JS | `CardsTest`, `CookiesTest`, `HelpFormTest`, `TimerTest` | Aquality Selenium 4.25, Selenium 4.48, JUnit 5, AssertJ, DataFaker |
| [`demo/jsonplaceholder-api`](../../tree/demo/jsonplaceholder-api) | The API layer: abstract `ApiClient`, generic `ApiResponse<T>`, enum endpoints with path parameters, negative scenarios kept apart from positive ones | `ApiTest` — 6 steps over `/posts` and `/users` | Rest Assured 6.0.1, Aquality (logging), JUnit 5, AssertJ, Jackson |
| [`demo/local-db`](../../tree/demo/local-db) | **API + DB**: every test result is written to the DB through a JUnit extension, CRUD via JPA/Hibernate repositories with no raw SQL in tests | `ApiTest`, `DatabaseTest` (copying and running tests, verification, cleanup) | Rest Assured, Hibernate 6.5, MySQL Connector/J, Lombok, java-dotenv, JUnit 5 |
| [`demo/local-docker-api-db-ui`](../../tree/demo/local-docker-api-db-ui) | An **end-to-end UI + API + DB test** against a Dockerized stand: token via API, authentication and cookie on the UI, table checked against the DB, a test created with a log and a screenshot | `ExamTest` — 6 task steps | Aquality Selenium, Rest Assured, Hibernate + `hibernate-jpamodelgen`, MySQL, Docker Compose |
| [`demo/yandexdisk-api-ui`](../../tree/demo/yandexdisk-api-ui) | **UI + API against a real service**: automatic login and session reuse, assertions without page reloads, image verified by MD5 | `YandexDiskTest` — 15 steps | Aquality Selenium, Rest Assured, JUnit 5, AssertJ, java-dotenv |

The baseline shared by all branches: **Java 25 (Temurin)**, **Maven 3.9.16**, **JUnit Jupiter 6.1.3**,
**AssertJ 3.27.7**, **Jackson Databind 2.22.2**, **DataFaker 2.7.0**, Log4j2 / Logback logging,
Apache Maven build, development on Windows 10/11.

## Engineering practices

- **Page Object / Page Component + Form** — pages, forms and repeated blocks are extracted into separate
  classes, locators live in `private static final By`, not in the tests.
- **Layering** — a test calls domain methods of clients and repositories and knows nothing about HTTP
  methods, HQL or locators; negative scenarios are separated from positive ones.
- **Configuration and data in JSON, not in code** — `config.json`, `environment.json`, `test-data.json`
  are mapped to record DTOs via Jackson; secrets and credentials live in `.env` (an `.env.example`
  template is committed).
- **Abstractions instead of duplication** — base `BaseElement` / `BaseForm` / `BasePage` / `BaseTest`,
  an abstract `Repository<T, ID>` with transaction and rollback, a generic `ApiResponse<T>`.
- **Type-safe literals** — enums instead of inline strings: `Attribute`, `Endpoint`, `CookieName`,
  `TestStatus`, `SortType`, `JsScript` and others.
- **Explicit waits** instead of `sleep` — custom waits configured per project, polling of asynchronous
  APIs, and waiting for "the UI has caught up with a change made through the API".
- **Generated test data** — `RandomUtil` / `@RandomGenerated` on top of DataFaker, run artifacts kept in
  `.gitignore`, so tests do not depend on the state of the stand.
- **Singletons where a single resource is required** — `DriverManager`, `ConfigManager`, `SessionFactory`.
- **Logs and artifacts** — a single logging entry point (Aquality / Log4j2), `logs/test.log`, plus
  screenshots and attachments in the DB for the end-to-end test.

## Quick start

1. Install **Java 25 (Eclipse Temurin)** and **Maven 3.9.16**, then restart the terminal:

   ```bat
   winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
   java -version
   mvn -version
   ```

2. Switch to the branch you need and run the tests from the project root:

   ```bat
   git clone https://github.com/kaelth4s/autotest.git
   cd autotest
   git checkout demo/demoqa-ui
   mvn clean test
   ```

3. Run a single test or pick a browser profile (where profile-based runs are supported):

   ```bat
   mvn test -Dtest=AlertsTest
   mvn clean test -Dprofile=firefox
   ```

### Per-branch notes

- **`demo/demoqa-ui`, `demo/steam-ui`** — only a browser is required (Chrome by default, Firefox can be
  selected in the config); drivers are downloaded automatically.
- **`demo/userinyerface-ui`, `demo/local-docker-api-db-ui`, `demo/yandexdisk-api-ui`** — run with a
  profile: `mvn clean test -Dprofile=chrome` (or `firefox`).
- **`demo/local-db`** — a local **MySQL 8.x** is required, with the `union_reporting` schema restored from
  a dump; the dump is cloned separately into `db-dump/`, credentials live in `.env`
  (`git clone https://github.com/tquality-education/db_task_dump.git db-dump`).
- **`demo/local-docker-api-db-ui`** — **Docker Desktop + Docker Compose** are required; the stand and the
  DB are started from [docker-kits](https://github.com/tquality-education-exam/docker-kits)
  (`docker-compose up -d`), UI — `http://localhost:8080/web`, API — `http://localhost:8080/api`,
  MySQL — `localhost:3306`.
- **`demo/yandexdisk-api-ui`** — `client_id`, a Yandex ID login and password are required in `.env`; if
  two-factor authentication is enabled, confirm the login in the opened browser window. The session is
  saved in `auth-session/` and reused by subsequent runs.

Exact steps, library versions and the structure of each project are in the README of the corresponding
branch.
