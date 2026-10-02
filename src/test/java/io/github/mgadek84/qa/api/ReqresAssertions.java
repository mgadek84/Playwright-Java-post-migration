package io.github.mgadek84.qa.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.APIResponse;
import io.github.mgadek84.qa.support.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.mgadek84.qa.data.ReqresTestData.ISO_TIMESTAMP_PATTERN;
import static io.github.mgadek84.qa.data.ReqresTestData.USERS_PER_PAGE;
import static io.github.mgadek84.qa.data.ReqresTestData.USERS_TOTAL;
import static io.github.mgadek84.qa.data.ReqresTestData.USERS_TOTAL_PAGES;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared assertions for reqres.in responses. Keep HTTP and JSON checks here so
 * test classes stay scenario-focused and every migrated RF check has one home.
 */
public final class ReqresAssertions {

    private ReqresAssertions() {
    }

    public static void assertStatus(APIResponse response, int expectedStatus) {
        assertThat(response.status())
                .as("HTTP status of %s", response.url())
                .isEqualTo(expectedStatus);
    }

    /** RF: Login Response Should Contain Token — {@code token} present and not empty. */
    public static void assertLoginToken(APIResponse response) {
        JsonNode body = Json.body(response);
        assertThat(body.has("token"))
                .as("login response should contain 'token'")
                .isTrue();
        assertThat(body.get("token").asText())
                .as("login token")
                .isNotBlank();
    }

    /** RF: Login Response Should Contain Error — {@code error} present, {@code token} absent. */
    public static void assertLoginError(APIResponse response, String expectedError) {
        JsonNode body = Json.body(response);
        assertFieldEquals(body, "error", expectedError);
        assertThat(body.has("token"))
                .as("failed login must not return a token")
                .isFalse();
    }

    public static void assertFieldEquals(JsonNode body, String field, String expectedValue) {
        assertThat(body.has(field))
                .as("JSON body should contain '%s'", field)
                .isTrue();
        assertThat(body.get(field).asText())
                .as("JSON field '%s'", field)
                .isEqualTo(expectedValue);
    }

    /** RF: Users Page Should Be — pagination metadata and the user ids on that page. */
    public static void assertUsersPage(APIResponse response, int page, List<Integer> expectedIds) {
        JsonNode body = Json.body(response);
        assertThat(intField(body, "page")).isEqualTo(page);
        assertThat(intField(body, "per_page")).isEqualTo(USERS_PER_PAGE);
        assertThat(intField(body, "total")).isEqualTo(USERS_TOTAL);
        assertThat(intField(body, "total_pages")).isEqualTo(USERS_TOTAL_PAGES);
        assertThat(body.has("data"))
                .as("users page should contain 'data'")
                .isTrue();
        assertThat(userIds(body)).containsExactlyElementsOf(expectedIds);
    }

    /** RF: User Response Should Match — {@code data} contains every expected user field. */
    public static void assertUserMatches(APIResponse response, Map<String, Object> expectedUser) {
        JsonNode data = Json.body(response).path("data");
        assertThat(data.isMissingNode())
                .as("user response should contain 'data'")
                .isFalse();
        assertContainsValues(data, expectedUser);
    }

    /** RF: Created User Response Should Match — echoed fields, a non-empty id, and {@code createdAt}. */
    public static void assertCreatedUser(APIResponse response, Map<String, Object> expectedUser) {
        JsonNode body = Json.body(response);
        assertContainsValues(body, expectedUser);
        assertThat(body.has("id"))
                .as("created user should contain 'id'")
                .isTrue();
        assertThat(body.get("id").asText())
                .as("created user id")
                .isNotBlank();
        assertTimestamp(body, "createdAt");
    }

    /** RF: Updated User Response Should Match — echoed fields and {@code updatedAt}. */
    public static void assertUpdatedUser(APIResponse response, Map<String, Object> expectedUser) {
        JsonNode body = Json.body(response);
        assertContainsValues(body, expectedUser);
        assertTimestamp(body, "updatedAt");
    }

    /** RF: Should Be Empty on a JSON body. */
    public static void assertJsonEmpty(APIResponse response) {
        assertThat(Json.body(response).isEmpty())
                .as("JSON body of %s", response.url())
                .isTrue();
    }

    /** RF: Should Be Empty on the raw response content. */
    public static void assertBodyEmpty(APIResponse response) {
        assertThat(response.body())
                .as("response body of %s", response.url())
                .isEmpty();
    }

    /** RF: Dictionary Should Contain Sub Dictionary. */
    public static void assertContainsValues(JsonNode body, Map<String, Object> expected) {
        for (Map.Entry<String, Object> entry : expected.entrySet()) {
            String field = entry.getKey();
            assertThat(body.has(field))
                    .as("JSON body should contain '%s'", field)
                    .isTrue();
            assertThat(body.get(field).asText())
                    .as("JSON field '%s'", field)
                    .isEqualTo(String.valueOf(entry.getValue()));
        }
    }

    private static void assertTimestamp(JsonNode body, String field) {
        assertThat(body.has(field))
                .as("JSON body should contain '%s'", field)
                .isTrue();
        assertThat(body.get(field).asText())
                .as("JSON field '%s'", field)
                .containsPattern(ISO_TIMESTAMP_PATTERN);
    }

    private static int intField(JsonNode body, String field) {
        assertThat(body.has(field))
                .as("JSON body should contain '%s'", field)
                .isTrue();
        return body.get(field).asInt();
    }

    private static List<Integer> userIds(JsonNode body) {
        List<Integer> ids = new ArrayList<>();
        for (JsonNode user : body.get("data")) {
            ids.add(user.get("id").asInt());
        }
        return ids;
    }
}
