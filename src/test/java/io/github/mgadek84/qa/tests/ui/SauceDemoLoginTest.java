package io.github.mgadek84.qa.tests.ui;

import io.github.mgadek84.qa.config.TestConfig;
import io.github.mgadek84.qa.pages.HeaderComponent;
import io.github.mgadek84.qa.pages.InventoryPage;
import io.github.mgadek84.qa.pages.LoginPage;
import io.github.mgadek84.qa.support.RfSource;
import io.github.mgadek84.qa.support.UiTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.github.mgadek84.qa.data.SauceDemoTestData.INVALID_CREDENTIALS_ERROR;
import static io.github.mgadek84.qa.data.SauceDemoTestData.LOCKED_OUT_ERROR;
import static io.github.mgadek84.qa.data.SauceDemoTestData.LOCKED_OUT_USER;
import static io.github.mgadek84.qa.data.SauceDemoTestData.LOGIN_REQUIRED_ERROR;
import static io.github.mgadek84.qa.data.SauceDemoTestData.PASSWORD_REQUIRED_ERROR;
import static io.github.mgadek84.qa.data.SauceDemoTestData.UNKNOWN_USER;
import static io.github.mgadek84.qa.data.SauceDemoTestData.USERNAME_REQUIRED_ERROR;
import static io.github.mgadek84.qa.data.SauceDemoTestData.WRONG_PASSWORD;

/**
 * saucedemo.com login and logout.
 */
@Tag("login")
@Tag("ui")
@Timeout(value = 2, unit = TimeUnit.MINUTES)
@DisplayName("SauceDemo Login")
class SauceDemoLoginTest extends UiTestBase {

    private LoginPage login;
    private InventoryPage inventory;
    private HeaderComponent header;

    @BeforeEach
    void openSauceDemo() {
        login = new LoginPage(page).open();
        inventory = new InventoryPage(page);
        header = new HeaderComponent(page);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Valid Login Opens Products Page")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Login.robot", test = "Valid Login Opens Products Page")
    void validLoginOpensProductsPage() {
        login.logInAsStandardUser();
        inventory.assertOpen();
    }

    @Test
    @Tag("negative")
    @DisplayName("Locked Out User Cannot Log In")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Login.robot", test = "Locked Out User Cannot Log In")
    void lockedOutUserCannotLogIn() {
        login.logInAs(LOCKED_OUT_USER, TestConfig.saucePassword())
                .assertError(LOCKED_OUT_ERROR)
                .assertOpen();
    }

    @ParameterizedTest(name = "[{index}] {0} {1} {2}")
    @MethodSource("invalidCredentialsAreRejectedRows")
    @Tag("negative")
    @DisplayName("Invalid Credentials Are Rejected")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Login.robot", test = "Invalid Credentials Are Rejected")
    void invalidCredentialsAreRejected(String username, String password, String expectedError) {
        login.open()
                .assertOpen()
                .logInAs(username, password)
                .assertError(expectedError)
                .assertOpen();
    }

    static Stream<Arguments> invalidCredentialsAreRejectedRows() {
        return Stream.of(
                Arguments.of(TestConfig.sauceUser(), WRONG_PASSWORD, INVALID_CREDENTIALS_ERROR),
                Arguments.of(UNKNOWN_USER, TestConfig.saucePassword(), INVALID_CREDENTIALS_ERROR),
                Arguments.of("", TestConfig.saucePassword(), USERNAME_REQUIRED_ERROR),
                Arguments.of(TestConfig.sauceUser(), "", PASSWORD_REQUIRED_ERROR));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Logout Returns To Login Page")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Login.robot", test = "Logout Returns To Login Page")
    void logoutReturnsToLoginPage() {
        login.logInAsStandardUser();
        inventory.assertOpen();
        header.logOut();
        login.assertOpen();
        inventory.open();
        login.assertError(LOGIN_REQUIRED_ERROR);
    }
}
