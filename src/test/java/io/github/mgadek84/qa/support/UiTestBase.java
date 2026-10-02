package io.github.mgadek84.qa.support;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.github.mgadek84.qa.config.TestConfig;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * One browser per test class and a fresh, isolated browser context per test, so cookies
 * and local storage (for example the saucedemo cart) never leak between tests.
 * On failure a screenshot and a Playwright trace are saved and attached to the Allure report.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(FailureArtifacts.class)
public abstract class UiTestBase {

    private static final Path ARTIFACTS_DIR = Paths.get("target", "failure-artifacts");

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    void launchBrowser() {
        playwright = PlaywrightFactory.create(BrowserFactory.usesBundledBrowser());
        playwright.selectors().setTestIdAttribute("data-test");
        browser = BrowserFactory.launch(playwright);
        PlaywrightAssertions.setDefaultAssertionTimeout(TestConfig.uiTimeoutMs());
    }

    @BeforeEach
    void openContext() {
        context = browser.newContext(BrowserFactory.contextOptions());
        context.setDefaultTimeout(TestConfig.uiTimeoutMs());
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    @AfterAll
    void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /** Called by {@link FailureArtifacts} before {@code @AfterEach} closes the context. */
    void finishTracing(boolean failed, String artifactName) {
        if (context == null) {
            return;
        }
        if (!failed) {
            context.tracing().stop();
            return;
        }
        Path screenshot = ARTIFACTS_DIR.resolve(artifactName + ".png");
        Path trace = ARTIFACTS_DIR.resolve(artifactName + "-trace.zip");
        byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true).setPath(screenshot));
        Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png), "png");
        context.tracing().stop(new Tracing.StopOptions().setPath(trace));
        try (InputStream zip = Files.newInputStream(trace)) {
            Allure.addAttachment("Playwright trace (open with: playwright show-trace)", "application/zip", zip, "zip");
        } catch (IOException e) {
            Allure.addAttachment("Playwright trace", "text/plain", "Trace saved to " + trace.toAbsolutePath());
        }
    }
}
