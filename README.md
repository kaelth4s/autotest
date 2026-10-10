[Русский](README_RU.md) | **English**

# UI + API test for Yandex Disk

## Solution overview

An automated test for the UI and API parts of **Yandex Disk** (<https://disk.yandex.ru/>), written in **Java** with **Aquality Selenium** (UI layer, unified logging) and **Rest Assured** (API layer). A single integration test, `YandexDiskTest`, covers all 15 steps of the task, alternately verifying changes through the API and through the interface.

Authorization requires no separate run: if there is no saved session in `auth-session/`, the test logs in itself via `AuthPrepare` (login/password from `.env`) and saves the debug token and cookies.

| Class / entry point | Purpose |
|---|---|
| `ru.kaelth4s.tests.test.YandexDiskTest` | the 15-step test case, run by JUnit |
| `ru.kaelth4s.tests.auth.AuthPrepare` | automatic login to Yandex ID and session saving; called from step 2, it has no separate `main` |

| Step | Type | What the test does | Implementation |
|---|---|---|---|
| 1 | UI | Open <https://disk.yandex.ru/> and make sure the user is not authorized | `getBrowser().goTo(baseUrl)` → `UnauthorizedMainPage.state().waitForExist()` |
| 2 | UI | Log in | if `auth-session/cookies.json` is missing — `AuthPrepare.auth()`; then `robotsUrl` → `CookieManager.load()` → `baseUrl` → `MainPage.state().waitForDisplayed() && waitForListingsLoaded()` |
| 3 | API | Add a text file with random text, status **201** | `RandomUtil.writeRandomTextFile()` (file in `test-files/`) → `ResourcesUploadClient.getUploadFileLink` (`GET /resources/upload?path=...&overwrite=true`) → `uploadFile` (PUT to the issued `href`) |
| 4 | UI | Make sure the file appeared on the disk without a page refresh, and that the name/extension match | `mainPage.waitForNewListing(старыйСчётчик)` + `getLastFileListing()` |
| 5 | UI | Open the document in a new tab and verify the contents | `mainPage.openLastFileListingInNewTab()` → `DocumentFilePage.getDocumentText()` vs `FileUtil.readFile` |
| 6 | API | Change the file extension to `jpg`, status **201** | `TabUtil.closeCurrent()` → `ResourcesMoveClient.changeFileExtension` (POST `/resources/move`) |
| 7 | UI | Make sure, without a page refresh, that the extension changed | `mainPage.waitForListingChanges(старыйСписок)` + checking `extension()` |
| 8 | API | Upload an image with the same file name and overwrite, status **201** | `getUploadFileLink` (for `overwrite=true`) → `ResourcesUploadClient.uploadResourceFile` (PUT to `href`) |
| 9 | UI | Open the image and make sure it matches the uploaded one, then close it | `TabUtil.refreshTab()` → `waitForListingsLoaded()` → `mainPage.openLastFileListing()` → `MediaFilePage` → MD5 comparison (`Md5Util` vs `ResourceClient.getMetadata`) |
| 10 | UI | Drag the image to the trash | `mainPage.moveLastFileListingToTrash()` → `ActionUtil.dragAndDrop` + `DeleteFileForm.deleteFile()` |
| 11 | API | Make sure the file is in the trash | `TrashUtil.waitForItemByOriginPath` (polling GET `/trash/resources?sort=-deleted`), verifying `origin_path = disk:<путь>` (`PathPrefix.DISK`) |
| 12 | API | Restore the file from the trash, status **201** | `TrashResourcesRestoreClient.restoreFileFromTrash` (PUT `/trash/resources/restore`) |
| 13 | UI | Make sure, without a page refresh, that the image is back on the disk | `mainPage.waitForListingChanges(старыйСписок)` |
| 14 | API | Delete the image permanently, status **204** | `ResourceClient.deleteFilePermanently` (DELETE `/resources?permanently=true`) |
| 15 | UI | Make sure the image is absent from the disk | `mainPage.moveLastFileListingToTrash()` → `mainPage.waitForFailedNotification()` |

> **About step 9.** After the file is overwritten with an image, the UI keeps showing the old preview, so `TabUtil.refreshTab()` is performed before opening it.
>
> **About step 15.** A file deleted permanently "bypassing the trash" remains in the UI as a card until the page is reloaded, and the interface receives no deletion event. There is no need to refresh the tab: the test drags the stale card to the trash again, Disk rejects the operation, and an error snackbar (`Orb-Snackbar_feedback_danger`) appears in the UI, which is exactly what `waitForFailedNotification()` waits for. This is how the deletion is confirmed by the server's reaction rather than by a reload.

### How it works

- **Automatic login with no manual step.** `AuthPrepare.auth()` under `ThreadContext authRun=true` opens `oauth.yandex.ru/authorize?...` (client_id is substituted from `.env`), completes the `PassportPage` form (login, password, "sign in"), waits for user confirmation when SFA appears (`timeoutSFA` from `auth/config.json`), closes Yandex ID service dialogs, reads the debug token via `DebugTokenPage` → `auth-session/access_token.txt`, then goes to Disk and dumps the cookies to `auth-session/cookies.json` (`CookieManager.save`). No repeated login is needed: as long as the session files are in place, step 2 only injects the cookies.
- **Cookie-based UI session.** The cookies are applied on the service page `robotsUrl` (`https://disk.yandex.ru/robots.txt`) because it is served from the same domain and stays on it. If the regular `baseUrl` is opened first, an unauthorized user is redirected to `360.yandex.ru` / `passport.yandex.ru`, the domain changes — and there is nowhere left to set cookies for `disk.yandex.ru`. That is why the order is strictly this: `robotsUrl` → `CookieManager.load()` → `baseUrl`. A cookie the browser rejected (`UnableToSetCookieException`) is logged as a warning and does not fail the test.
- **UI layer on Aquality Selenium.** Pages are subclasses of `Form` with a locator and a name (`MainPage`, `PassportPage`, `DocumentFilePage`, `MediaFilePage`, `UnauthorizedMainPage`), plus a separate modal form `DeleteFileForm`. Elements are obtained only through `getElementFactory()` with a human-readable name, so every action lands in the log automatically.
- **The file list as a DTO.** `FileListing` (`name`, `extension`, `imagePreviewHref`) is assembled by the method `MainPage.from(IButton)`: the name and extension are split at the last dot, and the preview `src` is read through the `Attribute` enum. The test compares **whole list snapshots** rather than individual locators.
- **Checks without a reload.** The methods `waitForNewListing(старыйСчётчик)` and `waitForListingChanges(старыйСписок)` are built on `getConditionalWait().waitFor(...)`: the wait stops as soon as the UI has caught up with the change made through the API. In addition, `waitForListingsLoaded()` waits for the spinner (`Orb-Spinner`) to disappear.
- **Test data is generated per run.** `RandomUtil.writeRandomTextFile()` writes a DataFaker paragraph to `test-files/random-<дата-время>.txt`; the `test-files/` directory is added to `.gitignore`. The image `api/files/random.jpg` lives in the resources and is uploaded as is.
- **Image verification by MD5.** The local MD5 is computed by `Md5Util` over the bytes of the `api/files/random.jpg` resource, and the server-side one is taken from `GET /resources` (`md5` in `GetResourceResponse`). Comparing the hashes proves that the file on Disk is exactly the uploaded one and not a placeholder preview image.
- **The API layer is separated from the tests.** The abstract `ApiClient` keeps its HTTP wrappers as `protected` methods (`getWithQuery`, `postWithQuery`, `putWithQuery`, `deleteWithQuery`, `putBinary`) and configures Jackson so that unknown response fields do not break deserialization. The test sees only the domain methods of the clients (`getUploadFileLink`, `uploadFile`, `uploadResourceFile`, `changeFileExtension`, `getMetadata`, `deleteFilePermanently`, `getAllTrashSorted`, `restoreFileFromTrash`). The shared response is the generic record `ApiResponse<T>` with `statusCode`, `contentType` and a parsed body.
- **Waiting for an asynchronous API.** A file appears in the trash not instantly, so `TrashUtil.waitForItemByOriginPath` does not make a single request but polls `getConditionalWait()` until an item with the expected `origin_path` appears, and only then returns it to the test.
- **API authorization at the specification level.** `Specifications.disk(baseUrl, token)` sets the base URI, `Content-Type`/`Accept` from `ContentType`, the `AqualityLoggingFilter` logging filter and the `Authorization: OAuth <token>` header; the token is injected from `DebugTokenManager` in `SpecificationsManager`.

### Libraries used

| Library | Version | Purpose |
|---|---|---|
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | UI automation (a wrapper over Selenium WebDriver) and unified logging |
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | HTTP client and DSL for API requests |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | test framework |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | reading JSON (configs, test data, API response deserialization) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | generating the random text of the file |
| [java-dotenv](https://github.com/cdimascio/dotenv-java) | 5.2.2 | reading `.env` (client_id, login, password) |
| [Lombok](https://projectlombok.org/) | 1.18.48 | getters for enum constants |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | logging (transitively through Aquality) |

The project is built with **Apache Maven**. Browser drivers do not need to be installed separately: Selenium 4 pulls them in itself (Selenium Manager).

### Environment

- OS: Windows 10/11 (the instructions assume `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Browsers: **Google Chrome** and **Mozilla Firefox** (current versions)
- IDE (optional): IntelliJ IDEA

## Project structure

```text
<корень репозитория>/
├── .env                          # client_id, yandex_login, yandex_password — в git не попадает
├── .env.example                  # шаблон .env
├── pom.xml
├── README.md
├── README_RU.md
├── auth-session/                 # создаётся AuthPrepare, всё содержимое в .gitignore
│   ├── .gitignore
│   ├── access_token.txt
│   └── cookies.json
├── logs/
│   ├── test.log
│   └── auth-prepare.log
├── test-files/                   # сгенерированные текстовые файлы прогонов, в git не попадает
└── src/
    ├── main/java/ru/kaelth4s/
    │   ├── framework/
    │   │   ├── ui/
    │   │   │   ├── enums/Attribute.java
    │   │   │   └── util/
    │   │   │       ├── ActionUtil.java       # drag-and-drop
    │   │   │       ├── TabUtil.java          # вкладки (история фокуса)
    │   │   │       └── WaitUtil.java         # ожидание нового окна
    │   │   └── util/
    │   │       ├── FileUtil.java             # чтение/запись файлов и ресурсов
    │   │       └── JsonUtil.java             # чтение/запись JSON
    │   └── tests/
    │       ├── auth/
    │       │   ├── config/AuthConfigManager.java
    │       │   ├── dto/AuthConfigData.java
    │       │   └── page/DebugTokenPage.java
    │       └── ui/
    │           ├── dto/FileListing.java
    │           ├── form/DeleteFileForm.java
    │           └── page/
    │               ├── DocumentFilePage.java
    │               ├── MainPage.java
    │               ├── MediaFilePage.java
    │               ├── PassportPage.java
    │               └── UnauthorizedMainPage.java
    ├── main/resources/auth/config.json
    └── test/
        ├── java/ru/kaelth4s/
        │   ├── framework/
        │   │   ├── api/client/
        │   │   │   ├── ApiClient.java
        │   │   │   └── ApiResponse.java
        │   │   ├── base/BaseTest.java
        │   │   └── config/CookieManager.java
        │   └── tests/
        │       ├── api/
        │       │   ├── client/
        │       │   │   ├── ResourceClient.java
        │       │   │   ├── ResourcesMoveClient.java
        │       │   │   ├── ResourcesUploadClient.java
        │       │   │   ├── TrashResourceClient.java
        │       │   │   └── TrashResourcesRestoreClient.java
        │       │   ├── config/
        │       │   │   ├── ApiEnvironmentDataManager.java
        │       │   │   ├── ApiTestDataManager.java
        │       │   │   └── SpecificationsManager.java
        │       │   ├── dto/
        │       │   │   ├── ApiTestData.java
        │       │   │   ├── resources/
        │       │   │   │   ├── ApiEnvironmentData.java
        │       │   │   │   ├── GetResourceResponse.java
        │       │   │   │   ├── move/PostResourcesMoveResponse.java
        │       │   │   │   └── upload/GetResourcesUploadResponse.java
        │       │   │   └── trash/resources/
        │       │   │       ├── GetTrashResourcesResponse.java
        │       │   │       ├── TrashResourcesEmbedded.java
        │       │   │       ├── TrashResourcesEmbeddedItem.java
        │       │   │       └── restore/PutTrashResourcesRestoreResponse.java
        │       │   ├── enums/
        │       │   │   ├── ContentType.java
        │       │   │   ├── Endpoint.java
        │       │   │   ├── Header.java
        │       │   │   ├── PathPrefix.java
        │       │   │   ├── Query.java
        │       │   │   └── SortAttribute.java
        │       │   ├── spec/
        │       │   │   ├── AqualityLoggingFilter.java
        │       │   │   └── Specifications.java
        │       │   └── util/TrashUtil.java
        │       ├── auth/
        │       │   ├── AuthPrepare.java
        │       │   ├── config/
        │       │   │   ├── AuthEnvironmentDataManager.java
        │       │   │   └── DebugTokenManager.java
        │       │   ├── dto/
        │       │   │   ├── AuthEnvironmentData.java
        │       │   │   └── CookieData.java
        │       │   └── enums/AuthEnvironmentKey.java
        │       ├── test/YandexDiskTest.java
        │       ├── ui/
        │       │   ├── config/UiEnvironmentDataManager.java
        │       │   └── dto/UiEnvironmentData.java
        │       └── util/
        │           ├── DiskPathUtil.java
        │           ├── Md5Util.java
        │           └── RandomUtil.java
        └── resources/
            ├── api/
            │   ├── environment.json
            │   ├── test-data.json
            │   └── files/random.jpg
            ├── auth/environment.json
            ├── ui/environment.json
            ├── log4j2.properties
            ├── settings.chrome.json
            └── settings.firefox.json
```

## How to run

> All commands are executed in **cmd**. After installing Java/Maven, restart the cmd window so that `PATH` is refreshed.

### Step 1. Install Java 25 (Eclipse Temurin)

If it is not installed. `winget` is available directly in cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Restart cmd and check the installation:

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
2. Unpack it into `%USERPROFILE%\maven` (for example, `C:\Users\<ИмяПользователя>\maven`; create this folder if it does not exist).
3. In cmd, run:

   ```bat
   setx MAVEN_HOME "%USERPROFILE%\maven\apache-maven-3.9.16"
   setx Path "%Path%;%USERPROFILE%\maven\apache-maven-3.9.16\bin"
   ```

4. Restart cmd and check:

   ```bat
   mvn -version
   ```

### Step 3. Fill in `.env`

In the repository root, create a `.env` file based on `.env.example`:

```dotenv
client_id=<client_id вашего OAuth-приложения Яндекс ID>
yandex_login=<логин Яндекс ID>
yandex_password=<пароль>
```

- `client_id` is substituted into the authorization link in `auth/environment.json` (the file contains the `{client_id}` placeholder).
- `yandex_login` and `yandex_password` are used by the `PassportPage` form.
- The values are read from `.env` (java-dotenv), and if a key is not there — from the process environment variables. If a key is found nowhere, the test fails with `Cannot resolve environment key ...`.
- `.env` is added to `.gitignore` — secrets never get into the repository.

### Step 4. Run the test

```bat
cd /d <путь к репозиторию>
mvn clean test -Dprofile=chrome
```

Instead of `chrome` you can specify `firefox` — the test runs in both browsers without any code changes.

**Authorization.** No separate run is needed: at step 2 the test sees that `auth-session/cookies.json` is missing and calls `AuthPrepare.auth()` itself. The browser starts in incognito mode, and the login is performed with the login and password from `.env`. If the account has two-factor authentication (SFA) enabled, the browser window waits for confirmation — **make sure you confirm the login** within `timeoutSFA` seconds (60 by default), otherwise the test fails with `You need to pass 2FA for test case`. After a successful login, `auth-session/access_token.txt` (for API requests) and `auth-session/cookies.json` (for the UI) appear, and subsequent runs will not log in again.

> The token has a limited lifetime. If the API steps started failing on authorization, delete the contents of `auth-session/` — the test will authorize again. Note: only the presence of `cookies.json` is checked, so with a live cookie but a missing or expired `access_token.txt` the automatic login will not trigger — delete both files.

**Logs** are written to `logs/test.log` and duplicated to the console: API requests and responses, UI actions, waits. The file is overwritten on every run. The automatic login log goes separately — to `logs/auth-prepare.log`, so test run logs are not wiped.

### Running through IntelliJ IDEA

1. Install Java 25 via winget (see Step 1).
2. Open the project: **File → Open** and select the repository root.
3. Wait for the dependencies to load and click **Load Maven** in the suggested window.
4. Select the JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Create `.env` (see Step 3) so that the `AuthEnvironmentData` class can assemble the authorization link.
6. Test: open `YandexDiskTest` (`src/test/java/ru/kaelth4s/tests/test/`) → right-click → **Modify Run Configuration…** → VM options `-Dprofile=chrome` (or `-Dprofile=firefox`) → **Run**.

## Configuration

### `.env`

Secrets and the OAuth application identifier (not committed to git):

```dotenv
client_id=
yandex_login=
yandex_password=
```

### `api/environment.json`

The base URL of the Yandex Disk API:

```json
{
  "baseUrl": "https://cloud-api.yandex.net/v1/disk"
}
```

### `ui/environment.json`

The URL of the stand and the service page on which cookies are set before returning to Disk:

```json
{
  "baseUrl": "https://disk.yandex.ru",
  "robotsUrl": "https://disk.yandex.ru/robots.txt"
}
```

> **Important.** The cookies are applied specifically on `robotsUrl` (`https://disk.yandex.ru/robots.txt`), because it is served from the same `disk.yandex.ru` domain and stays on it. If the regular `baseUrl` is opened first, an unauthorized user is redirected to `360.yandex.ru` or `passport.yandex.ru`, the domain changes — and there is nowhere left to set cookies for `disk.yandex.ru`. That is why the order is strictly this: `robotsUrl` → `CookieManager.load()` → `baseUrl`.

### `auth/environment.json`

The authorization link of the Yandex ID OAuth application; `{client_id}` is substituted from `.env`:

```json
{
  "baseUrl": "https://oauth.yandex.ru/authorize?response_type=token&client_id={client_id}"
}
```

### `auth/config.json`

Automatic login timeouts (it lives in `src/main/resources` and is read by `AuthConfigManager`):

```json
{
  "timeoutSFA": 60,
  "timeoutAuthFormLoad": 5
}
```

`timeoutSFA` is how many seconds to wait for two-factor authentication confirmation, and `timeoutAuthFormLoad` is the wait for the Yandex ID form to load.

### `api/test-data.json`

Test step data: the file path on Disk, the new extension and the path to the image in the resources.

```json
{
  "uploadTextFileDiskPath": "/random.txt",
  "changedFileExtension": "jpg",
  "uploadImageFileResourcePath": "api/files/random.jpg"
}
```

The text of the text file is not stored in advance: before uploading it is generated by DataFaker into `test-files/random-<дата-время>.txt` (`RandomUtil.writeRandomTextFile`), while `random.jpg` is a small binary file in the resources that is uploaded as is.

### `settings.<profile>.json`

Aquality Selenium profiles. The profile is selected by the system property `profile` (`-Dprofile=...`), and the file name is `settings.<profile>.json`:

| Profile | File | Browser | Specifics |
|---|---|---|---|
| `chrome` | `settings.chrome.json` | Chrome | `--lang=en`, `--incognito`, locale `en` |
| `firefox` | `settings.firefox.json` | Firefox | locale `en`, strict `SameSite` restrictions for cookies disabled |

Otherwise the profiles are identical: a local driver (`isRemote=false`), element highlighting, a 35 s condition wait, a 20 s page load, a 300 ms polling interval, 2 action retries, element cache disabled.

Authorization and the test use the same profile: the test logs in by itself in the browser launched with `-Dprofile=chrome|firefox`, so there are no separate `auth-*` profiles any more.

### `log4j2.properties`

A unified log format for the console and the files. `RoutingAppender` routes entries to files: when the `ThreadContext` `authRun=true` is set (this is done by `AuthPrepare`), the logs go to `logs/auth-prepare.log`, otherwise — to `logs/test.log`.

```properties
status=warn
name=PropertiesConfig
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
appender.authFile.type=File
appender.authFile.name=AuthFileAppender
appender.authFile.fileName=logs/auth-prepare.log
appender.authFile.append=false
appender.authFile.layout.type=PatternLayout
appender.authFile.layout.pattern=%d{yyyy-MM-dd HH:mm:ss} %-5level - %msg%n
appender.routing.type=Routing
appender.routing.name=RoutingAppender
appender.routing.routes.type=Routes
appender.routing.routes.pattern=$${ctx:authRun}
appender.routing.routes.auth.type=Route
appender.routing.routes.auth.key=true
appender.routing.routes.auth.ref=AuthFileAppender
appender.routing.routes.default.type=Route
appender.routing.routes.default.ref=FileAppender
rootLogger.level=info
rootLogger.appenderRef.routing.ref=RoutingAppender
rootLogger.appenderRef.console.ref=ConsoleAppender
```
