package io.github.mgadek84.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static io.github.mgadek84.qa.data.SauceDemoTestData.INVENTORY_PATH;
import static io.github.mgadek84.qa.data.SauceDemoTestData.INVENTORY_TITLE;

/**
 * saucedemo.com products page. Product cards are resolved by visible name, never by XPath.
 */
public class InventoryPage extends BasePage {

    private final Locator titleLabel;
    private final Locator inventoryItem;
    private final Locator itemName;
    private final Locator itemPrice;
    private final Locator sortSelect;
    private final Locator activeSortOption;

    public InventoryPage(Page page) {
        super(page);
        this.titleLabel = page.getByTestId("title");
        this.inventoryItem = page.getByTestId("inventory-item");
        this.itemName = page.getByTestId("inventory-item-name");
        this.itemPrice = page.getByTestId("inventory-item-price");
        this.sortSelect = page.getByTestId("product-sort-container");
        this.activeSortOption = page.getByTestId("active-option");
    }

    public InventoryPage open() {
        navigateTo(INVENTORY_PATH);
        return this;
    }

    public InventoryPage assertOpen() {
        assertThat(titleLabel).isVisible();
        assertThat(titleLabel).hasText(INVENTORY_TITLE);
        String inventoryPath = INVENTORY_PATH.startsWith("/") ? INVENTORY_PATH.substring(1) : INVENTORY_PATH;
        assertThat(page).hasURL(Pattern.compile(".*" + inventoryPath.replace(".", "\\.") + ".*"));
        return this;
    }

    public InventoryPage assertProductCount(int expectedCount) {
        assertThat(inventoryItem).hasCount(expectedCount);
        return this;
    }

    public InventoryPage assertProductNames(List<String> expectedNames) {
        assertThat(itemName).hasText(expectedNames.toArray(String[]::new));
        return this;
    }

    public InventoryPage assertActiveSortOption(String optionLabel) {
        assertThat(activeSortOption).hasText(optionLabel);
        return this;
    }

    /** Selects {@code optionLabel} and checks the visible active sort label. */
    public InventoryPage sortBy(String optionLabel) {
        sortSelect.selectOption(new SelectOption().setLabel(optionLabel));
        assertThat(activeSortOption).hasText(optionLabel);
        return this;
    }

    /**
     * Checks the products currently on screen are ordered by {@code field}
     * ({@code name} or {@code price}) in {@code direction} ({@code ascending} or {@code descending}).
     */
    public InventoryPage assertOrderedBy(String field, String direction) {
        boolean descending = "descending".equals(direction);
        if ("price".equals(field)) {
            List<Double> values = productPrices();
            List<Double> expected = new ArrayList<>(values);
            expected.sort(descending ? Comparator.reverseOrder() : Comparator.naturalOrder());
            org.assertj.core.api.Assertions.assertThat(values).containsExactlyElementsOf(expected);
        } else {
            List<String> values = productNames();
            List<String> expected = new ArrayList<>(values);
            expected.sort(descending ? Comparator.reverseOrder() : Comparator.naturalOrder());
            org.assertj.core.api.Assertions.assertThat(values).containsExactlyElementsOf(expected);
        }
        return this;
    }

    public InventoryPage addToCart(String productName) {
        Locator button = productButton(productName);
        assertThat(button).hasText("Add to cart");
        button.click();
        assertThat(button).containsText("Remove");
        return this;
    }

    public InventoryPage removeFromCart(String productName) {
        Locator button = productButton(productName);
        assertThat(button).hasText("Remove");
        button.click();
        assertThat(button).containsText("Add to cart");
        return this;
    }

    private List<String> productNames() {
        return itemName.allTextContents();
    }

    private List<Double> productPrices() {
        return itemPrice.allTextContents().stream()
                .map(text -> Double.parseDouble(text.startsWith("$") ? text.substring(1) : text))
                .toList();
    }

    /** Add/Remove button on the card whose name matches {@code productName} exactly. */
    private Locator productButton(String productName) {
        return productCard(productName).locator("button");
    }

    private Locator productCard(String productName) {
        return inventoryItem.filter(new Locator.FilterOptions()
                .setHas(itemName.getByText(productName, new Locator.GetByTextOptions().setExact(true))));
    }
}
