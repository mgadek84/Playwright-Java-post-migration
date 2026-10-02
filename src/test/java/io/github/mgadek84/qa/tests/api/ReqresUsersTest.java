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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.github.mgadek84.qa.api.ReqresAssertions.assertBodyEmpty;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertCreatedUser;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertJsonEmpty;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertStatus;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertUpdatedUser;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertUserMatches;
import static io.github.mgadek84.qa.api.ReqresAssertions.assertUsersPage;
import static io.github.mgadek84.qa.data.ReqresTestData.EXISTING_USER;
import static io.github.mgadek84.qa.data.ReqresTestData.FIRST_PAGE_USER_IDS;
import static io.github.mgadek84.qa.data.ReqresTestData.NEW_USER;
import static io.github.mgadek84.qa.data.ReqresTestData.NO_USER_IDS;
import static io.github.mgadek84.qa.data.ReqresTestData.SECOND_PAGE_USER_IDS;
import static io.github.mgadek84.qa.data.ReqresTestData.UNKNOWN_USER_ID;
import static io.github.mgadek84.qa.data.ReqresTestData.UPDATED_USER;

/**
 * reqres.in users: pagination, single user lookup, create, update, delete and status codes.
 */
@Tag("api")
@Tag("users")
@Timeout(value = 1, unit = TimeUnit.MINUTES)
@DisplayName("Reqres Users")
class ReqresUsersTest extends ApiTestBase {

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
    @DisplayName("List Users Returns First Page")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "List Users Returns First Page")
    void listUsersReturnsFirstPage() {
        APIResponse response = reqres.getUsersPage(1);
        assertStatus(response, 200);
        assertUsersPage(response, 1, FIRST_PAGE_USER_IDS);
    }

    @Test
    @DisplayName("List Users Returns Second Page")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "List Users Returns Second Page")
    void listUsersReturnsSecondPage() {
        APIResponse response = reqres.getUsersPage(2);
        assertStatus(response, 200);
        assertUsersPage(response, 2, SECOND_PAGE_USER_IDS);
    }

    @Test
    @DisplayName("List Users Beyond Last Page Returns No Users")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "List Users Beyond Last Page Returns No Users")
    void listUsersBeyondLastPageReturnsNoUsers() {
        APIResponse response = reqres.getUsersPage(3);
        assertStatus(response, 200);
        assertUsersPage(response, 3, NO_USER_IDS);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Get Single User Returns User Details")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "Get Single User Returns User Details")
    void getSingleUserReturnsUserDetails() {
        APIResponse response = reqres.getUser(EXISTING_USER.get("id"));
        assertStatus(response, 200);
        assertUserMatches(response, EXISTING_USER);
    }

    @Test
    @Tag("negative")
    @DisplayName("Get Unknown User Returns 404")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "Get Unknown User Returns 404")
    void getUnknownUserReturns404() {
        APIResponse response = reqres.getUser(UNKNOWN_USER_ID);
        assertStatus(response, 404);
        assertJsonEmpty(response);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Create User Returns 201 With Id")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "Create User Returns 201 With Id")
    void createUserReturns201WithId() {
        APIResponse response = reqres.createUser(NEW_USER);
        assertStatus(response, 201);
        assertCreatedUser(response, NEW_USER);
    }

    @Test
    @DisplayName("Update User Returns Updated Fields")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "Update User Returns Updated Fields")
    void updateUserReturnsUpdatedFields() {
        APIResponse response = reqres.updateUser(EXISTING_USER.get("id"), UPDATED_USER);
        assertStatus(response, 200);
        assertUpdatedUser(response, UPDATED_USER);
    }

    @Test
    @DisplayName("Delete User Returns 204 With Empty Body")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "Delete User Returns 204 With Empty Body")
    void deleteUserReturns204WithEmptyBody() {
        APIResponse response = reqres.deleteUser(EXISTING_USER.get("id"));
        assertStatus(response, 204);
        assertBodyEmpty(response);
    }

    @ParameterizedTest(name = "[{index}] {0} {1} {2}")
    @MethodSource("endpointsReturnExpectedStatusCodesRows")
    @Tag("status-codes")
    @DisplayName("Endpoints Return Expected Status Codes")
    @RfSource(suite = "tests/RF/API/Reqres_Users.robot", test = "Endpoints Return Expected Status Codes")
    void endpointsReturnExpectedStatusCodes(String method, String path, int expectedStatus) {
        assertStatus(reqres.request(method, path), expectedStatus);
    }

    static Stream<Arguments> endpointsReturnExpectedStatusCodesRows() {
        return Stream.of(
                Arguments.of("GET", "/users?page=1", 200),
                Arguments.of("GET", "/users/" + EXISTING_USER.get("id"), 200),
                Arguments.of("GET", "/users/" + UNKNOWN_USER_ID, 404),
                Arguments.of("GET", "/unknown/2", 200),
                Arguments.of("GET", "/unknown/" + UNKNOWN_USER_ID, 404),
                Arguments.of("DELETE", "/users/" + EXISTING_USER.get("id"), 204));
    }
}
