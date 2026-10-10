# Автотесты на Java — портфолио решений

Репозиторий-компиляция моих автотестов на **Java**: UI, API и БД, отдельно и в сквозных сценариях.
Цель — показать, как я проектирую тестовые фреймворки, слои и тестовые данные.

Каждое решение живёт в **отдельной ветке** со своим README, тестами и исходниками. В `main`
намеренно нет кода — это оглавление репозитория.

Документация в каждой ветке двуязычная: [`README.md`](../../tree/demo/demoqa-ui) — английский
(основной), `README_RU.md` — русский; в начале каждого файла есть переключатель языка.

Автор: **kaelth4s** · Лицензия: [MIT](LICENSE)

## Как устроен репозиторий

| Ветка | Что демонстрирует | Ключевые тесты | Стек |
|---|---|---|---|
| [`demo/demoqa-ui`](../../tree/demo/demoqa-ui) | UI-фреймворк **с нуля**: собственные `BaseElement` / `BaseForm`, фабрика браузеров, синглтон драйвера, кастомные ожидания | `AlertsTest`, `IframeTest`, `TablesTest`, `HandlesTest`, `SliderTest`, `DatePickerTest` | Selenium 4.47, JUnit 5, AssertJ, Jackson, DataFaker, Logback |
| [`demo/steam-ui`](../../tree/demo/steam-ui) | Page Object + компоненты страниц, чекбокс-фильтры, счётчики результатов, сравнение данных между страницами | `StoreAboutTest`, `TopSellersTest`, `MarketTest` | Selenium 4.47, WebDriverManager, JUnit 5, AssertJ, Jackson |
| [`demo/userinyerface-ui`](../../tree/demo/userinyerface-ui) | Работа на **Aquality Selenium**: профильный запуск, Element Factory, кастомные чекбоксы, загрузка файла без нативного диалога через JS | `CardsTest`, `CookiesTest`, `HelpFormTest`, `TimerTest` | Aquality Selenium 4.25, Selenium 4.48, JUnit 5, AssertJ, DataFaker |
| [`demo/jsonplaceholder-api`](../../tree/demo/jsonplaceholder-api) | API-слой: абстрактный `ApiClient`, generic `ApiResponse<T>`, enum-эндпоинты с path-параметрами, негативные сценарии отдельно от позитивных | `ApiTest` — 6 шагов по `/posts` и `/users` | Rest Assured 6.0.1, Aquality (логирование), JUnit 5, AssertJ, Jackson |
| [`demo/local-db`](../../tree/demo/local-db) | **API + БД**: результат каждого теста пишется в БД через JUnit-extension, CRUD через JPA/Hibernate-репозитории без «сырых» SQL в тестах | `ApiTest`, `DatabaseTest` (копирование и прогон тестов, проверка, откат) | Rest Assured, Hibernate 6.5, MySQL Connector/J, Lombok, java-dotenv, JUnit 5 |
| [`demo/local-docker-api-db-ui`](../../tree/demo/local-docker-api-db-ui) | **Сквозной UI + API + БД тест** на стенде в Docker: токен по API, авторизация и cookie на UI, сверка таблицы с БД, создание теста с логом и скриншотом | `ExamTest` — 6 шагов задания | Aquality Selenium, Rest Assured, Hibernate + `hibernate-jpamodelgen`, MySQL, Docker Compose |
| [`demo/yandexdisk-api-ui`](../../tree/demo/yandexdisk-api-ui) | **UI + API на реальном сервисе**: автологин и переиспользование сессии, проверки без перезагрузки страницы, сверка картинки по MD5 | `YandexDiskTest` — 15 шагов | Aquality Selenium, Rest Assured, JUnit 5, AssertJ, java-dotenv |

Одинаковый для всех веток минимум: **Java 25 (Temurin)**, **Maven 3.9.16**, **JUnit Jupiter 6.1.3**,
**AssertJ 3.27.7**, **Jackson Databind 2.22.2**, **DataFaker 2.7.0**, логирование Log4j2 / Logback,
сборка Apache Maven, разработка под Windows 10/11.

## Общие инженерные приёмы

- **Page Object / Page Component + Form** — страницы, формы и повторяющиеся блоки вынесены в отдельные
  классы, локаторы — в `private static final By`, а не в тестах.
- **Слоистость** — тест вызывает доменные методы клиентов и репозиториев и не знает про HTTP-методы,
  HQL и локаторы; негативные сценарии отделены от позитивных.
- **Конфигурация и данные — в JSON, а не в коде** — `config.json`, `environment.json`, `test-data.json`
  маппятся в record-DTO через Jackson; секреты и доступы — в `.env` (шаблон `.env.example` в репозитории).
- **Абстракции вместо дублирования** — базовые `BaseElement` / `BaseForm` / `BasePage` / `BaseTest`,
  абстрактный `Repository<T, ID>` с транзакцией и rollback, generic `ApiResponse<T>`.
- **Типобезопасные литералы** — enum'ы вместо строк на месте вызова: `Attribute`, `Endpoint`,
  `CookieName`, `TestStatus`, `SortType`, `JsScript` и др.
- **Явные ожидания** вместо `sleep` — кастомные ожидания из конфига, поллинг асинхронных API,
  ожидание «UI догнал изменение, сделанное по API».
- **Генерируемые тестовые данные** — `RandomUtil` / `@RandomGenerated` на DataFaker, файлы прогонов в
  `.gitignore`, чтобы тесты не зависели от состояния стенда.
- **Синглтоны там, где нужен один ресурс** — `DriverManager`, `ConfigManager`, `SessionFactory`.
- **Логи и артефакты** — единая точка логирования (Aquality / Log4j2), `logs/test.log`,
  скриншоты и вложения в БД в сквозном тесте.

## Быстрый старт

1. Установите **Java 25 (Eclipse Temurin)** и **Maven 3.9.16**, перезапустите терминал:

   ```bat
   winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
   java -version
   mvn -version
   ```

2. Переключитесь на нужную ветку и запустите тесты из корня проекта:

   ```bat
   git clone https://github.com/kaelth4s/autotest.git
   cd autotest
   git checkout demo/demoqa-ui
   mvn clean test
   ```

3. Отдельный тест или профиль браузера (там, где поддерживается профильный запуск):

   ```bat
   mvn test -Dtest=AlertsTest
   mvn clean test -Dprofile=firefox
   ```

### Особенности запуска по веткам

- **`demo/demoqa-ui`, `demo/steam-ui`** — нужен только браузер (Chrome по умолчанию, Firefox меняется
  в конфиге); драйверы скачиваются автоматически.
- **`demo/userinyerface-ui`, `demo/local-docker-api-db-ui`, `demo/yandexdisk-api-ui`** — запуск
  профилем: `mvn clean test -Dprofile=chrome` (или `firefox`).
- **`demo/local-db`** — требуется локальный **MySQL 8.x** и восстановленная из дампа схема
  `union_reporting`; дамп клонируется отдельно в `db-dump/`, доступы — в `.env`
  (`git clone https://github.com/tquality-education/db_task_dump.git db-dump`).
- **`demo/local-docker-api-db-ui`** — требуется **Docker Desktop + Docker Compose**; стенд и БД
  поднимаются из [docker-kits](https://github.com/tquality-education-exam/docker-kits)
  (`docker-compose up -d`), UI — `http://localhost:8080/web`, API — `http://localhost:8080/api`,
  MySQL — `localhost:3306`.
- **`demo/yandexdisk-api-ui`** — нужны `client_id`, логин и пароль Яндекс ID в `.env`; при включённой
  двухфакторной аутентификации подтвердите вход в открытом окне браузера. Сессия сохраняется в
  `auth-session/` и переиспользуется следующими прогонами.

Точные шаги, версии библиотек и структура каждого проекта — в README соответствующей ветки.
