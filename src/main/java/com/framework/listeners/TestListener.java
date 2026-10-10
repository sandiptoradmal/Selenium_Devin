package com.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.framework.utilities.DriverManager;
import com.framework.utilities.ExtentManager;
import com.framework.utilities.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG {@link ITestListener} that:
 * <ul>
 *     <li>Creates/updates an Extent test node for every test method.</li>
 *     <li>Captures a screenshot on failure and embeds it into the HTML report.</li>
 *     <li>Logs pass/fail/skip status with the underlying exception, when present.</li>
 * </ul>
 * Registered declaratively via {@code <listener class-name="com.framework.listeners.TestListener"/>}
 * in testng.xml so it applies to every test in the suite without per-class annotations.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    // ExtentTest, like the WebDriver, must be isolated per parallel thread.
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private final ExtentReports extentReports = ExtentManager.getInstance();

    @Override
    public void onStart(ITestContext context) {
        log.info("=== Test Suite Started: {} ===", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extentReports.createTest(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        extentTestThreadLocal.set(test);
        log.info("Test started: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTestThreadLocal.get().log(Status.PASS, "Test passed.");
        log.info("Test passed: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest extentTest = extentTestThreadLocal.get();
        extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable());

        WebDriver driver = DriverManager.getDriver();
        String screenshotPath = ScreenshotUtil.capture(driver, testName);

        if (screenshotPath != null) {
            try {
                extentTest.fail("Screenshot on failure:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                log.error("Failed to attach screenshot to Extent report for '{}'", testName, e);
            }
        }

        log.error("Test failed: {}", testName, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTestThreadLocal.get().log(Status.SKIP, "Test skipped: " + result.getThrowable());
        log.warn("Test skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        extentReports.flush();
        log.info("=== Test Suite Finished: {} ===", context.getName());
    }
}
