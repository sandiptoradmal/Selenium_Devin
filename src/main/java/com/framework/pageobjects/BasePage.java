package com.framework.pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Parent class for all page objects.
 * <p>
 * Wraps every interaction in an explicit {@link WebDriverWait} so individual page objects
 * never need to manage synchronization themselves. This is the single place responsible
 * for eliminating flakiness caused by timing issues (elements not yet clickable/visible/present).
 * <p>
 * Deliberately avoids the PageFactory / @FindBy model in favor of locators resolved lazily
 * on every call, which plays more safely with dynamic/SPA-style pages.
 */
public abstract class BasePage {

    private static final Logger log = LogManager.getLogger(BasePage.class);
    private static final Duration DEFAULT_EXPLICIT_WAIT = Duration.ofSeconds(15);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_EXPLICIT_WAIT);
    }

    /**
     * Waits for the element to be visible and returns it.
     */
    protected WebElement waitForVisibility(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element not visible within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Waits for the element to be clickable and returns it.
     */
    protected WebElement waitForClickability(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            log.error("Element not clickable within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Clicks an element once it is clickable. Retries once on a stale element reference,
     * which is common right after a page/DOM re-render.
     */
    protected void click(By locator) {
        try {
            waitForClickability(locator).click();
            log.debug("Clicked element: {}", locator);
        } catch (StaleElementReferenceException e) {
            log.warn("Stale element on click, retrying once: {}", locator);
            waitForClickability(locator).click();
        }
    }

    /**
     * Clears the field and types the given text once the element is visible.
     */
    protected void sendKeys(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element: {}", text, locator);
    }

    /**
     * Returns the visible text of an element, waiting for visibility first.
     */
    protected String getText(By locator) {
        String text = waitForVisibility(locator).getText();
        log.debug("Read text '{}' from element: {}", text, locator);
        return text;
    }

    /**
     * Returns true if the element becomes visible within the timeout, false otherwise
     * (never throws — safe to use for conditional assertions/branching).
     */
    protected boolean isVisible(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits until the element is no longer present/visible in the DOM.
     */
    protected boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits for the page <title> to change to the expected value — useful as a lightweight
     * "page has loaded" signal after a navigation.
     */
    protected void waitForTitleContains(String partialTitle) {
        wait.until(ExpectedConditions.titleContains(partialTitle));
    }
    
    protected void openURL()
    {
    	driver.get(null);
    }
}
