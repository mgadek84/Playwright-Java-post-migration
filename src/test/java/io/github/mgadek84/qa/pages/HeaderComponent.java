package io.github.mgadek84.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Cart badge and side menu, present on every page after login.
 */
public class HeaderComponent extends BasePage {

    private final Locator cartBadge;
    private final Locator menuButton;
    private final Locator logoutLink;

    public HeaderComponent(Page page) {
        super(page);
        this.cartBadge = page.getByTestId("shopping-cart-badge");
        this.menuButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu"));
        this.logoutLink = page.getByTestId("logout-sidebar-link");
    }

    public HeaderComponent assertCartBadge(String expectedCount) {
        assertThat(cartBadge).isVisible();
        assertThat(cartBadge).hasText(expectedCount);
        return this;
    }

    public HeaderComponent assertNoCartBadge() {
        assertThat(cartBadge).hasCount(0);
        return this;
    }

    public void logOut() {
        menuButton.click();
        assertThat(logoutLink).isVisible();
        logoutLink.click();
    }
}
