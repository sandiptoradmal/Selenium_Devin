package com.framework.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lazily creates and hands out a single, process-wide {@link ExtentReports} instance.
 * ExtentReports itself is thread-safe for concurrent test/node creation, which is what
 * makes it safe to share across parallel TestNG threads.
 */
public final class ExtentManager {

    private static volatile ExtentReports extentReports;

    private ExtentManager() {
        // Utility class — no instances.
    }

    public static ExtentReports getInstance() {
        if (extentReports == null) {
            synchronized (ExtentManager.class) {
                if (extentReports == null) {
                    extentReports = createInstance();
                }
            }
        }
        return extentReports;
    }

    private static ExtentReports createInstance() {
        String reportsDir = ConfigReader.get("reports.dir", "test-output/reports");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = reportsDir + "/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Automation Execution Report");
        sparkReporter.config().setReportName("Selenium WebDriver | TestNG Results");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));

        return extent;
    }
}
