[Русский](README_RU.md) | **English**

# UI autotests for userinyerface.com

## Solution overview

A suite of automated UI tests for the **User In Yer Face** game site (<https://userinyerface.com/>), written in **Java** using the corporate **Aquality Selenium** framework. The project covers the key scenarios of completing the quest:

| Test | What it verifies |
|---|---|
| `CardsTest` | full completion of three cards: entering email/password/domain, accepting the terms, selecting random interests, uploading an avatar, moving to the final card |
| `CookiesTest` | accepting the cookie banner and its disappearance |
| `HelpFormTest` | hiding the help form via the "Send to bottom" button |
| `TimerTest` | the timer starts from the expected value (`00:00:00`) |

### How it works

- **Aquality Selenium as the corporate environment** — the browser, elements and waits are handled through `AqualityServices`; configuration lives in `settings.*.json`, and profile-based runs are supported (`-Dprofile=chrome` / `-Dprofile=firefox`);
- **Page Object pattern** — every card and form is described by its own class extending `Form` (`LoginCardPage`, `InterestsCardPage`, `PersonalDetailsCardPage`, `CookiesForm`, `HelpForm`); elements shared by all cards are moved to `CardPage`;
- **Element Factory** — all elements are created via `getElementFactory()`, and the code works through the `ITextBox`, `IButton`, `ICheckBox`, `ILabel`, `ILink` interfaces;
- **Explicit waits with Aquality** — page opening and form closing are verified with `state().waitForDisplayed()` / `waitForNotDisplayed()`;
- **Element states** — `ElementState.EXISTS_IN_ANY_STATE` is used for hidden checkboxes and lists;
- **Handling custom checkboxes** — the click targets the visible `<span class="checkbox__box">`, while the state is read from the real `<input>` via `isChecked()`;
- **JSON configuration** — `environment.json` (test stand URL), `test-data.json` (test data), `settings.chrome.json` / `settings.firefox.json` (Aquality settings); mapped into record DTOs with Jackson;
- **DTO separation** — the "raw" JSON data (`CardsTestDataJson`, `TimerTestDataJson`, `TestDataJson`) is separated from the domain models (`CardsTestData`, `TimerTestData`) assembled in `TestDataManager`;
- **Random data generation** — `RandomUtil` built on DataFaker generates an email without a TLD, a password matching the game requirements (an uppercase letter, a digit, a Cyrillic character, a letter from the email), and a random national domain from the dropdown;
- **File upload without the native dialog** — `WebUtil` + `JsUtil` + `resources/js/upload_file.js`: the script intercepts `HTMLInputElement.prototype.click`, prevents the system file picker from opening and injects the image via `DataTransfer` with a `change` event;
- **Unified access to classpath resources** — `FileUtil` reads both text resources (`readResource`) and binary ones as Base64 (`readResourceAsBase64`); the image and the JS script live in the classpath and are read the same way;
- **Type-safe attributes and scripts** — the `Attribute` enum (`ID`, `CLASS`) instead of string literals, and the `JsScript` enum (`UPLOAD_FILE`) for JS paths;
- **Assertions** — AssertJ (`assertThat(...).as(...).isTrue()`) with meaningful descriptions;
- **Logging** — Log4j2, to the console and to `logs/test.log` (the file is overwritten on every run via `appender.file.append=false`).

### Libraries used

| Library | Version | Purpose |
|---|---|---|
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | corporate web automation environment (a wrapper over Selenium) |
| [Selenium WebDriver](https://www.selenium.dev/) | 4.48.0 | browser control (transitively through Aquality) |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | testing framework |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | JSON parsing (configs and test data) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | random test data generation |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | logging (transitively through Aquality) |

The project is built with **Apache Maven**.

### Environment

- OS: Windows 10/11 (the instructions assume `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Browser: **Google Chrome** (default) or **Mozilla Firefox** — selected by the profile at runtime
- IDE (optional): IntelliJ IDEA

## Project structure

```text
<repository root>/
├── pom.xml
├── logs/
│   └── test.log
└── src/
    ├── main/
    │   ├── java/ru/kaelth4s/
    │   │   ├── framework/
    │   │   │   ├── enums/
    │   │   │   │   ├── Attribute.java
    │   │   │   │   └── JsScript.java
    │   │   │   └── util/
    │   │   │       ├── FileUtil.java
    │   │   │       ├── JsUtil.java
    │   │   │       └── WebUtil.java
    │   │   └── tests/
    │   │       ├── form/
    │   │       │   ├── CookiesForm.java
    │   │       │   └── HelpForm.java
    │   │       └── page/
    │   │           ├── MainPage.java
    │   │           ├── CardPage.java
    │   │           └── card/
    │   │               ├── LoginCardPage.java
    │   │               ├── InterestsCardPage.java
    │   │               └── PersonalDetailsCardPage.java
    │   └── resources/
    │       └── js/
    │           └── upload_file.js
    └── test/
        ├── java/ru/kaelth4s/
        │   ├── framework/
        │   │   ├── base/BaseTest.java
        │   │   └── util/
        │   │       ├── JsonUtil.java
        │   │       └── RandomUtil.java
        │   └── tests/
        │       ├── config/
        │       │   ├── EnvironmentDataManager.java
        │       │   └── TestDataManager.java
        │       ├── dto/
        │       │   ├── CardsTestData.java
        │       │   ├── EnvironmentData.java
        │       │   ├── TestData.java
        │       │   ├── TimerTestData.java
        │       │   └── json/
        │       │       ├── CardsTestDataJson.java
        │       │       ├── TestDataJson.java
        │       │       └── TimerTestDataJson.java
        │       └── test/
        │           ├── CardsTest.java
        │           ├── CookiesTest.java
        │           ├── HelpFormTest.java
        │           └── TimerTest.java
        └── resources/
            ├── environment.json
            ├── log4j2.properties
            ├── settings.chrome.json
            ├── settings.firefox.json
            ├── test-data.json
            └── test-files/
                └── BTNHeroBloodElfPrince.jpg
```

## How to run

> All commands are executed in **cmd**. After installing Java/Maven, restart the cmd window so that `PATH` is refreshed.

### Preparation (common steps)

#### Step 0. Install a browser

Download and install one of the supported browsers:

- **Google Chrome**: <https://www.google.com/chrome/>
- **Mozilla Firefox**: <https://www.firefox.com/thanks/>

The driver is downloaded automatically by Selenium Manager (bundled with Selenium 4.48.0).

#### Step 1. Install Java 25 (Eclipse Temurin)

If it is not installed yet. `winget` is available directly in cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Restart cmd and verify the installation:

```bat
java -version
```

#### Step 2. Install Maven 3.9.16

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

### Option 1. Running from the console

After completing the preparation steps (browser, Java 25, Maven):

```bat
cd /d <path to repository>
mvn clean test -Dprofile=chrome
```

Maven downloads the dependencies and runs all four tests. A browser window opens — do not close it manually while the tests are running.

**Running in Firefox:**

```bat
mvn clean test -Dprofile=firefox
```

**Running a single specific test:**

```bat
mvn test -Dtest=CardsTest -Dprofile=chrome
```

**Logs** are written to `logs/test.log` and duplicated to the console. The file is overwritten on every run.

### Browser profiles

The browser is selected through the `profile` variable. Aquality Selenium looks for a file named `settings.<profile>.json` in the classpath:

| Profile | Configuration file |
|---|---|
| `chrome` | `settings.chrome.json` |
| `firefox` | `settings.firefox.json` |

Without `-Dprofile=...` Aquality looks for `settings.json` (without a suffix), which does not exist in the project — so the profile must always be specified.

### Option 2. Running from IntelliJ IDEA

1. Install a browser (see Step 0) and Java 25 via winget (see Step 1).
2. Open the project: **File → Open** and select the repository root.
3. Wait for the dependencies to load and click **Load Maven** in the suggested window.
4. Select the JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Open the test you need in `src/test/java/ru/kaelth4s/tests/test/...` → right-click → **More Run/Debug...** → **Modify Run Configuration** → in the **VM options** field add:

   ```
   -Dprofile=chrome
   ```

   (or `-Dprofile=firefox`)

6. Run the test with `SHIFT+F10` (or the run button in the top right corner).

## Configuration

### `environment.json`

URL of the test stand:

```json
{
  "baseUrl": "https://userinyerface.com/"
}
```

### `test-data.json`

Test data that depends on the scenario:

- `cardsTestData.interestsCount` — how many interests to select on card 2;
- `cardsTestData.imageResourcePath` — path to the avatar file **relative to the classpath** (`test-files/` lives in `src/test/resources/`);
- `timerTestData.expectedTimerStart` — the expected timer start value.

```json
{
  "cardsTestData": {
    "interestsCount": 3,
    "imageResourcePath": "test-files/BTNHeroBloodElfPrince.jpg"
  },
  "timerTestData": {
    "expectedTimerStart": "00:00:00"
  }
}
```

The remaining data (email name, domain, password, national domain) is generated dynamically by `RandomUtil` when the test starts.

### `settings.chrome.json` / `settings.firefox.json`

Aquality Selenium configuration: `browserName`, `isRemote`, timeouts, retry policy, browser options. The mandatory sections (`timeouts`, `retry`, `logger`, `elementCache`) are filled with default values.
