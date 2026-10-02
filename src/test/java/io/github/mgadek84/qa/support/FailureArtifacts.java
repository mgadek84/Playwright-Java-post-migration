package io.github.mgadek84.qa.support;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;

/**
 * Captures a screenshot and trace when a UI test, or its {@code @BeforeEach} setup, fails.
 * Runs before {@code @AfterEach}, while the page is still open.
 */
public class FailureArtifacts implements AfterTestExecutionCallback, LifecycleMethodExecutionExceptionHandler {

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        uiTest(extensionContext).finishTracing(extensionContext.getExecutionException().isPresent(),
                artifactName(extensionContext));
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext extensionContext, Throwable throwable)
            throws Throwable {
        uiTest(extensionContext).finishTracing(true, artifactName(extensionContext));
        throw throwable;
    }

    private static UiTestBase uiTest(ExtensionContext extensionContext) {
        return (UiTestBase) extensionContext.getRequiredTestInstance();
    }

    private static String artifactName(ExtensionContext extensionContext) {
        String name = extensionContext.getRequiredTestClass().getSimpleName() + "-"
                + extensionContext.getRequiredTestMethod().getName() + "-" + extensionContext.getDisplayName();
        String safe = name.replaceAll("[^A-Za-z0-9._-]+", "_");
        return safe.length() > 120 ? safe.substring(0, 120) : safe;
    }
}
