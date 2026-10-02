package io.github.mgadek84.qa.support;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import io.github.mgadek84.qa.config.TestConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Launches the browser selected by {@code UI_BROWSER}, headed or headless per {@code HEADLESS}.
 * {@code chrome} and {@code msedge} drive the browsers installed on the machine; {@code chromium},
 * {@code firefox} and {@code webkit} use Playwright's bundled builds, downloaded on first use.
 */
public final class BrowserFactory {

    private static final int HEADLESS_WIDTH = 1920;
    private static final int HEADLESS_HEIGHT = 1080;
    private static final List<String> CHROMIUM_FAMILY = List.of("chromium", "chrome", "msedge");

    private BrowserFactory() {
    }

    public static Browser launch(Playwright playwright) {
        String browser = browserName();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(TestConfig.headless());
        return switch (browser) {
            case "chromium" -> playwright.chromium().launch(chromiumArgs(options));
            case "chrome", "msedge" -> playwright.chromium().launch(chromiumArgs(options).setChannel(browser));
            case "firefox" -> playwright.firefox().launch(options);
            case "webkit" -> playwright.webkit().launch(options);
            default -> throw new IllegalArgumentException(
                    "Unsupported UI_BROWSER '" + browser + "'; use chrome, msedge, chromium, firefox or webkit");
        };
    }

    public static boolean usesBundledBrowser() {
        return List.of("chromium", "firefox", "webkit").contains(browserName());
    }

    public static Browser.NewContextOptions contextOptions() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        if (!TestConfig.headless() && CHROMIUM_FAMILY.contains(browserName())) {
            // A null viewport lets the page follow the maximized window size.
            return options.setViewportSize(null);
        }
        return options.setViewportSize(HEADLESS_WIDTH, HEADLESS_HEIGHT);
    }

    private static String browserName() {
        String browser = TestConfig.uiBrowser();
        return "edge".equals(browser) ? "msedge" : browser;
    }

    private static BrowserType.LaunchOptions chromiumArgs(BrowserType.LaunchOptions options) {
        List<String> args = new ArrayList<>();
        args.add("--disable-search-engine-choice-screen");
        args.add("--disable-features=PasswordLeakDetection,PasswordManagerOnboarding");
        if (!TestConfig.headless()) {
            args.add("--start-maximized");
        }
        return options.setArgs(args);
    }
}
