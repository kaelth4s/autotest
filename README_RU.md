**Русский** | [English](README.md)

# UI-автотесты demoqa.com

## Описание решения

Набор автоматизированных UI-тестов для сайта **DemoQA** (<https://demoqa.com/>), написанный на **Java**. Проект реализует собственный тестовый фреймворк с нуля: базовые классы элементов и форм, фабрику браузеров, синглтон драйвера и набор утилит. Проверяются ключевые сценарии работы сайта:

| Тест | Что проверяет |
|---|---|
| `AlertsTest` | работа с JavaScript-алертами: `alert`, `confirm`, `prompt`, проверка текста и ввод данных |
| `IframeTest` | вложенные фреймы: навигация по parent/child iframe, сравнение текста в Frames и Nested Frames |
| `TablesTest` | CRUD с таблицами: добавление пользователя через форму регистрации, проверка данных в таблице, удаление строки (DDT + параметризация) |
| `HandlesTest` | открытие/закрытие вкладок, переключение между ними, переход по ссылке Home |
| `SliderTest` | установка случайного значения слайдера, остановка Progress Bar на значении возраста инженера |
| `DatePickerTest` | проверка текущих значений полей даты, выбор ближайшего 29 февраля |

Как устроено:

- **Собственный тестовый фреймворк** — абстрактные `BaseElement` (клик, чтение текста/атрибутов, поиск дочерних элементов) и `BaseForm` (проверка открытия по уникальному элементу), от которых наследуются все элементы и страницы;
- **Паттерн Page Object** — каждая страница DemoQA описана отдельным классом (`tests/page/`), переиспользуемые формы вынесены в `tests/form/`, кастомный календарь — в `tests/element/DatePicker`;
- **Композиция** — `BaseCategoryForm` хранит `LeftPanelForm`, все страницы категорий наследуются от него и получают навигацию по левому меню без дублирования кода;
- **Навигация через enum** — `Category` (группа + href пункта меню), переход выполняется одним методом `LeftPanelForm.clickCategory(Category)`;
- **Singleton** (`DriverManager`) — WebDriver создаётся один раз и переиспользуется до завершения теста;
- **Factory Method** (`BrowserFactory` + `ChromeBrowserFactory` / `FirefoxBrowserFactory`) — браузер выбирается в конфигурации (`config.json`), фабрика создаёт нужный экземпляр драйвера;
- **Явные ожидания** — `WaitUtil` работает с `By`-локаторами: базовые ожидания (`defaultWaitDuration`) плюс именованные кастомные ожидания из конфига (`additionalWaits`, например `progressBar` с таймаутом и polling-интервалом);
- **Типобезопасные атрибуты** — enum `Attribute` (`VALUE`, `HREF`, `CLASS`) вместо строковых литералов;
- **Конфигурация запуска** (`config.json`) и **тестовые данные** (`test-data.json`) хранятся в **JSON** и маппятся в DTO через Jackson (`ConfigManager`, `TestDataManager`);
- **Утилиты**: `AlertUtil` (работа с alert/confirm/prompt), `FrameUtil` (навигация по iframe), `TabUtil` (управление вкладками), `ActionUtil` (скролл и клавиатура через Actions), `JsonUtil` (чтение JSON), `ParserUtil` (парсинг чисел);
- **Работа с моделями** — `RegistrationData` наследует `BaseTableData`: строка таблицы маппится в модель по заголовкам колонок, сравнение — через `equals`/`hashCode`;
- **DDT + параметризация** — `TablesTest` использует `@ParameterizedTest` + `@MethodSource`, данные берутся из `test-data.json`;
- **Случайная генерация тестовых данных** — кастомная аннотация `@RandomGenerated` + `RandomFiller` заполняют поля record'ов случайными значениями через DataFaker;
- **Логирование** — SLF4J + Logback, вывод в консоль и файл `logs/latest-test.log`.

### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Selenium WebDriver](https://www.selenium.dev/) | 4.47.0 | управление браузером |
| [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) | 6.3.4 | автоматическое скачивание webdriver |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк (включая параметризованные тесты) |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги и тест-данные) |
| [DataFaker](https://www.datafaker.net/) | 2.7.0 | генерация случайных тестовых данных |
| [Logback](https://logback.qos.ch/) | 1.6.3 | логирование (консоль + файл) |

Сборка проекта — **Apache Maven**.

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Браузер: **Google Chrome** (по умолчанию) или **Mozilla Firefox** — меняется в `src/main/resources/config.json`
- IDE (опционально): IntelliJ IDEA

## Структура проекта

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

## Как запустить

> Все команды выполняются в **cmd**. После установки Java/Maven перезапустите окно cmd, чтобы обновился `PATH`.

### Подготовка (общие шаги)

**Шаг 0. Установите браузер и выберите его для тестов**

Скачайте и установите один из поддерживаемых браузеров:

- **Google Chrome**: <https://www.google.com/chrome/>
- **Mozilla Firefox**: <https://www.firefox.com/thanks/>

Укажите нужный браузер в файле `src/main/resources/config.json`:

```json
{
  "browser": "CHROME"
}
```

Доступные значения: `CHROME`, `FIREFOX`. Драйвер для выбранного браузера скачивается автоматически через WebDriverManager.

**Шаг 1. Установите Java 25 (Eclipse Temurin)** — если не установлена. `winget` доступен прямо в cmd:

```bat
winget install --id EclipseAdoptium.Temurin.25.JDK --source winget --accept-package-agreements
```

Перезапустите cmd и проверьте установку:

```bat
java -version
```

**Шаг 2. Установите Maven 3.9.16** — если не установлен.

Вариант А — автоматическая установка одной командой (из cmd):

```bat
powershell -Command "$url='https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip'; $dir='%USERPROFILE%\maven'; New-Item -ItemType Directory -Force -Path $dir | Out-Null; $zip=Join-Path $dir 'maven.zip'; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dir -Force; Remove-Item $zip; $mvn=Join-Path $dir 'apache-maven-3.9.16'; [Environment]::SetEnvironmentVariable('MAVEN_HOME', $mvn, 'User'); $path=[Environment]::GetEnvironmentVariable('Path', 'User'); if ($path -notlike '*'+$mvn+'\bin*') { [Environment]::SetEnvironmentVariable('Path', $path+';'+$mvn+'\bin', 'User') }"
```

Вариант Б — ручная установка:

1. Скачайте архив вручную из браузера: <https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip>
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

---

### Вариант 1. Запуск через консоль

После выполнения подготовительных шагов (браузер, Java 25, Maven):

```bat
cd /d <путь к репозиторию>
mvn clean test
```

Maven сам скачает все зависимости и запустит все шесть тестов. Откроется окно браузера — не закрывайте его вручную во время прохождения тестов.

Запуск одного конкретного теста:

```bat
mvn test -Dtest=AlertsTest
```

Логи пишутся в `task3\logs\latest-test.log`.

---

### Вариант 2. Запуск через IntelliJ IDEA

1. Установите браузер (см. Шаг 0) и Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open Project** (или **File → Open**) и выберите папку **`task3`**.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — выберите **25 Eclipse Temurin** (скачанный через winget) или любой другой **Java 25 JDK**.
5. Запустите тесты: правой кнопкой мыши по нужному тесту в `src/test/java/ru/kaelth4s/tests/test` → **Run '...'**, либо все тесты сразу ПКМ по папке `src/test/java` → **Run 'Tests in 'java''**.
