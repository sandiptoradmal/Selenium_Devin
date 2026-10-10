package com.framework.base;

import com.framework.utilities.ConfigReader;
import com.framework.utilities.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * Parent class for every test class.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Spins up a fresh, isolated WebDriver instance per test method (safe for parallel execution
 *         because the driver is held in a {@link ThreadLocal}).</li>
 *     <li>Resolves the target browser from testng.xml &lt;parameter&gt; values, falling back to
 *         config.properties, so runs can be re-targeted without touching code.</li>
 *     <li>Relies exclusively on Selenium Manager (bundled with Selenium 4.6+) to resolve driver
 *         binaries — no WebDriverManager dependency and no binaries checked into the repo.</li>
 *     <li>Centralizes implicit/explicit wait and page-load timeout configuration.</li>
 * </ul>
 */
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    /**
     * Convenience accessor for subclasses (test classes). Delegates to {@link DriverManager},
     * the single source of truth for the current thread's WebDriver instance, so page objects,
     * tests, and listeners all read from the same place.
     */
    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("") String browserParam, @Optional("") String headlessParam) {
        // Resolution order: testng.xml <parameter> > -Dbrowser/-Dheadless CLI override > config.properties default.
        String browser = !browserParam.isEmpty()
                ? browserParam
                : System.getProperty("browser", ConfigReader.get("browser", "chrome"));
        boolean headless = !headlessParam.isEmpty()
                ? Boolean.parseBoolean(headlessParam)
                : Boolean.parseBoolean(System.getProperty("headless",
                        String.valueOf(ConfigReader.getBoolean("headless", true))));

        log.info("Initializing '{}' driver (headless={})", browser, headless);
        WebDriver driver = createDriver(browser, headless);

        int implicitWait = ConfigReader.getInt("implicit.wait.seconds", 5);
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout.seconds", 30);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();

        DriverManager.setDriver(driver);

        String baseUrl = ConfigReader.get("base.url", "https://www.priceline.com");
        driver.get(baseUrl);
        log.info("Navigated to base URL: {}", baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        DriverManager.quitDriver();
        log.info("Driver quit for test: {}", result.getMethod().getMethodName());
    }

    /**
     * Creates a WebDriver instance for the requested browser.
     * <p>
     * Selenium Manager (built into selenium-java as of 4.6+) transparently resolves and
     * downloads the correct driver binary for the detected browser version — no explicit
     * driver-path setup is required.
     */
    private WebDriver createDriver(String browser, boolean headless) {
        switch (browser.trim().toLowerCase()) {
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--remote-allow-origins=*", "--disable-notifications");
                return new ChromeDriver(options);
            }
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                return new FirefoxDriver(options);
            }
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                return new EdgeDriver(options);
            }
            default:
                throw new IllegalArgumentException(
                        "Unsupported browser: '" + browser + "'. Supported values: chrome, firefox, edge.");
        }
    }
}
