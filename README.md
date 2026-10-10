[Русский](README_RU.md) | **English**

# Steam Store UI Autotests

## Solution Overview

A set of automated UI tests for the **Steam** store (<https://store.steampowered.com/>), written in **Java**. The project verifies the key scenarios of the store:

| Test | What it verifies |
|---|---|
| `StoreAboutTest` | navigation to the **About** page, that the "currently playing" value is lower than the "online" value, and a correct return to the store page |
| `TopSellersTest` | navigation to the **Top Sellers** section, applying checkbox filters (OS, category, tags), the result counter matching the actual number of games in the list, and the data on the page of a selected game being correct |
| `MarketTest` | **Community Market**: opening the advanced search form, filters by game / hero / rarity, search by name, the list refreshing when a filter is cleared, and the item page matching the data from the previous page |

How it is built:

- **Page Object** pattern — every Steam page is described by a separate class (`page/`), recurring elements are extracted into components (`page/component/`);
- the `BaseComponent` / `BasePage` base classes encapsulate element search, clicks, text input and page-open checks;
- a **browser factory** (`browser/BrowserFactory`, with implementations for Chrome and Firefox) — the browser is selected in the configuration;
- **explicit waits** via `WebDriverWait` (`service/WaitManager`), the timeout is configurable in the config;
- the run configuration (`config.json`) and test data (`market-test.json`, `top-sellers-test.json`) are stored in **JSON** and mapped to DTOs via Jackson (`ConfigDataManager`, `TestDataManager`);
- the WebDriver is created as a singleton (`driver/DriverManager`), and the browser driver is downloaded automatically via WebDriverManager.

### Libraries Used

| Library | Version | Purpose |
|---|---|---|
| [Selenium WebDriver](https://www.selenium.dev/) | 4.47.0 | browser control |
| [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) | 6.3.4 | automatic webdriver download |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | test framework |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | reading JSON (configs and test data) |

The project is built with **Apache Maven**.

### Environment

- OS: Windows 10/11 (the instructions assume `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Browser: **Google Chrome** (default; switching to Firefox is supported (without parameters) — changed in `src/main/resources/config.json`)
- IDE (optional): IntelliJ IDEA

## Project Structure

```text
<repository root>/
├── pom.xml                                  # Maven: dependencies and build settings
└── src/
   ├── main/
   │   ├── java/ru/kaelth4s/
   │   │   ├── BaseComponent.java           # base operations: search, waits, clicks, input
   │   │   ├── BasePage.java                # base page (isPageOpened check)
   │   │   ├── browser/                     # browser factories: Chrome, Firefox
   │   │   ├── driver/DriverManager.java    # singleton managing the WebDriver
   │   │   ├── dto/                         # configuration DTOs (ConfigData, ChromeConfig, etc.)
   │   │   ├── object/Game.java             # game model
   │   │   ├── page/                        # Page Objects of the Steam pages
   │   │   │   └── component/               # shared page components (Header, filters)
   │   │   ├── service/                     # WaitManager, ConfigDataManager, ChromeOptionsManager
   │   │   └── util/                        # JsonUtils, custom ExpectedCondition implementations
   │   └── resources/
   │       └── config.json                  # settings: browser, URL, timeouts, Chrome options
   └── test/
      ├── java/ru/kaelth4s/
      │   ├── TestDataManager.java         # loading test data from JSON
      │   ├── base/BaseTest.java           # base test class (setUp/tearDown)
      │   ├── dto/                         # test data DTOs (filters, etc.)
      │   └── test/                        # StoreAboutTest, TopSellersTest, MarketTest
      └── resources/test-data/             # market-test.json, top-sellers-test.json
```

## How to Run

> All commands are executed in **cmd**. After installing Java/Maven, restart the cmd window so that `PATH` is refreshed.

### Preparation (common steps)

**Step 0. Install Google Chrome** (the default browser for the tests) if it is not installed:
<https://www.google.com/chrome/>

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

1. Download the archive manually from your browser: <https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip>
2. Unpack it into `%USERPROFILE%\maven` (for example, `C:\Users\<UserName>\maven`; create this folder if it does not exist).
3. Run the following in cmd:

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

After completing the preparation steps (Chrome, Java 25, Maven):

```bat
cd /d <path to the repository>
mvn clean test
```

Maven downloads all dependencies itself and runs all three tests (`StoreAboutTest`, `TopSellersTest`, `MarketTest`). A Chrome window opens — do not close it manually while the tests are running.

Running one specific test:

```bat
mvn test -Dtest=MarketTest
```

Result reports appear in `task2\target\surefire-reports\`.

---

### Option 2. Running from IntelliJ IDEA

1. Install Chrome (see Step 0) and, if necessary, Java 25 via winget (see Step 1).
2. Open the project: **File → Open Project** (or **File → Open**) and select the **`task2`** folder.
3. Wait for the dependencies to load and click **Load Maven** in the dialog that appears.
4. Select the JDK: **File → Project Structure → Project Settings → Project → SDK** — choose **25 Eclipse Temurin** (the one downloaded via winget, if it was not present) or any other **Java 25 JDK**.
5. Run the tests: right-click the test you need in `src/test/java/ru/kaelth4s/test` → **Run '...'**, or run all tests at once by right-clicking `src/test/java` → **Run 'Tests in 'java''**.
