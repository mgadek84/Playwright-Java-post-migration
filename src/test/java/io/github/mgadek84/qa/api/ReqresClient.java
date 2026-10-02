package io.github.mgadek84.qa.api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import io.github.mgadek84.qa.config.TestConfig;
import io.github.mgadek84.qa.support.ApiSession;

import java.util.Map;

/**
 * Playwright client for the reqres.in REST API. Tests talk to this type, not to
 * {@link ApiSession} directly, so new endpoints are added here once and reused.
 */
public final class ReqresClient implements AutoCloseable {

    private final ApiSession session;

    public ReqresClient(Playwright playwright) {
        this(playwright, TestConfig.reqresApiKey());
    }

    public ReqresClient(Playwright playwright, String apiKey) {
        this.session = new ApiSession(
                playwright,
                TestConfig.reqresBaseUrl(),
                Map.of("Accept", "application/json", "x-api-key", apiKey),
                TestConfig.reqresTimeoutMs());
    }

    public APIResponse login(Map<String, Object> credentials) {
        return post("/login", credentials);
    }

    public APIResponse getUsersPage(int page) {
        return get("/users", RequestOptions.create().setQueryParam("page", page));
    }

    public APIResponse getUser(Object userId) {
        return get("/users/" + userId);
    }

    public APIResponse createUser(Object user) {
        return post("/users", user);
    }

    public APIResponse updateUser(Object userId, Object user) {
        return put("/users/" + userId, user);
    }

    public APIResponse deleteUser(Object userId) {
        return delete("/users/" + userId);
    }

    public APIResponse request(String method, String path) {
        return session.send(method, path);
    }

    public APIResponse get(String path) {
        return session.send("GET", path);
    }

    public APIResponse get(String path, RequestOptions options) {
        return session.send("GET", path, options);
    }

    public APIResponse post(String path, Object jsonBody) {
        return session.send("POST", path, jsonBody(jsonBody));
    }

    public APIResponse put(String path, Object jsonBody) {
        return session.send("PUT", path, jsonBody(jsonBody));
    }

    public APIResponse delete(String path) {
        return session.send("DELETE", path);
    }

    @Override
    public void close() {
        session.close();
    }

    private static RequestOptions jsonBody(Object body) {
        return RequestOptions.create().setData(body);
    }
}
