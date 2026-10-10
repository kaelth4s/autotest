[Русский](README_RU.md) | **English**

# UI autotests for demoqa.com

## Solution overview

A set of automated UI tests for the **DemoQA** website (<https://demoqa.com/>), written in **Java**. The project implements its own test framework from scratch: base element and form classes, a browser factory, a driver singleton and a set of utilities. The key website scenarios covered are:

| Test | What it verifies |
|---|---|
| `AlertsTest` | working with JavaScript alerts: `alert`, `confirm`, `prompt`, text validation and data input |
| `IframeTest` | nested frames: navigating parent/child iframes, comparing text in Frames and Nested Frames |
| `TablesTest` | CRUD with tables: adding a user through the registration form, verifying the data in the table, deleting a row (DDT + parameterization) |
| `HandlesTest` | opening/closing tabs, switching between them, following the Home link |
| `SliderTest` | setting a random slider value, stopping the Progress Bar at the engineer's age value |
| `DatePickerTest` | validating the current date field values, selecting the nearest February 29 |

How it is built:

- **Custom test framework** — abstract `BaseElement` (click, reading text/attributes, finding child elements) and `BaseForm` (open check by a unique element), which all elements and pages inherit from;
- **Page Object pattern** — every DemoQA page is described by its own class (`tests/page/`), reusable forms are moved to `tests/form/`, the custom calendar — to `tests/element/DatePicker`;
- **Composition** — `BaseCategoryForm` holds `LeftPanelForm`; all category pages inherit from it and get left-menu navigation without code duplication;
- **Navigation via an enum** — `Category` (group + menu item href); navigation is performed by a single method `LeftPanelForm.clickCategory(Category)`;
- **Singleton** (`DriverManager`) — WebDriver is created once and reused until the test finishes;
- **Factory Method** (`BrowserFactory` + `ChromeBrowserFactory` / `FirefoxBrowserFactory`) — the browser is selected in the configuration (`config.json`), and the factory creates the required driver instance;
- **Explicit waits** — `WaitUtil` works with `By` locators: base waits (`defaultWaitDuration`) plus named custom waits from the config (`additionalWaits`, e.g. `progressBar` with a timeout and polling interval);
- **Type-safe attributes** — the `Attribute` enum (`VALUE`, `HREF`, `CLASS`) instead of string literals;
- **Run configuration** (`config.json`) and **test data** (`test-data.json`) are stored in **JSON** and mapped to DTOs via Jackson (`ConfigManager`, `TestDataManager`);
- **Utilities**: `AlertUtil` (working with alert/confirm/prompt), `FrameUtil` (iframe navigation), `TabUtil` (tab management), `ActionUtil` (scrolling and keyboard via Actions), `JsonUtil` (reading JSON), `ParserUtil` (parsing numbers);
- **Working with models** — `RegistrationData` extends `BaseTableData`: a table row is mapped to a model by column headers, and comparison is done via `equals`/`hashCode`;
- **DDT + parameterization** — `TablesTest` uses `@ParameterizedTest` + `@MethodSource`, with data taken from `test-data.json`;
- **Random test data generation** — the custom `@RandomGenerated` annotation + `RandomFiller` fill record fields with random values via DataFaker;
- **Logging** — SLF4J + Logback, output to the console and to the `logs/latest-test.log` file.

### Libraries used

| Library | Version | Purpose |
|---|---|---|
| [Selenium WebDriver](https://www.selenium.dev/) | 4.47.0 | browser control |
| [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) | 6.3.4 | automatic webdriver download |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | test framework (including parameterized tests) |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | reading JSON (configs and test data) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | random test data generation |
| [Logback](https://logback.qos.ch/) | 1.6.3 | logging (console + file) |

The project is built with **Apache Maven**.

### Environment

- OS: Windows 10/11 (the instructions assume `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Browser: **Google Chrome** (default) or **Mozilla Firefox** — changed in `src/main/resources/config.json`
- IDE (optional): IntelliJ IDEA

## Project structure

```text
<корень репозитория>/
├── pom.xml                                          # Maven: зависимости и настройки сборки
├── logs/
│   └── latest-test.log                              # лог последнего запуска тестов
└── src/
├── main/
│   ├── java/ru/kaelth4s/
│   │   ├── framework/
│   │   │   ├── base/
│   │   │   │   ├── BaseElement.java             # абстрактный элемент: клик, текст, атрибуты, getChild
│   │   │   │   ├── BaseForm.java                # абстрактная форма: isDisplayed по уникальному элементу
│   │   │   │   └── BaseCategoryForm.java        # форма категории: композиция с LeftPanelForm
│   │   │   ├── browser/
│   │   │   │   ├── Browser.java                 # enum CHROME / FIREFOX / UNKNOWN
│   │   │   │   ├── BrowserFactory.java          # интерфейс фабрики
│   │   │   │   ├── ChromeBrowserFactory.java    # фабрика Chrome
│   │   │   │   └── FirefoxBrowserFactory.java   # фабрика Firefox
│   │   │   ├── dto/
│   │   │   │   ├── ConfigData.java              # record конфигурации (таймауты, URL, браузер, опции)
│   │   │   │   ├── WaitConfig.java              # record кастомного ожидания (timeout, pollingMillis)
│   │   │   │   ├── BrowserOptions.java          # record опций браузера
│   │   │   │   └── BaseTableData.java           # абстрактная модель строки таблицы
│   │   │   ├── element/
│   │   │   │   ├── Button.java                  # кнопка
│   │   │   │   ├── Dropdown.java                # выпадающий список
│   │   │   │   ├── Input.java                   # поле ввода
│   │   │   │   ├── Slider.java                  # слайдер (управление стрелками клавиатуры)
│   │   │   │   └── Table.java                   # таблица (маппинг строк в модели, пропуск пустых)
│   │   │   ├── enums/
│   │   │   │   └── Attribute.java               # enum атрибутов: VALUE, HREF, CLASS
│   │   │   ├── manager/
│   │   │   │   ├── ConfigManager.java           # синглтон конфигурации (из config.json)
│   │   │   │   └── DriverManager.java           # синглтон WebDriver
│   │   │   └── util/
│   │   │       ├── WaitUtil.java                # явные ожидания по By-локаторам + кастомные ожидания
│   │   │       ├── ActionUtil.java              # скролл к элементу, отправка клавиш (Actions)
│   │   │       ├── AlertUtil.java               # работа с alert/confirm/prompt
│   │   │       ├── FrameUtil.java               # навигация по iframe
│   │   │       ├── JsonUtil.java                # чтение JSON из ресурсов
│   │   │       ├── ParserUtil.java              # парсинг строк в int
│   │   │       └── TabUtil.java                 # управление вкладками браузера
│   │   └── tests/
│   │       ├── dto/
│   │       │   ├── RegistrationData.java        # модель пользователя (наследник BaseTableData)
│   │       │   └── Date.java                    # парсинг даты из строки
│   │       ├── element/
│   │       │   └── DatePicker.java              # элемент календаря (месяц/год/дни)
│   │       ├── enums/
│   │       │   ├── Category.java                # пункты левого меню (группа + href)
│   │       │   └── ConfigWaitKey.java           # ключи кастомных ожиданий из конфига
│   │       ├── form/
│   │       │   ├── LeftPanelForm.java           # левая панель: clickCategory(Category)
│   │       │   └── RegistrationForm.java        # форма регистрации (наследует BaseForm)
│   │       └── page/
│   │           ├── MainPage.java                # главная страница demoqa.com
│   │           ├── CategoryCardPage.java        # страница категории с левой панелью
│   │           ├── AlertsPage.java              # страница Alerts
│   │           ├── BrowserWindowsPage.java      # страница Browser Windows
│   │           ├── NestedFramesPage.java        # страница Nested Frames
│   │           ├── FramesPage.java              # страница Frames
│   │           ├── WebTablesPage.java           # страница Web Tables
│   │           ├── LinksPage.java               # страница Links
│   │           ├── SliderPage.java              # страница Slider
│   │           ├── ProgressBarPage.java         # страница Progress Bar (wait по aria-valuenow)
│   │           ├── DatePickerPage.java          # страница Date Picker
│   │           └── SamplePage.java              # страница sample (открывается во вкладке)
│   └── resources/
│       └── config.json                          # настройки: браузер, URL, таймауты, опции
└── test/
    ├── java/ru/kaelth4s/
    │   ├── framework/
    │   │   ├── base/
    │   │   │   └── BaseTest.java                # базовый класс тестов (setUp/tearDown)
    │   │   └── util/
    │   │       ├── RandomUtil.java              # генерация случайных значений (DataFaker)
    │   │       ├── RandomGenerated.java         # аннотация для автозаполнения
    │   │       ├── RandomFiller.java            # рефлексивный filler record'ов
    │   │       └── LocalDateUtil.java           # вычисление ближайшей даты (29 февраля)
    │   └── tests/
    │       ├── dto/
    │       │   ├── TestData.java                # record-обёртка всех тестовых данных
    │       │   ├── AlertsTestData.java          # DTO для AlertsTest
    │       │   ├── IframeTestData.java          # DTO для IframeTest
    │       │   ├── TablesTestData.java          # DTO для TablesTest
    │       │   ├── SliderTestData.java          # DTO для SliderTest
    │       │   └── DatePickerTestData.java      # DTO для DatePickerTest
    │       ├── manager/
    │       │   └── TestDataManager.java         # загрузка тестовых данных из JSON
    │       └── test/
    │           ├── AlertsTest.java              # тест алертов
    │           ├── IframeTest.java              # тест фреймов
    │           ├── TablesTest.java              # тест таблиц (параметризованный)
    │           ├── HandlesTest.java             # тест вкладок/хэндлов
    │           ├── SliderTest.java              # тест слайдера и прогресс-бара
    │           └── DatePickerTest.java          # тест выбора даты
    └── resources/
        ├── logback-test.xml                     # конфигурация логгера
        └── test-data.json                       # тестовые данные
```

## How to run

> All commands are executed in **cmd**. After installing Java/Maven, restart the cmd window so that `PATH` is updated.

### Preparation (common steps)

**Step 0. Install a browser and select it for the tests**

Download and install one of the supported browsers:

- **Google Chrome**: <https://www.google.com/chrome/>
- **Mozilla Firefox**: <https://www.firefox.com/thanks/>

Specify the required browser in the `src/main/resources/config.json` file:

```json
{
  "browser": "CHROME"
}
```

Available values: `CHROME`, `FIREFOX`. The driver for the selected browser is downloaded automatically via WebDriverManager.

**Step 1. Install Java 25 (Eclipse Temurin)** — if it is not installed. `winget` is available directly in cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Restart cmd and verify the installation:

```bat
java -version
```

**Step 2. Install Maven 3.9.16** — if it is not installed.

Option A — automatic installation with a single command (from cmd):

```bat
powershell -Command "$url='https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip'; $dir='%USERPROFILE%\maven'; New-Item -ItemType Directory -Force -Path $dir | Out-Null; $zip=Join-Path $dir 'maven.zip'; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dir -Force; Remove-Item $zip; $mvn=Join-Path $dir 'apache-maven-3.9.16'; [Environment]::SetEnvironmentVariable('MAVEN_HOME', $mvn, 'User'); $path=[Environment]::GetEnvironmentVariable('Path', 'User'); if ($path -notlike '*'+$mvn+'\bin*') { [Environment]::SetEnvironmentVariable('Path', $path+';'+$mvn+'\bin', 'User') }"
```

Option B — manual installation:

1. Download the archive manually from the browser: <https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip>
2. Unpack it into `%USERPROFILE%\maven` (for example, `C:\Users\<UserName>\maven`; create this folder if it does not exist).
3. In cmd, run:

   ```bat
   setx MAVEN_HOME "%USERPROFILE%\maven\apache-maven-3.9.16"
   setx Path "%Path%;%USERPROFILE%\maven\apache-maven-3.9.16\bin"
   ```

4. Restart cmd and verify:

   ```bat
   mvn -version
   ```

---

### Option 1. Running from the console

After completing the preparation steps (browser, Java 25, Maven):

```bat
cd /d <путь к репозиторию>
mvn clean test
```

Maven downloads all dependencies and runs all six tests. A browser window opens — do not close it manually while the tests are running.

Running one specific test:

```bat
mvn test -Dtest=AlertsTest
```

Logs are written to `task3\logs\latest-test.log`.

---

### Option 2. Running from IntelliJ IDEA

1. Install a browser (see Step 0) and Java 25 via winget (see Step 1).
2. Open the project: **File → Open Project** (or **File → Open**) and select the **`task3`** folder.
3. Wait for the dependencies to load and click **Load Maven** in the dialog that appears.
4. Select the JDK: **File → Project Structure → Project Settings → Project → SDK** — select **25 Eclipse Temurin** (installed via winget) or any other **Java 25 JDK**.
5. Run the tests: right-click the required test in `src/test/java/ru/kaelth4s/tests/test` → **Run '...'**, or run all tests at once by right-clicking the `src/test/java` folder → **Run 'Tests in 'java''**.
