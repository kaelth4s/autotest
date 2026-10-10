# API-автотесты jsonplaceholder.typicode.com

## Описание решения

Набор автоматизированных API-тестов для публичного REST-стенда **JSONPlaceholder** (<https://jsonplaceholder.typicode.com/>), написанный на **Java** с использованием **Rest Assured** в качестве HTTP-клиента и **Aquality Selenium** для единой точки логирования.

Один интеграционный тест `ApiTest` покрывает шесть шагов из задания и проверяет ключевые сценарии работы с ресурсами `/posts` и `/users`:

| Шаг | Метод и эндпоинт | Что проверяется |
|---|---|---|
| 1 | `GET /posts` | код 200, тело — JSON, `id` идут по возрастанию |
| 2 | `GET /posts/99` | код 200, `userId = 10`, `id = 99`, `title` и `body` не пусты |
| 3 | `GET /posts/150` | код 404, тело — пустой JSON-объект |
| 4 | `POST /posts` | код 201, `userId`, `title`, `body` совпадают с отправленными, `id` сгенерирован |
| 5 | `GET /users` | код 200, тело — JSON, данные пользователя с `id = 5` совпадают с ожидаемыми |
| 6 | `GET /users/5` | код 200, данные совпадают с полученными на шаге 5 |

### Как устроено

- **Абстрактный `ApiClient`** — базовый класс без публичных методов; `get` / `post` объявлены `protected`, поэтому доступ к отправке HTTP-запросов есть только у наследников-клиентов, а из тестов — нет. Поддерживается перегрузка `get(spec, path, pathParams)` для подстановки path-параметров через `Map`.
- **Универсальная обёртка `ApiResponse<T>`** — generic-record с `statusCode`, `contentType` и распарсенным `body`; тест получает и метаданные HTTP-ответа, и доменную модель, не зная о Rest Assured.
- **Слой клиентов API** — `PostClient` и `UserClient` наследуются от `ApiClient` и описывают бизнес-операции (`getAll`, `getById`, `create`, `getRawById`). Тест вызывает готовые методы и не знает ни эндпоинтов, ни HTTP-методов, ни типов ответов.
- **`Endpoint` без дублирования** — один базовый путь на ресурс (`/posts`, `/users`); конкретный элемент строится методом `byId()`, который добавляет плейсхолдер `{id}` — синтаксис Rest Assured. Значение подставляется через `pathParams`, а не конкатенацией строк.
- **Разделение негативного и позитивного сценариев** — позитивные методы возвращают доменную модель (`PostResponse`, `UserResponse`), негативные (`getRawById`) — `Map<String, Object>`, чтобы строго проверить «тело пусто» через `isEmpty()`.
- **DTO как `record`** — request-модели (`CreatePostRequest`), response-модели (`PostResponse`, `UserResponse` и вложенные `UserAddress`, `UserAddressGeo`, `UserCompany`), все response-модели помечены `@JsonIgnoreProperties(ignoreUnknown = true)`.
- **Разделение DTO по ресурсам** — `tests/dto/api/posts/` для постов, `tests/dto/api/users/` для пользователей; это соответствует принципу «один контракт — один DTO».
- **Двухслойные тестовые данные** — «сырая» форма JSON (`TestDataJson`) отделена от доменной модели (`TestData`), сборка — в `TestDataManager`; при изменении структуры `test-data.json` правится только `TestDataJson` и маппинг в `TestData.from`.
- **Генерация случайных данных** — `RandomUtil` на базе DataFaker генерирует `title` и `body` для POST-запроса; `userId` берётся из `test-data.json`.
- **Данные не захардкожены** — все значения живут в `test-data.json` и `environment.json`. Рандомные данные (`title`, `body` для POST) генерируются в `TestData.from` через `RandomUtil`. Изменение любого ожидаемого значения не требует правки Java-кода.

### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | HTTP-клиент и DSL для API-запросов |
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | единая среда логирования (обёртка над Log4j2) |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги, тест-данные, десериализация ответов) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | генерация случайных тестовых данных |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | логирование (транзитивно через Aquality) |

Сборка проекта — **Apache Maven**.

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- IDE (опционально): IntelliJ IDEA

## Структура проекта

```text
<корень репозитория>/
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

### Запуск через консоль

После выполнения подготовительных шагов:

```bat
cd /d <путь к репозиторию>
mvn clean test
```

Maven скачает зависимости и запустит тест. Тестовый стенд — публичный, никаких дополнительных настроек не требуется.

**Логи** пишутся в `logs/test.log` и дублируются в консоль. Файл перезаписывается на каждом запуске.

### Запуск через IntelliJ IDEA

1. Установите Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open** и выберите корень репозитория.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Откройте `ApiTest` в `src/test/java/ru/kaelth4s/tests/test/` → ПКМ → **Run 'ApiTest'**.

## Конфигурация

### `environment.json`

URL тестируемого стенда:

```json
{
  "baseUrl": "https://jsonplaceholder.typicode.com"
}
```

### `test-data.json`

Тестовые данные, разбитые по шагам теста:

- `expectedPostGetRequest` — ожидаемые `id` и `userId` для шага 2 (`GET /posts/99`);
- `badPostGetRequestId` — несуществующий `id` поста для шага 3 (`GET /posts/150` → 404);
- `creatingPostRequest.userId` — `userId` для шага 4 (`POST /posts`); `title` и `body` генерируются динамически через `RandomUtil` в момент загрузки данных;
- `expectedUserGetRequest` — полный ожидаемый объект пользователя с `id = 5` для шагов 5 и 6.

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
