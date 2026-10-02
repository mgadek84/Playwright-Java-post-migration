package io.github.mgadek84.qa.tests.ui;

import io.github.mgadek84.qa.pages.HeaderComponent;
import io.github.mgadek84.qa.pages.InventoryPage;
import io.github.mgadek84.qa.pages.LoginPage;
import io.github.mgadek84.qa.support.RfSource;
import io.github.mgadek84.qa.support.UiTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.github.mgadek84.qa.data.SauceDemoTestData.BACKPACK;
import static io.github.mgadek84.qa.data.SauceDemoTestData.BIKE_LIGHT;
import static io.github.mgadek84.qa.data.SauceDemoTestData.DEFAULT_SORT_OPTION;
import static io.github.mgadek84.qa.data.SauceDemoTestData.EXPECTED_PRODUCT_COUNT;
import static io.github.mgadek84.qa.data.SauceDemoTestData.EXPECTED_PRODUCT_NAMES;

/**
 * saucedemo.com product list, sorting and cart badge, as the standard user.
 */
@Tag("inventory")
@Tag("ui")
@Timeout(value = 2, unit = TimeUnit.MINUTES)
@DisplayName("SauceDemo Inventory")
class SauceDemoInventoryTest extends UiTestBase {

    private InventoryPage inventory;
    private HeaderComponent header;

    @BeforeEach
    void openSauceDemoAndLogIn() {
        new LoginPage(page).open().logInAsStandardUser();
        inventory = new InventoryPage(page);
        header = new HeaderComponent(page);
        inventory.assertOpen();
    }

    @ParameterizedTest(name = "[{index}] {0} {1} {2}")
    @MethodSource("productsCanBeSortedRows")
    @DisplayName("Products Can Be Sorted")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Inventory.robot", test = "Products Can Be Sorted")
    void productsCanBeSorted(String optionLabel, String field, String direction) {
        inventory.sortBy(optionLabel).assertOrderedBy(field, direction);
    }

    static Stream<Arguments> productsCanBeSortedRows() {
        return Stream.of(
                Arguments.of("Name (Z to A)", "name", "descending"),
                Arguments.of("Name (A to Z)", "name", "ascending"),
                Arguments.of("Price (low to high)", "price", "ascending"),
                Arguments.of("Price (high to low)", "price", "descending"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Product List Shows All Products")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Inventory.robot", test = "Product List Shows All Products")
    void productListShowsAllProducts() {
        inventory.assertProductCount(EXPECTED_PRODUCT_COUNT);
        inventory.assertProductNames(EXPECTED_PRODUCT_NAMES);
        inventory.assertActiveSortOption(DEFAULT_SORT_OPTION);
    }

    @Test
    @Tag("smoke")
    @Tag("cart")
    @DisplayName("Adding Products Updates Cart Badge")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Inventory.robot", test = "Adding Products Updates Cart Badge")
    void addingProductsUpdatesCartBadge() {
        header.assertNoCartBadge();
        inventory.addToCart(BACKPACK);
        header.assertCartBadge("1");
        inventory.addToCart(BIKE_LIGHT);
        header.assertCartBadge("2");
    }

    @Test
    @Tag("cart")
    @DisplayName("Removing Product Updates Cart Badge")
    @RfSource(suite = "tests/RF/UI/SauceDemo_Inventory.robot", test = "Removing Product Updates Cart Badge")
    void removingProductUpdatesCartBadge() {
        inventory.addToCart(BACKPACK);
        inventory.addToCart(BIKE_LIGHT);
        header.assertCartBadge("2");
        inventory.removeFromCart(BACKPACK);
        header.assertCartBadge("1");
        inventory.removeFromCart(BIKE_LIGHT);
        header.assertNoCartBadge();
    }
}
