package io.github.mgadek84.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.mgadek84.qa.config.TestConfig;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * saucedemo.com login page. Locators stay private; tests call actions and assertions only.
 */
public class LoginPage extends BasePage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        super(page);
        this.usernameInput = page.getByTestId("username");
        this.passwordInput = page.getByTestId("password");
        this.loginButton = page.getByTestId("login-button");
        this.errorMessage = page.getByTestId("error");
    }

    public LoginPage open() {
        navigateTo("/");
        return this;
    }

    public LoginPage assertOpen() {
        assertThat(loginButton).isVisible();
        assertThat(usernameInput).isVisible();
        assertThat(passwordInput).isVisible();
        return this;
    }

    public LoginPage logInAs(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
        return this;
    }

    public LoginPage logInAsStandardUser() {
        return logInAs(TestConfig.sauceUser(), TestConfig.saucePassword());
    }

    public LoginPage assertError(String expectedMessage) {
        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).hasText(expectedMessage);
        return this;
    }
}
