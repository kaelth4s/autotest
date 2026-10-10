**Русский** | [English](README.md)

# UI + API автотест Яндекс.Диска

## Описание решения

Автоматический тест UI- и API-частей **Яндекс.Диска** (<https://disk.yandex.ru/>), написанный на **Java** с использованием **Aquality Selenium** (UI-слой, единое логирование) и **Rest Assured** (API-слой). Один интеграционный тест `YandexDiskTest` покрывает все 15 шагов задания, попеременно проверяя изменения через API и через интерфейс.

Авторизация не требует отдельного запуска: если в `auth-session/` нет сохранённой сессии, тест сам логинится через `AuthPrepare` (логин/пароль из `.env`) и сохраняет debug-токен и cookies.

| Класс / точка входа | Назначение |
|---|---|
| `ru.kaelth4s.tests.test.YandexDiskTest` | тест-кейс из 15 шагов, запускается JUnit'ом |
| `ru.kaelth4s.tests.auth.AuthPrepare` | автологин в Яндекс ID и сохранение сессии; вызывается из шага 2, отдельного `main` у него нет |

| Шаг | Тип | Что делает тест | Реализация |
|---|---|---|---|
| 1 | UI | Открыть <https://disk.yandex.ru/> и убедиться, что пользователь не авторизован | `getBrowser().goTo(baseUrl)` → `UnauthorizedMainPage.state().waitForExist()` |
| 2 | UI | Авторизоваться | если `auth-session/cookies.json` нет — `AuthPrepare.auth()`; затем `robotsUrl` → `CookieManager.load()` → `baseUrl` → `MainPage.state().waitForDisplayed() && waitForListingsLoaded()` |
| 3 | API | Добавить текстовый файл со случайным текстом, код **201** | `RandomUtil.writeRandomTextFile()` (файл в `test-files/`) → `ResourcesUploadClient.getUploadFileLink` (`GET /resources/upload?path=...&overwrite=true`) → `uploadFile` (PUT по выданному `href`) |
| 4 | UI | Убедиться, что файл появился на диске без обновления страницы, имя/расширение совпадают | `mainPage.waitForNewListing(старыйСчётчик)` + `getLastFileListing()` |
| 5 | UI | Открыть документ в новой вкладке и сверить содержимое | `mainPage.openLastFileListingInNewTab()` → `DocumentFilePage.getDocumentText()` vs `FileUtil.readFile` |
| 6 | API | Изменить расширение файла на `jpg`, код **201** | `TabUtil.closeCurrent()` → `ResourcesMoveClient.changeFileExtension` (POST `/resources/move`) |
| 7 | UI | Убедиться без обновления страницы, что расширение изменилось | `mainPage.waitForListingChanges(старыйСписок)` + проверка `extension()` |
| 8 | API | Загрузить изображение с тем же именем файла с заменой, код **201** | `getUploadFileLink` (для `overwrite=true`) → `ResourcesUploadClient.uploadResourceFile` (PUT по `href`) |
| 9 | UI | Открыть изображение и убедиться, что оно соответствует загруженному, закрыть | `TabUtil.refreshTab()` → `waitForListingsLoaded()` → `mainPage.openLastFileListing()` → `MediaFilePage` → сверка MD5 (`Md5Util` vs `ResourceClient.getMetadata`) |
| 10 | UI | Перетащить изображение в корзину | `mainPage.moveLastFileListingToTrash()` → `ActionUtil.dragAndDrop` + `DeleteFileForm.deleteFile()` |
| 11 | API | Убедиться, что файл находится в корзине | `TrashUtil.waitForItemByOriginPath` (поллинг GET `/trash/resources?sort=-deleted`), сверка `origin_path = disk:<путь>` (`PathPrefix.DISK`) |
| 12 | API | Восстановить файл из корзины, код **201** | `TrashResourcesRestoreClient.restoreFileFromTrash` (PUT `/trash/resources/restore`) |
| 13 | UI | Убедиться без обновления страницы, что изображение вновь на диске | `mainPage.waitForListingChanges(старыйСписок)` |
| 14 | API | Безвозвратно удалить изображение, код **204** | `ResourceClient.deleteFilePermanently` (DELETE `/resources?permanently=true`) |
| 15 | UI | Убедиться, что изображение отсутствует на диске | `mainPage.moveLastFileListingToTrash()` → `mainPage.waitForFailedNotification()` |

> **Про шаг 9.** После перезаписи файла изображением UI продолжает показывать старое превью, поэтому перед открытием выполняется `TabUtil.refreshTab()`.
>
> **Про шаг 15.** Безвозвратно удалённый «мимо корзины» файл остаётся в UI карточкой до перезагрузки страницы, и интерфейс не получает события об удалении. Обновлять вкладку не нужно: тест повторно тянет устаревшую карточку в корзину, Диск отклоняет операцию, и в UI появляется снекбар ошибки (`Orb-Snackbar_feedback_danger`), которого и ждёт `waitForFailedNotification()`. Так факт удаления подтверждается по реакции сервера, а не по перезагрузке.

### Как устроено

- **Автологин без ручного шага.** `AuthPrepare.auth()` под `ThreadContext authRun=true` открывает `oauth.yandex.ru/authorize?...` (client_id подставляется из `.env`), проходит форму `PassportPage` (логин, пароль, «войти»), при появлении SFA ждёт подтверждения от пользователя (`timeoutSFA` из `auth/config.json`), закрывает служебные диалоги Яндекс ID, читает debug-токен через `DebugTokenPage` → `auth-session/access_token.txt`, затем переходит на Диск и выгружает cookies в `auth-session/cookies.json` (`CookieManager.save`). Повторный вход не требуется: пока файлы сессии на месте, шаг 2 только подставляет cookies.
- **Cookie-сессия UI.** Cookies применяются на служебной странице `robotsUrl` (`https://disk.yandex.ru/robots.txt`), потому что она отдаётся с того же домена и остаётся на нём. Если сначала открыть обычный `baseUrl`, неавторизованный пользователь уезжает по редиректу на `360.yandex.ru` / `passport.yandex.ru`, домен меняется — и выставить cookies для `disk.yandex.ru` уже некуда. Поэтому порядок строго такой: `robotsUrl` → `CookieManager.load()` → `baseUrl`. Cookie, который браузер отверг (`UnableToSetCookieException`), логируется предупреждением и не роняет тест.
- **UI-слой на Aquality Selenium.** Страницы — наследники `Form` с локатором и именем (`MainPage`, `PassportPage`, `DocumentFilePage`, `MediaFilePage`, `UnauthorizedMainPage`), отдельная форма-модалка `DeleteFileForm`. Элементы берутся только через `getElementFactory()` с человекочитаемым именем, поэтому все действия автоматически попадают в лог.
- **Список файлов как DTO.** `FileListing` (`name`, `extension`, `imagePreviewHref`) собирается методом `MainPage.from(IButton)`: имя и расширение разбираются по последней точке, `src` превью читается через enum `Attribute`. Тест сравнивает **снимки списка целиком**, а не отдельные локаторы.
- **Проверки без перезагрузки.** Методы `waitForNewListing(старыйСчётчик)` и `waitForListingChanges(старыйСписок)` построены на `getConditionalWait().waitFor(...)`: ожидание прекращается, как только UI догнал изменение, сделанное по API. Дополнительно `waitForListingsLoaded()` ждёт исчезновения спиннера (`Orb-Spinner`).
- **Тестовые данные генерируются на прогон.** `RandomUtil.writeRandomTextFile()` пишет параграф DataFaker в `test-files/random-<дата-время>.txt`; каталог `test-files/` добавлен в `.gitignore`. Изображение `api/files/random.jpg` лежит в ресурсах и загружается как есть.
- **Проверка изображения по MD5.** Локальный MD5 считается `Md5Util` по байтам ресурса `api/files/random.jpg`, серверный берётся из `GET /resources` (`md5` в `GetResourceResponse`). Сравнение хешей доказывает, что на Диске лежит именно загруженный файл, а не картинка-заглушка превью.
- **API-слой отделён от тестов.** Абстрактный `ApiClient` держит HTTP-обёртки `protected`-методами (`getWithQuery`, `postWithQuery`, `putWithQuery`, `deleteWithQuery`, `putBinary`) и настраивает Jackson так, чтобы неизвестные поля ответа не ломали десериализацию. Тест видит только доменные методы клиентов (`getUploadFileLink`, `uploadFile`, `uploadResourceFile`, `changeFileExtension`, `getMetadata`, `deleteFilePermanently`, `getAllTrashSorted`, `restoreFileFromTrash`). Общий ответ — generic-record `ApiResponse<T>` со `statusCode`, `contentType` и распарсенным телом.
- **Ожидание асинхронного API.** Файл в корзине появляется не мгновенно, поэтому `TrashUtil.waitForItemByOriginPath` не делает один запрос, а поллит `getConditionalWait()` до появления элемента с ожидаемым `origin_path`, и только потом возвращает его тесту.
- **Авторизация API на уровне спецификации.** `Specifications.disk(baseUrl, token)` задаёт базовый URI, `Content-Type`/`Accept` из `ContentType`, фильтр логирования `AqualityLoggingFilter` и заголовок `Authorization: OAuth <token>`; токен подставляется из `DebugTokenManager` в `SpecificationsManager`.

### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | UI-автоматизация (обёртка над Selenium WebDriver) и единая среда логирования |
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | HTTP-клиент и DSL для API-запросов |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги, тест-данные, десериализация ответов API) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | генерация случайного текста файла |
| [java-dotenv](https://github.com/cdimascio/dotenv-java) | 5.2.2 | чтение `.env` (client_id, логин, пароль) |
| [Lombok](https://projectlombok.org/) | 1.18.48 | геттеры enum-констант |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | логирование (транзитивно через Aquality) |

Сборка проекта — **Apache Maven**. Драйверы браузеров отдельно устанавливать не нужно: Selenium 4 подтягивает их сам (Selenium Manager).

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Браузеры: **Google Chrome** и **Mozilla Firefox** (актуальные версии)
- IDE (опционально): IntelliJ IDEA

## Структура проекта

```text
<корень репозитория>/
├── .env                          # client_id, yandex_login, yandex_password — в git не попадает
├── .env.example                  # шаблон .env
├── pom.xml
├── README.md
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

### Шаг 3. Заполните `.env`

В корне репозитория создайте файл `.env` по образцу `.env.example`:

```dotenv
client_id=<client_id вашего OAuth-приложения Яндекс ID>
yandex_login=<логин Яндекс ID>
yandex_password=<пароль>
```

- `client_id` подставляется в ссылку авторизации `auth/environment.json` (в файле лежит плейсхолдер `{client_id}`).
- `yandex_login` и `yandex_password` используются формой `PassportPage`.
- Значения читаются из `.env` (java-dotenv), а если ключа там нет — из переменных окружения процесса. Если ключ не найден нигде, тест падает с `Cannot resolve environment key ...`.
- `.env` добавлен в `.gitignore` — секреты в репозиторий не попадают.

### Шаг 4. Запустите тест

```bat
cd /d <путь к репозиторию>
mvn clean test -Dprofile=chrome
```

Вместо `chrome` можно указать `firefox` — тест выполняется в обоих браузерах без изменений в коде.

**Авторизация.** Отдельный запуск не нужен: на шаге 2 тест видит, что `auth-session/cookies.json` отсутствует, и сам вызывает `AuthPrepare.auth()`. Браузер запускается в режиме инкогнито, вход выполняется логином и паролем из `.env`. Если у аккаунта включена двухфакторная аутентификация (SFA), окно браузера ждёт подтверждения — **успейте подтвердить вход** за `timeoutSFA` секунд (по умолчанию 60), иначе тест падает с `You need to pass 2FA for test case`. После успешного входа появятся `auth-session/access_token.txt` (для API-запросов) и `auth-session/cookies.json` (для UI), и следующие прогоны логиниться уже не будут.

> Токен живёт ограниченное время. Если API-шаги начали падать на авторизации, удалите содержимое `auth-session/` — тест авторизуется заново. Обратите внимание: проверяется только наличие `cookies.json`, поэтому при живой куке, но отсутствующем или просроченном `access_token.txt` автологин не сработает — удалите оба файла.

**Логи** пишутся в `logs/test.log` и дублируются в консоль: запросы и ответы API, действия по UI, ожидания. Файл перезаписывается на каждом запуске. Лог автологина уходит отдельно — в `logs/auth-prepare.log`, логи прогонов теста не затираются.

### Запуск через IntelliJ IDEA

1. Установите Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open** и выберите корень репозитория.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Создайте `.env` (см. Шаг 3), чтобы класс `AuthEnvironmentData` смог собрать ссылку авторизации.
6. Тест: откройте `YandexDiskTest` (`src/test/java/ru/kaelth4s/tests/test/`) → ПКМ → **Modify Run Configuration…** → VM options `-Dprofile=chrome` (или `-Dprofile=firefox`) → **Run**.

## Конфигурация

### `.env`

Секреты и идентификатор OAuth-приложения (в git не попадает):

```dotenv
client_id=
yandex_login=
yandex_password=
```

### `api/environment.json`

Базовый URL API Яндекс.Диска:

```json
{
  "baseUrl": "https://cloud-api.yandex.net/v1/disk"
}
```

### `ui/environment.json`

URL стенда и служебная страница, на которой выставляются cookies перед возвратом на Диск:

```json
{
  "baseUrl": "https://disk.yandex.ru",
  "robotsUrl": "https://disk.yandex.ru/robots.txt"
}
```

> **Важно.** Cookies применяются именно на `robotsUrl` (`https://disk.yandex.ru/robots.txt`), потому что она отдаётся с того же домена `disk.yandex.ru` и остаётся на нём. Если сначала открыть обычный `baseUrl`, неавторизованный пользователь уходит по редиректу на `360.yandex.ru` либо `passport.yandex.ru`, домен становится другим — и cookies для `disk.yandex.ru` выставить уже некуда. Поэтому порядок строго такой: `robotsUrl` → `CookieManager.load()` → `baseUrl`.

### `auth/environment.json`

Ссылка авторизации OAuth-приложения Яндекс ID; `{client_id}` подставляется из `.env`:

```json
{
  "baseUrl": "https://oauth.yandex.ru/authorize?response_type=token&client_id={client_id}"
}
```

### `auth/config.json`

Таймауты автологина (лежит в `src/main/resources`, читается `AuthConfigManager`):

```json
{
  "timeoutSFA": 60,
  "timeoutAuthFormLoad": 5
}
```

`timeoutSFA` — сколько секунд ждать подтверждения двухфакторной аутентификации, `timeoutAuthFormLoad` — ожидание загрузки формы Яндекс ID.

### `api/test-data.json`

Данные шагов теста: путь файла на Диске, новое расширение и путь к изображению в ресурсах.

```json
{
  "uploadTextFileDiskPath": "/random.txt",
  "changedFileExtension": "jpg",
  "uploadImageFileResourcePath": "api/files/random.jpg"
}
```

Текст текстового файла заранее не хранится: перед загрузкой он генерируется DataFaker'ом в `test-files/random-<дата-время>.txt` (`RandomUtil.writeRandomTextFile`), а `random.jpg` — небольшой бинарный файл в ресурсах, который загружается как есть.

### `settings.<profile>.json`

Профили Aquality Selenium. Профиль выбирается системным свойством `profile` (`-Dprofile=...`), имя файла — `settings.<profile>.json`:

| Профиль | Файл | Браузер | Особенности |
|---|---|---|---|
| `chrome` | `settings.chrome.json` | Chrome | `--lang=en`, `--incognito`, локаль `en` |
| `firefox` | `settings.firefox.json` | Firefox | локаль `en`, отключены строгие ограничения `SameSite` для cookies |

В остальном профили одинаковы: локальный драйвер (`isRemote=false`), подсветка элементов, ожидание условия 35 с, загрузка страницы 20 с, интервал опроса 300 мс, 2 повтора действий, кэш элементов выключен.

Авторизация и тест используют один и тот же профиль: тест сам логинится в браузере, запущенном с `-Dprofile=chrome|firefox`, поэтому отдельных `auth-*` профилей больше нет.

### `log4j2.properties`

Единый формат логов для консоли и файлов. `RoutingAppender` разводит записи по файлам: при выставленном `ThreadContext` `authRun=true` (это делает `AuthPrepare`) логи идут в `logs/auth-prepare.log`, иначе — в `logs/test.log`.

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
