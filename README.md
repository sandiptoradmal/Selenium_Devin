# Selenium WebDriver + TestNG Automation Framework

Production-grade boilerplate: Java 17, Maven, Selenium 4 (Selenium Manager — no manual driver
binaries), TestNG, ExtentReports, Log4j2.

## Project Structure

```
selenium-testng-framework/
├── pom.xml
├── testng.xml
├── src/
│   ├── main/java/com/framework/
│   │   ├── pageobjects/
│   │   │   ├── BasePage.java        # explicit-wait wrapped actions (click, sendKeys, isVisible...)
│   │   │   └── LoginPage.java       # sample page object, private By locators, no assertions
│   │   ├── utilities/
│   │   │   ├── ConfigReader.java    # reads config.properties
│   │   │   ├── ScreenshotUtil.java  # PNG capture on demand
│   │   │   └── ExtentManager.java   # thread-safe singleton ExtentReports instance
│   │   └── listeners/
│   │       └── TestListener.java    # ITestListener: Extent logging + screenshot-on-failure
│   └── test/
│       ├── java/com/framework/
│       │   ├── base/
│       │   │   └── BaseTest.java    # cross-browser setup/teardown, ThreadLocal driver
│       │   └── tests/
│       │       └── LoginTest.java   # sample test, assertions live here
│       └── resources/
│           ├── config.properties
│           └── log4j2.xml
└── .gitignore
```

## Running

```bash
# Full suite as defined in testng.xml (chrome + firefox + edge, 3 threads in parallel)
mvn clean test

# Override the browser for an ad-hoc single run
mvn clean test -Dbrowser=chrome

# Override parallel thread count
mvn clean test -Dthread.count=5
```

Reports are written to `test-output/reports/ExtentReport_<timestamp>.html`,
screenshots of failures to `test-output/screenshots/`, and logs to `logs/automation.log`.

## Design Notes

- **Driver management**: Selenium Manager (bundled since Selenium 4.6) resolves the correct
  driver binary for the browser installed locally — no WebDriverManager dependency, no
  binaries committed to source control.
- **Thread safety**: both the `WebDriver` (in `BaseTest`) and `ExtentTest` (in `TestListener`)
  are held in `ThreadLocal`s so `mvn test` can run classes/methods in parallel safely.
- **No PageFactory**: locators are `private By` fields resolved fresh on every call via
  `BasePage`'s explicit-wait helpers, which avoids stale-element issues common with
  `@FindBy`-cached fields on dynamic pages.
- **Assertions stay in tests**: page objects only expose actions and raw state (`getText`,
  `isVisible`, etc.) — never `Assert` calls — keeping them reusable across positive and
  negative test scenarios.
- **Config layering**: `config.properties` provides defaults; testng.xml `<parameter>` values
  (browser, headless) override them per suite `<test>` block, which itself can be overridden
  from the CLI via `-D` system properties in `pom.xml`.

## Adding a New Page/Test

1. Add a new page object under `pageobjects/`, extending `BasePage`.
2. Add a new test class under `tests/`, extending `BaseTest`.
3. Register the class in `testng.xml` under the relevant `<test>` block(s).
