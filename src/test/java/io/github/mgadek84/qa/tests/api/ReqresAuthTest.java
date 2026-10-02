package io.github.mgadek84.qa.tests.api;

import com.microsoft.playwright.APIResponse;
import io.github.mgadek84.qa.api.ReqresClient;
import io.github.mgadek84.qa.support.ApiTestBase;
import io.github.mgadek84.qa.support.RfSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static io.github.mgadek84.qa.api.ReqresAssertions.assertLoginError;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertLoginToken;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertStatus;
import static io.github.mgadek84.qa.data.ReqresTestData.EXISTING_USER;
import static io.github.mgadek84.qa.data.ReqresTestData.INVALID_API_KEY;
import static io.github.mgadek84.qa.data.ReqresTestData.LOGIN_UNKNOWN_USER;
import static io.github.mgadek84.qa.data.ReqresTestData.LOGIN_WITHOUT_PASSWORD;
import static io.github.mgadek84.qa.data.ReqresTestData.MISSING_PASSWORD_ERROR;
import static io.github.mgadek84.qa.data.ReqresTestData.UNKNOWN_USER_ERROR;
import static io.github.mgadek84.qa.data.ReqresTestData.VALID_LOGIN;

/**
 * reqres.in authentication: successful login, rejected logins and API key checks.
 */
@Tag("api")
@Tag("auth")
@Timeout(value = 1, unit = TimeUnit.MINUTES)
@DisplayName("Reqres Auth")
class ReqresAuthTest extends ApiTestBase {

    private ReqresClient reqres;

    @BeforeAll
    void createReqresSession() {
        reqres = new ReqresClient(playwright);
    }

    @AfterAll
    void deleteAllSessions() {
        if (reqres != null) {
            reqres.close();
        }
    }

    @Test
    @Tag("smoke")
    @DisplayName("Login With Valid Credentials Returns Token")
    @RfSource(suite = "tests/RF/API/Reqres_Auth.robot", test = "Login With Valid Credentials Returns Token")
    void loginWithValidCredentialsReturnsToken() {
        APIResponse response = reqres.login(VALID_LOGIN);
        assertStatus(response, 200);
        assertLoginToken(response);
    }

    @Test
    @Tag("negative")
    @DisplayName("Login Without Password Is Rejected")
    @RfSource(suite = "tests/RF/API/Reqres_Auth.robot", test = "Login Without Password Is Rejected")
    void loginWithoutPasswordIsRejected() {
        APIResponse response = reqres.login(LOGIN_WITHOUT_PASSWORD);
        assertStatus(response, 400);
        assertLoginError(response, MISSING_PASSWORD_ERROR);
    }

    @Test
    @Tag("negative")
    @DisplayName("Login With Unknown User Is Rejected")
    @RfSource(suite = "tests/RF/API/Reqres_Auth.robot", test = "Login With Unknown User Is Rejected")
    void loginWithUnknownUserIsRejected() {
        APIResponse response = reqres.login(LOGIN_UNKNOWN_USER);
        assertStatus(response, 400);
        assertLoginError(response, UNKNOWN_USER_ERROR);
    }

    @Test
    @Tag("negative")
    @DisplayName("Request With Invalid API Key Is Rejected")
    @RfSource(suite = "tests/RF/API/Reqres_Auth.robot", test = "Request With Invalid API Key Is Rejected")
    void requestWithInvalidApiKeyIsRejected() {
        try (ReqresClient invalidKey = new ReqresClient(playwright, INVALID_API_KEY)) {
            APIResponse response = invalidKey.getUser(EXISTING_USER.get("id"));
            assertStatus(response, 403);
        }
    }
}
