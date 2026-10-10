[Русский](README_RU.md) | **English**

# API autotests for jsonplaceholder.typicode.com

## Solution overview

A set of automated API tests for the public REST stand **JSONPlaceholder** (<https://jsonplaceholder.typicode.com/>), written in **Java** with **Rest Assured** as the HTTP client and **Aquality Selenium** as the single logging entry point.

A single integration test `ApiTest` covers six steps of the assignment and verifies the key scenarios for the `/posts` and `/users` resources:

| Step | Method and endpoint | What is verified |
|---|---|---|
| 1 | `GET /posts` | status 200, body is JSON, `id` values are in ascending order |
| 2 | `GET /posts/99` | status 200, `userId = 10`, `id = 99`, `title` and `body` are not empty |
| 3 | `GET /posts/150` | status 404, body is an empty JSON object |
| 4 | `POST /posts` | status 201, `userId`, `title`, `body` match the ones sent, `id` is generated |
| 5 | `GET /users` | status 200, body is JSON, the data of the user with `id = 5` matches the expected values |
| 6 | `GET /users/5` | status 200, the data matches what was received in step 5 |

### How it works

- **Abstract `ApiClient`** — a base class with no public methods; `get` / `post` are declared `protected`, so only client subclasses can send HTTP requests, and tests cannot. It supports the `get(spec, path, pathParams)` overload for substituting path parameters via a `Map`.
- **Universal `ApiResponse<T>` wrapper** — a generic record with `statusCode`, `contentType` and a parsed `body`; the test gets both the HTTP response metadata and the domain model without knowing anything about Rest Assured.
- **API client layer** — `PostClient` and `UserClient` extend `ApiClient` and describe business operations (`getAll`, `getById`, `create`, `getRawById`). The test calls ready-made methods and knows neither endpoints, nor HTTP methods, nor response types.
- **`Endpoint` without duplication** — one base path per resource (`/posts`, `/users`); a concrete item is built by the `byId()` method, which appends the `{id}` placeholder — Rest Assured syntax. The value is substituted through `pathParams` rather than string concatenation.
- **Separation of negative and positive scenarios** — positive methods return a domain model (`PostResponse`, `UserResponse`), negative ones (`getRawById`) return `Map<String, Object>` so that "the body is empty" can be asserted strictly via `isEmpty()`.
- **DTOs as `record`s** — request models (`CreatePostRequest`), response models (`PostResponse`, `UserResponse` and the nested `UserAddress`, `UserAddressGeo`, `UserCompany`); all response models are annotated with `@JsonIgnoreProperties(ignoreUnknown = true)`.
- **DTOs separated by resource** — `tests/dto/api/posts/` for posts, `tests/dto/api/users/` for users; this follows the "one contract — one DTO" principle.
- **Two-layer test data** — the "raw" JSON shape (`TestDataJson`) is separated from the domain model (`TestData`), with assembly in `TestDataManager`; when the structure of `test-data.json` changes, only `TestDataJson` and the mapping in `TestData.from` need updating.
- **Random data generation** — `RandomUtil` based on DataFaker generates `title` and `body` for the POST request; `userId` is taken from `test-data.json`.
- **No hardcoded data** — all values live in `test-data.json` and `environment.json`. Random data (`title`, `body` for POST) is generated in `TestData.from` via `RandomUtil`. Changing any expected value requires no edits to Java code.

### Libraries used

| Library | Version | Purpose |
|---|---|---|
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | HTTP client and DSL for API requests |
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | unified logging environment (wrapper over Log4j2) |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | test framework |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | JSON reading (configs, test data, response deserialization) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | random test data generation |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | logging (transitively via Aquality) |

The project is built with **Apache Maven**.

### Environment

- OS: Windows 10/11 (the instructions assume `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- IDE (optional): IntelliJ IDEA

## Project structure

```text
<repository root>/
├── pom.xml
├── logs/
│   └── test.log
└── src/
    └── test/
        ├── java/ru/kaelth4s/
        │   ├── framework/
        │   │   ├── base/
        │   │   │   └── BaseTest.java
        │   │   ├── client/
        │   │   │   ├── ApiClient.java
        │   │   │   └── ApiResponse.java
        │   │   ├── spec/
        │   │   │   ├── AqualityLoggingFilter.java
        │   │   │   └── Specifications.java
        │   │   └── util/
        │   │       ├── JsonUtil.java
        │   │       └── RandomUtil.java
        │   └── tests/
        │       ├── client/
        │       │   ├── PostClient.java
        │       │   └── UserClient.java
        │       ├── config/
        │       │   ├── EnvironmentDataManager.java
        │       │   ├── SpecificationsManager.java
        │       │   └── TestDataManager.java
        │       ├── dto/
        │       │   ├── EnvironmentData.java
        │       │   ├── TestData.java
        │       │   └── api/
        │       │       ├── posts/
        │       │       │   ├── CreatePostRequest.java
        │       │       │   └── PostResponse.java
        │       │       └── users/
        │       │           ├── UserAddress.java
        │       │           ├── UserAddressGeo.java
        │       │           ├── UserCompany.java
        │       │           └── UserResponse.java
        │       ├── enums/
        │       │   ├── ContentType.java
        │       │   └── Endpoint.java
        │       └── test/
        │           └── ApiTest.java
        └── resources/
            ├── environment.json
            ├── log4j2.properties
            └── test-data.json
```

## How to run

> All commands are executed in **cmd**. After installing Java/Maven, restart the cmd window so that `PATH` is refreshed.

### Step 1. Install Java 25 (Eclipse Temurin)

If it is not installed yet. `winget` is available directly in cmd:

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
2. Unpack it into `%USERPROFILE%\maven` (for example, `C:\Users\<UserName>\maven`; create this folder if it does not exist).
3. In cmd run:

   ```bat
   setx MAVEN_HOME "%USERPROFILE%\maven\apache-maven-3.9.16"
   setx Path "%Path%;%USERPROFILE%\maven\apache-maven-3.9.16\bin"
   ```

4. Restart cmd and check:

   ```bat
   mvn -version
   ```

### Running from the console

After completing the preparation steps:

```bat
cd /d <path to the repository>
mvn clean test
```

Maven downloads the dependencies and runs the test. The test stand is public, so no additional configuration is required.

**Logs** are written to `logs/test.log` and duplicated to the console. The file is overwritten on every run.

### Running from IntelliJ IDEA

1. Install Java 25 via winget (see Step 1).
2. Open the project: **File → Open** and select the repository root.
3. Wait for the dependencies to load and click **Load Maven** in the window that appears.
4. Select the JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Open `ApiTest` in `src/test/java/ru/kaelth4s/tests/test/` → right-click → **Run 'ApiTest'**.

## Configuration

### `environment.json`

URL of the stand under test:

```json
{
  "baseUrl": "https://jsonplaceholder.typicode.com"
}
```

### `test-data.json`

Test data split by test step:

- `expectedPostGetRequest` — the expected `id` and `userId` for step 2 (`GET /posts/99`);
- `badPostGetRequestId` — a non-existent post `id` for step 3 (`GET /posts/150` → 404);
- `creatingPostRequest.userId` — the `userId` for step 4 (`POST /posts`); `title` and `body` are generated dynamically via `RandomUtil` at the moment the data is loaded;
- `expectedUserGetRequest` — the full expected user object with `id = 5` for steps 5 and 6.

```json
{
  "expectedPostGetRequest": {
    "id": 99,
    "userId": 10
  },
  "badPostGetRequestId": 150,
  "creatingPostRequest": {
    "userId": 1
  },
  "expectedUserGetRequest": {
    "id": 5,
    "name": "Chelsey Dietrich",
    "username": "Kamren",
    "email": "Lucio_Hettinger@annie.ca",
    "address": {
      "street": "Skiles Walks",
      "suite": "Suite 351",
      "city": "Roscoeview",
      "zipcode": "33263",
      "geo": {
        "lat": "-31.8129",
        "lng": "62.5342"
      }
    },
    "phone": "(254)954-1289",
    "website": "demarco.info",
    "company": {
      "name": "Keebler LLC",
      "catchPhrase": "User-centric fault-tolerant solution",
      "bs": "revolutionize end-to-end systems"
    }
  }
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
