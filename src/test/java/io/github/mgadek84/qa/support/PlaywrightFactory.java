package io.github.mgadek84.qa.support;

import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates Playwright instances. Playwright for Java downloads all bundled browsers (about 1 GB)
 * on first use; that is skipped unless the run actually needs a bundled browser.
 */
public final class PlaywrightFactory {

    private PlaywrightFactory() {
    }

    public static Playwright create(boolean needsBundledBrowsers) {
        Map<String, String> env = new HashMap<>(System.getenv());
        if (!needsBundledBrowsers) {
            env.putIfAbsent("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
        }
        return Playwright.create(new Playwright.CreateOptions().setEnv(env));
    }
}
