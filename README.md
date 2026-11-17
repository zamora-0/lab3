# TODO Scanner (Поиск "TODO" в исходном коде)

Консольное Java-приложение для автоматического поиска комментариев с ключевыми словами (`TODO`, `FIXME` и др.) в исходном коде проекта и формирования сгруппированного отчёта по файлам.

---

## 1. Краткое описание проекта

**Назначение:** утилита рекурсивно обходит заданную директорию, фильтрует файлы по настроенным расширениям и выполняет регистронезависимый поиск маркеров задач (`TODO`, `FIXME` и пользовательских ключевых слов) в комментариях кода и текстовых/документационных файлах.

**Решаемые задачи:**
- Быстрый аудит технического долга и отложенных доработок в репозитории.
- Выявление незавершённых участков кода на этапе разработки и в пайплайне CI/CD.
- Группировка найденных замечаний по относительным путям файлов с указанием точного номера строки (`L<номер>`).

**Технологический стек:**
- **Язык:** Java 11 (совместимо со сборкой и запуском на JDK 11, 17, 21 и новее).
- **Сборщик:** Gradle 8.14.2 (Kotlin DSL, Gradle Wrapper).
- **Тестирование:** JUnit 5 (JUnit Jupiter).
- **CI/CD:** GitHub Actions (matrix-сборка на JDK 11/17/21, кэширование Gradle, публикация артефактов и автоматическое создание GitHub Releases по тегам `v*`).

**Структура пакетов:**
- `by.gstu.project.config` — загрузка конфигурации из `app.properties` с резервными значениями по умолчанию (`AppConfig`, `ConfigLoader`).
- `by.gstu.project.model` — доменные модели найденной задачи (`TodoItem`) и агрегированного отчёта (`ScanReport`).
- `by.gstu.project.scanner` — фильтрация по расширениям (`FileExtensionFilter`), извлечение комментариев (`CommentExtractor`) и рекурсивное сканирование (`TodoScanner`).
- `by.gstu.project.report` — форматирование отчёта (`ReportFormatter`) и вывод в консоль (`ReportPrinter`, `ConsoleReportPrinter`).
- `by.gstu.project.Main` — точка входа CLI-приложения.

---

## 2. Пример работы

### Входные данные

Фрагмент исходного кода (`src/main/java/by/gstu/project/Main.java`):
```java
// TODO: Add CLI options (--dir, --keywords, --ext) to override app.properties parameters
```

Фрагмент комментария в `src/main/java/by/gstu/project/report/ReportFormatter.java`:
```java
// FIXME: Support configurable output formats (e.g., JSON or Markdown) in addition to plain text
```

Фрагмент документации `src/main/resources/roadmap.md`:
```markdown
- TODO: Add optional summary statistics by keyword type
```

### Вывод в консоль (`stdout`)

```text
Found 3 issues:

[src/main/java/by/gstu/project/Main.java]
  L51: TODO: Add CLI options (--dir, --keywords, --ext) to override app.properties parameters

[src/main/java/by/gstu/project/report/ReportFormatter.java]
  L40: FIXME: Support configurable output formats (e.g., JSON or Markdown) in addition to plain text

[src/main/resources/roadmap.md]
  L5: TODO: Add optional summary statistics by keyword type
```

Если совпадений не найдено, приложение выводит:
```text
Found 0 issues.
```

---

## 3. Как выполнить сборку проекта

Проект использует **Gradle Wrapper**, поэтому предварительно устанавливать Gradle вручную не требуется.

> **Примечание:** Gradle Wrapper версии 8.14.2 поддерживает запуск на JDK 11–24 (при этом собранный `.jar` имеет байт-код Java 11 и запускается на любой JVM от 11 и выше, включая JDK 25).

**Linux / macOS:**
```bash
chmod +x gradlew
./gradlew clean build
```

**Windows (PowerShell):**
```powershell
.\gradlew.bat clean build
```

После успешной сборки исполняемый fat JAR-файл создаётся по пути:
```text
build/libs/todo-scanner-1.0.0.jar
```

Для генерации JavaDoc-документации выполните:
```bash
./gradlew javadoc
```
Сгенерированная HTML-документация будет находиться в `build/docs/javadoc/index.html`.

---

## 4. Как выполнить конфигурацию проекта

Перед первым запуском скопируйте шаблон конфигурации `app.properties.example` в файл `app.properties` (локальный файл `app.properties` добавлен в `.gitignore` и не отслеживается Git):

**Linux / macOS:**
```bash
cp app.properties.example app.properties
```

**Windows (PowerShell):**
```powershell
Copy-Item app.properties.example app.properties
```

### Содержимое `app.properties`

```properties
# Директория для сканирования
scan.source.directory=./src
# Расширения файлов для сканирования (через запятую)
scan.file.extensions=.java,.kt,.xml,.md
# Ключевые слова для поиска (через запятую, регистронезависимо)
scan.keywords=TODO,FIXME
```

### Описание параметров

| Параметр | Описание | Значение по умолчанию | Пример настройки |
|---|---|---|---|
| `scan.source.directory` | Путь к корневой директории для рекурсивного обхода (относительный или абсолютный) | `./src` | `./src/main` или `/var/projects/app` |
| `scan.file.extensions` | Список расширений файлов через запятую (без учёта регистра, ведущая точка добавляется автоматически при отсутствии) | `.java,.kt,.xml,.md` | `.java,.kt,.xml,.md,.txt` |
| `scan.keywords` | Список ключевых слов для регистронезависимого поиска через запятую | `TODO,FIXME` | `TODO,FIXME,BUG,NOTE` |

Если файл `app.properties` отсутствует или какой-либо параметр оставлен пустым, приложение автоматически использует значения по умолчанию. Также путь к альтернативному файлу конфигурации можно передать первым аргументом командной строки или через системное свойство `-Dconfig.file=...`.

---

## 5. Как запустить тесты и какой результат ожидать

Модульные тесты написаны с использованием **JUnit 5 (JUnit Jupiter)** и покрывают все пакеты бизнес-логики (`config`, `model`, `scanner`, `report`, `Main`).

**Linux / macOS:**
```bash
./gradlew test
```

**Windows (PowerShell):**
```powershell
.\gradlew.bat test
```

### Ожидаемый результат

Все **21 тест** в 8 тестовых классах должны завершиться со статусом `PASSED`, а задача `:test` — завершиться с `BUILD SUCCESSFUL`:
- `AppConfigTest` (3 теста) — значения по умолчанию, неизменяемость коллекций, валидация параметров.
- `ConfigLoaderTest` (3 теста) — чтение `app.properties`, нормализация расширений, фоллбэк на значения по умолчанию.
- `ScanReportAndTodoItemTest` (3 теста) — валидация `TodoItem`, группировка по файлам и сортировка по номерам строк.
- `FileExtensionFilterTest` (2 теста) — регистронезависимая фильтрация файлов и игнорирование директорий.
- `CommentExtractorTest` (3 теста) — извлечение однострочных/блочных комментариев Java/Kotlin, XML-комментариев, игнорирование строковых литералов и JavaDoc.
- `TodoScannerTest` (3 теста) — рекурсивный обход, фильтрация расширений, поиск ключевых слов и обработка ошибок несуществующей директории.
- `ReportFormatterAndPrinterTest` (2 теста) — форматирование отчёта с `L<номер>` и вывод в `PrintStream`.
- `MainTest` (2 теста) — сквозной запуск CLI и обработка некорректной конфигурации.

Подробный HTML-отчёт о тестировании формируется в `build/reports/tests/test/index.html`.

---

## 6. Как запустить приложение

Запуск с конфигурацией из `./app.properties` (или со значениями по умолчанию):
```bash
java -jar build/libs/todo-scanner-1.0.0.jar
```

Запуск с указанием произвольного файла конфигурации через аргумент командной строки:
```bash
java -jar build/libs/todo-scanner-1.0.0.jar path/to/custom.properties
```

Запуск с указанием файла конфигурации через системное свойство JVM:
```bash
java -Dconfig.file=path/to/custom.properties -jar build/libs/todo-scanner-1.0.0.jar
```
