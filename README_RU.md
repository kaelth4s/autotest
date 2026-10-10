**Русский** | [English](README.md)

# UI-автотесты магазина Steam

## Описание решения

Набор автоматизированных UI-тестов для сайта **Steam** (<https://store.steampowered.com/>), написанный на **Java**. Проект проверяет ключевые сценарии работы магазина:

| Тест | Что проверяет |
|---|---|
| `StoreAboutTest` | переход на страницу **About**, что значение «играют сейчас» меньше, чем «в сети», и корректный возврат на страницу магазина |
| `TopSellersTest` | переход в раздел **Top Sellers**, применение чекбокс-фильтров (ОС, категория, теги), соответствие счётчика результатов фактическому количеству игр в списке, корректность данных на странице выбранной игры |
| `MarketTest` | **Community Market**: открытие формы расширенного поиска, фильтры по игре / герою / редкости, поиск по названию, обновление списка при снятии фильтра, соответствие страницы предмета данным с предыдущей страницы |

Как устроено:

- паттерн **Page Object** — каждая страница Steam описана отдельным классом (`page/`), повторяющиеся элементы вынесены в компоненты (`page/component/`);
- базовые классы `BaseComponent` / `BasePage` инкапсулируют поиск элементов, клики, ввод текста и проверки открытия страницы;
- **фабрика браузеров** (`browser/BrowserFactory`, реализации для Chrome и Firefox) — браузер выбирается в конфигурации;
- **явные ожидания** через `WebDriverWait` (`service/WaitManager`), время ожидания настраивается в конфиге;
- конфигурация запуска (`config.json`) и тестовые данные (`market-test.json`, `top-sellers-test.json`) хранятся в **JSON** и маппятся в DTO через Jackson (`ConfigDataManager`, `TestDataManager`);
- WebDriver создается как синглтон (`driver/DriverManager`), драйвер браузера автоматически скачивается через WebDriverManager.

### Используемые библиотеки

| Библиотека | Версия | Назначение |
|---|---|---|
| [Selenium WebDriver](https://www.selenium.dev/) | 4.47.0 | управление браузером |
| [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) | 6.3.4 | автоматическое скачивание webdriver |
| [JUnit Jupiter](https://junit.org/junit5/) | 6.1.3 | тестовый фреймворк |
| [AssertJ](https://assertj.github.io/doc/) | 3.27.7 | fluent-Assertions |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 2.22.2 | чтение JSON (конфиги и тест-данные) |

Сборка проекта — **Apache Maven**.

### Окружение

- ОС: Windows 10/11 (инструкции рассчитаны на `cmd`)
- JDK: **Java 25** (Eclipse Temurin)
- Maven: **3.9.16**
- Браузер: **Google Chrome** (по умолчанию; поддерживается смена на Firefox (без параметров) — меняется в `src/main/resources/config.json`)
- IDE (опционально): IntelliJ IDEA

## Структура проекта

```text
<корень репозитория>/
├── pom.xml                                  # Maven: зависимости и настройки сборки
└── src/
   ├── main/
   │   ├── java/ru/kaelth4s/
   │   │   ├── BaseComponent.java           # базовые операции: поиск, ожидания, клики, ввод
   │   │   ├── BasePage.java                # базовая страница (проверка isPageOpened)
   │   │   ├── browser/                     # фабрики браузеров: Chrome, Firefox
   │   │   ├── driver/DriverManager.java    # синглтон управления WebDriver
   │   │   ├── dto/                         # DTO конфигурации (ConfigData, ChromeConfig и др.)
   │   │   ├── object/Game.java             # модель игры
   │   │   ├── page/                        # Page Object'ы страниц Steam
   │   │   │   └── component/               # общие компоненты страниц (Header, фильтры)
   │   │   ├── service/                     # WaitManager, ConfigDataManager, ChromeOptionsManager
   │   │   └── util/                        # JsonUtils, кастомные ExpectedCondition
   │   └── resources/
   │       └── config.json                  # настройки: браузер, URL, таймауты, опции Chrome
   └── test/
      ├── java/ru/kaelth4s/
      │   ├── TestDataManager.java         # загрузка тестовых данных из JSON
      │   ├── base/BaseTest.java           # базовый класс тестов (setUp/tearDown)
      │   ├── dto/                         # DTO тестовых данных (фильтры и т.д.)
      │   └── test/                        # StoreAboutTest, TopSellersTest, MarketTest
      └── resources/test-data/             # market-test.json, top-sellers-test.json
```

## Как запустить

> Все команды выполняются в **cmd**. После установки Java/Maven перезапустите окно cmd, чтобы обновился `PATH`.

### Подготовка (общие шаги)

**Шаг 0. Установите Google Chrome** (браузер по умолчанию для тестов), если он не установлен:
<https://www.google.com/chrome/>

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

После выполнения подготовительных шагов (Chrome, Java 25, Maven):

```bat
cd /d <путь к репозиторию>
mvn clean test
```

Maven сам скачает все зависимости и запустит все три теста (`StoreAboutTest`, `TopSellersTest`, `MarketTest`). Откроется окно Chrome — не закрывайте его вручную во время прохождения тестов.

Запуск одного конкретного теста:

```bat
mvn test -Dtest=MarketTest
```

Отчёты о результатах появятся в `task2\target\surefire-reports\`.

---

### Вариант 2. Запуск через IntelliJ IDEA

1. Установите Chrome (см. Шаг 0), при необходимости — Java 25 через winget (см. Шаг 1).
2. Откройте проект: **File → Open Project** (или **File → Open**) и выберите папку **`task2`**.
3. Дождитесь загрузки зависимостей и нажмите **Load Maven** в предлагаемом окне.
4. Выберите JDK: **File → Project Structure → Project Settings → Project → SDK** — выберите **25 Eclipse Temurin** (скачанный через winget, если не было) или любой другой **Java 25 JDK**.
5. Запустите тесты: правой кнопкой мыши по нужному тесту в `src/test/java/ru/kaelth4s/test` → **Run '...'**, либо все тесты сразу ПКМ — `src/test/java` → **Run 'Tests in 'java''** .
