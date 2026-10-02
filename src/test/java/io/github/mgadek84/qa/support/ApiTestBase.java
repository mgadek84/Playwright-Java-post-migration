package io.github.mgadek84.qa.support;

import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

/** One Playwright instance per API test class; subclasses open their API sessions in {@code @BeforeAll}. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ApiTestBase {

    protected Playwright playwright;

    @BeforeAll
    void startPlaywright() {
        playwright = PlaywrightFactory.create(false);
    }

    @AfterAll
    void stopPlaywright() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
