# UI-автотесты userinyerface.com

## Описание решения

Набор автоматизированных UI-тестов для игры-сайта **User In Yer Face** (<https://userinyerface.com/>), написанный на **Java** с использованием корпоративного фреймворка **Aquality Selenium**. Проект проверяет ключевые сценарии прохождения квеста:

| Тест | Что проверяет |
|---|---|
| `CardsTest` | полное прохождение трёх карточек: ввод email/пароля/домена, принятие условий, выбор случайных интересов, загрузка аватара, переход на финальную карточку |
| `CookiesTest` | принятие cookie-баннера и его исчезновение |
| `HelpFormTest` | скрытие формы помощи по кнопке «Send to bottom» |
| `TimerTest` | старт таймера с ожидаемого значения (`00:00:00`) |

### Как устроено

- **Aquality Selenium как корпоративная среда** — работа с браузером, элементами и ожиданиями ведётся через `AqualityServices`; конфигурация — через `settings.*.json`, поддерживается профильный запуск (`-Dprofile=chrome` / `-Dprofile=firefox`);
- **Page Object паттерн** — каждая карточка и форма описана отдельным классом, унаследованным от `Form` (`LoginCardPage`, `InterestsCardPage`, `PersonalDetailsCardPage`, `CookiesForm`, `HelpForm`); общие для всех карточек элементы вынесены в `CardPage`;
- **Element Factory** — все элементы создаются через `getElementFactory()`, работа ведётся через интерфейсы `ITextBox`, `IButton`, `ICheckBox`, `ILabel`, `ILink`;
- **Явные ожидания Aquality** — для проверки открытия страниц и закрытия форм используется `state().waitForDisplayed()` / `waitForNotDisplayed()`;
- **Состояния элементов** — для скрытых чекбоксов и списков применяется `ElementState.EXISTS_IN_ANY_STATE`;
- **Работа с кастомными чекбоксами** — клик выполняется по видимому `<span class="checkbox__box">`, а состояние читается с реального `<input>` через `isChecked()`;
- **Конфигурация в JSON** — `environment.json` (URL стенда), `test-data.json` (тестовые данные), `settings.chrome.json` / `settings.firefox.json` (настройки Aquality); маппинг через Jackson в record-DTO;
- **Разделение DTO** — «сырые» данные из JSON (`CardsTestDataJson`, `TimerTestDataJson`, `TestDataJson`) отделены от доменных моделей (`CardsTestData`, `TimerTestData`), собранных в `TestDataManager`;
- **Случайная генерация данных** — `RandomUtil` на базе DataFaker генерирует email без TLD, пароль с учётом требований игры (заглавная, цифра, кириллица, буква из email), случайный национальный домен из выпадающего списка;
- **Загрузка файла без нативного диалога** — `WebUtil` + `JsUtil` + `resources/js/upload_file.js`: скрипт перехватывает `HTMLInputElement.prototype.click`, не даёт открыться системному окну выбора файла и подставляет картинку через `DataTransfer` с вызовом события `change`;
- **Единая работа с classpath-ресурсами** — `FileUtil` читает и текстовые ресурсы (`readResource`), и бинарные в Base64 (`readResourceAsBase64`); картинка и JS-скрипт живут в classpath и читаются одинаково;
- **Типобезопасные атрибуты и скрипты** — enum `Attribute` (`ID`, `CLASS`) вместо строковых литералов, enum `JsScript` (`UPLOAD_FILE`) для путей к JS;
- **Утверждения** — AssertJ (`assertThat(...).as(...).isTrue()`) с осмысленными описаниями;
- **Логирование** — Log4j2, вывод в консоль и файл `logs/test.log` (файл перезаписывается на каждом запуске через `appender.file.append=false`).

### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Aquality Selenium](https://github.com/aquality-automation/aquality-selenium-java) | 4.25.0 | корпоративная среда веб-автоматизации (обёртка над Selenium) |
| [Selenium WebDriver](https://www.selenium.dev/) | 4.48.0 | управление браузером (транзитивно через Aquality) |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги и тест-данные) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | генерация случайных тестовых данных |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.26.1 | логирование (транзитивно через Aquality) |

Сборка проекта — **Apache Maven**.

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Браузер: **Google Chrome** (по умолчанию) или **Mozilla Firefox** — выбирается профилем при запуске
- IDE (опционально): IntelliJ IDEA

## Структура проекта

```text
<корень репозитория>/
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

## Как запустить

> Все команды выполняются в **cmd**. После установки Java/Maven перезапустите окно cmd, чтобы обновился `PATH`.

### Подготовка (общие шаги)

#### Шаг 0. Установите браузер

Скачайте и установите один из поддерживаемых браузеров:

- **Google Chrome**: <https://www.google.com/chrome/>
- **Mozilla Firefox**: <https://www.firefox.com/thanks/>

Драйвер скачивается автоматически через Selenium Manager (встроен в Selenium 4.48.0).

#### Шаг 1. Установите Java 25 (Eclipse Temurin)

Если не установлена. `winget` доступен прямо в cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Перезапустите cmd и проверьте установку:

```bat
java -version
```

#### Шаг 2. Установите Maven 3.9.16

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

### Вариант 1. Запуск через консоль

После выполнения подготовительных шагов (браузер, Java 25, Maven):

```bat
cd /d <путь к репозиторию>
mvn clean test -Dprofile=chrome
```

Maven скачает зависимости и запустит все четыре теста. Откроется окно браузера — не закрывайте его вручную во время прохождения тестов.

**Запуск в Firefox:**

```bat
mvn clean test -Dprofile=firefox
```

**Запуск одного конкретного теста:**

```bat
mvn test -Dtest=CardsTest -Dprofile=chrome
```

**Логи** пишутся в `logs/test.log` и дублируются в консоль. Файл перезаписывается на каждом запуске.

### Профили браузеров

Выбор браузера управляется переменной `profile`. Aquality Selenium ищет в classpath файл с именем `settings.<profile>.json`:

| Профиль | Файл конфигурации |
|---|---|
| `chrome` | `settings.chrome.json` |
| `firefox` | `settings.firefox.json` |

Без `-Dprofile=...` Aquality ищет `settings.json` (без суффикса), которого в проекте нет — поэтому профиль указывать обязательно.

### Вариант 2. Запуск через IntelliJ IDEA

1. Установите браузер (см. Шаг 0) и Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open** и выберите корень репозитория.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — **25 Eclipse Temurin**.
5. Откройте нужный тест в `src/test/java/ru/kaelth4s/tests/test/...` → ПКМ → **More Run/Debug...** → **Modify Run Configuration** → в поле **VM options** добавьте:

   ```
   -Dprofile=chrome
   ```

   (или `-Dprofile=firefox`)

6. Запустите тест `SHIFT+F10` (или кнопка запуска справа сверху).

## Конфигурация

### `environment.json`

URL тестируемого стенда:

```json
{
  "baseUrl": "https://userinyerface.com/"
}
```

### `test-data.json`

Тестовые данные, зависящие от сценария:

- `cardsTestData.interestsCount` — сколько интересов выбрать на карточке 2;
- `cardsTestData.imageResourcePath` — путь к файлу аватара **относительно classpath** (`test-files/` лежит в `src/test/resources/`);
- `timerTestData.expectedTimerStart` — ожидаемое стартовое значение таймера.

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

Остальные данные (email-имя, домен, пароль, национальный домен) генерируются динамически на основе `RandomUtil` в момент старта теста.

### `settings.chrome.json` / `settings.firefox.json`

Конфигурация Aquality Selenium: `browserName`, `isRemote`, таймауты, retry-политика, опции браузера. Обязательные секции (`timeouts`, `retry`, `logger`, `elementCache`) заполнены значениями по умолчанию.