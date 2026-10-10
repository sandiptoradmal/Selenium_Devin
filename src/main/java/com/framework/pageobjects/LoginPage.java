package com.framework.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Login page.
 * <p>
 * Locators are private and resolved lazily (no PageFactory/@FindBy) so this class has
 * no hidden dependency on WebElement caching. Every public method represents a single
 * user-facing action or a piece of state to read — never an assertion. Assertions belong
 * exclusively in the test layer.
 */
public class LoginPage extends BasePage {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error-message']");
    private final By logoutLink = By.id("logout-link");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String username) {
        sendKeys(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        sendKeys(passwordField, password);
        return this;
    }

    public void clickLogin() {
        click(loginButton);
    }

    /**
     * Convenience method composing the three individual steps for the common
     * "happy path" login flow.
     */
    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessageText() {
        return getText(errorMessage);
    }

    public boolean isErrorMessageDisplayed() {
        return isVisible(errorMessage);
    }

    public boolean isLogoutLinkDisplayed() {
        return isVisible(logoutLink);
    }
}
