package io.github.mgadek84.qa.support;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;
import java.util.Set;

/**
 * HTTP session with a base URL and default headers, the Playwright counterpart of a
 * RequestsLibrary session. Idempotent requests are retried on transient statuses.
 */
public final class ApiSession implements AutoCloseable {

    private static final Set<Integer> RETRY_STATUSES = Set.of(429, 502, 503, 504);
    private static final Set<String> RETRY_METHODS = Set.of("GET", "PUT", "DELETE", "HEAD", "OPTIONS");
    private static final int MAX_RETRIES = 3;

    private final APIRequestContext request;

    public ApiSession(Playwright playwright, String baseUrl, Map<String, String> headers, double timeoutMs) {
        // Playwright resolves paths like a browser does: "users" is appended to ".../api/",
        // while "/users" would replace the "/api" path. Hence the trailing slash here and
        // the relative paths in send().
        this.request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
                .setExtraHTTPHeaders(headers)
                .setTimeout(timeoutMs));
    }

    public APIResponse send(String method, String path) {
        return send(method, path, RequestOptions.create());
    }

    public APIResponse send(String method, String path, RequestOptions options) {
        String url = path.startsWith("/") ? path.substring(1) : path;
        options.setMethod(method);
        APIResponse response = request.fetch(url, options);
        for (int attempt = 1; attempt <= MAX_RETRIES && shouldRetry(method, response); attempt++) {
            backOff(attempt);
            response = request.fetch(url, options);
        }
        return response;
    }

    @Override
    public void close() {
        request.dispose();
    }

    private static boolean shouldRetry(String method, APIResponse response) {
        return RETRY_METHODS.contains(method) && RETRY_STATUSES.contains(response.status());
    }

    private static void backOff(int attempt) {
        try {
            Thread.sleep(1000L << (attempt - 1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting to retry", e);
        }
    }
}
