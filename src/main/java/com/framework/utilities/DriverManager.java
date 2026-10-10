package com.framework.utilities;

import org.openqa.selenium.WebDriver;

/**
 * Holds the active {@link WebDriver} instance for the current thread.
 * <p>
 * Centralizing the {@link ThreadLocal} here (rather than as a private field on {@code BaseTest})
 * means any class that needs the driver for the currently-running test — most notably
 * {@code TestListener}, which runs outside the test class hierarchy — can fetch it through a
 * plain public method call instead of reflection. Reflection against a non-public method is
 * fragile: it breaks with {@code NoSuchMethodException} if the method signature changes,
 * {@code IllegalAccessException}/{@code InaccessibleObjectException} depending on JVM/module
 * settings, and it silently fails for any test class that doesn't extend {@code BaseTest}
 * directly. A shared, public accessor avoids all of that.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
        // Utility class — no instances.
    }

    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER_THREAD_LOCAL.set(driver);
    }

    /**
     * Quits the current thread's driver (if any) and clears the ThreadLocal slot.
     * Always call this in {@code @AfterMethod} to prevent leaking WebDriver instances
     * or state across TestNG's reused thread pool.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }
}
