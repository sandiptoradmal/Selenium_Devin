package com.framework.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures full-page screenshots on demand — primarily used by the failure-reporting listener,
 * but safe to call from anywhere a WebDriver instance is available.
 */
public final class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
        // Utility class — no instances.
    }

    /**
     * Takes a screenshot and writes it to {@code <screenshots.dir>/<testName>_<timestamp>.png}.
     *
     * @return the absolute path of the saved screenshot file, or {@code null} if capture failed.
     */
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            log.warn("Cannot capture screenshot for '{}' — WebDriver instance is null.", testName);
            return null;
        }

        String screenshotsDir = ConfigReader.get("screenshots.dir", "test-output/screenshots");
        String fileName = testName + "_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".png";

        try {
            Path targetDir = Paths.get(screenshotsDir);
            Files.createDirectories(targetDir);

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path target = targetDir.resolve(fileName);
            Files.copy(source.toPath(), target);

            log.info("Screenshot captured for '{}': {}", testName, target.toAbsolutePath());
            return target.toAbsolutePath().toString();
        } catch (IOException | ClassCastException e) {
            log.error("Failed to capture screenshot for test '{}'", testName, e);
            return null;
        }
    }
}
