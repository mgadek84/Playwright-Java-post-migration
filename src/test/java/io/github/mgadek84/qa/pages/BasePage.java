package io.github.mgadek84.qa.pages;

import com.microsoft.playwright.Page;
import io.github.mgadek84.qa.config.TestConfig;

/** Base class for saucedemo.com page objects. */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /** Opens {@code path} relative to {@code SAUCEDEMO_URL}. */
    protected void navigateTo(String path) {
        String base = TestConfig.sauceDemoUrl();
        String relative = path.startsWith("/") ? path.substring(1) : path;
        page.navigate((base.endsWith("/") ? base : base + "/") + relative);
    }
}
