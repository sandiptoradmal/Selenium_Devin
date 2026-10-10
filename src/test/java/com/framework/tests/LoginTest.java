package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pageobjects.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Sample test class demonstrating framework usage against https://www.saucedemo.com.
 * All assertions live here — the page object exposes only actions and raw state getters.
 */
public class LoginTest extends BaseTest {

    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    private static final String INVALID_PASSWORD = "wrong_password";

    @Test(description = "Verify a user with valid credentials can log in successfully")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);

        Assert.assertTrue(loginPage.isLogoutLinkDisplayed(),
                "Logout link should be visible after a successful login.");
    }

    @Test(description = "Verify an appropriate error message is shown for invalid credentials")
    public void testLoginWithInvalidCredentialsShowsError() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.loginAs(VALID_USERNAME, INVALID_PASSWORD);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "An error message should be displayed for invalid credentials.");
        Assert.assertTrue(loginPage.getErrorMessageText().toLowerCase().contains("username and password"),
                "Error message text did not match the expected validation message.");
    }

    @Test(description = "Verify login fails gracefully when the password field is left empty")
    public void testLoginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.enterUsername(VALID_USERNAME);
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "An error message should be displayed when the password field is empty.");
    }
}
