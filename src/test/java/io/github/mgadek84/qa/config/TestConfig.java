package io.github.mgadek84.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Suite configuration. Each value is resolved from, in order: a JVM system property,
 * an environment variable with the same name, then {@code config.properties}.
 */
public final class TestConfig {

    private static final Properties DEFAULTS = loadDefaults();

    private TestConfig() {
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            value = DEFAULTS.getProperty(key);
        }
        if (value == null) {
            throw new IllegalStateException("Missing configuration value: " + key);
        }
        return value.trim();
    }

    public static String reqresBaseUrl() {
        return get("REQRES_BASE_URL");
    }

    public static String reqresApiKey() {
        return get("REQRES_API_KEY");
    }

    public static double reqresTimeoutMs() {
        return Double.parseDouble(get("REQRES_TIMEOUT")) * 1000;
    }

    public static String sauceDemoUrl() {
        return get("SAUCEDEMO_URL");
    }

    public static String sauceUser() {
        return get("SAUCE_USER");
    }

    public static String saucePassword() {
        return get("SAUCE_PASSWORD");
    }

    public static String uiBrowser() {
        return get("UI_BROWSER").toLowerCase(Locale.ROOT);
    }

    public static boolean headless() {
        return switch (get("HEADLESS").toLowerCase(Locale.ROOT)) {
            case "true", "1", "yes", "on" -> true;
            default -> false;
        };
    }

    /** UI timeout in milliseconds; accepts Robot Framework style values such as {@code 10s} or {@code 500ms}. */
    public static double uiTimeoutMs() {
        String value = get("UI_TIMEOUT").toLowerCase(Locale.ROOT);
        if (value.endsWith("ms")) {
            return Double.parseDouble(value.substring(0, value.length() - 2));
        }
        if (value.endsWith("s")) {
            return Double.parseDouble(value.substring(0, value.length() - 1)) * 1000;
        }
        return Double.parseDouble(value) * 1000;
    }

    private static Properties loadDefaults() {
        Properties properties = new Properties();
        try (InputStream stream = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read config.properties", e);
        }
        return properties;
    }
}
