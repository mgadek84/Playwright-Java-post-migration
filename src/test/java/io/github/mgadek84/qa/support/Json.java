package io.github.mgadek84.qa.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class Json {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Json() {
    }

    /** Parses the response body; an empty body becomes a missing node. */
    public static JsonNode body(APIResponse response) {
        try {
            return MAPPER.readTree(response.body());
        } catch (IOException e) {
            throw new UncheckedIOException("Response body is not JSON: " + response.text(), e);
        }
    }
}
