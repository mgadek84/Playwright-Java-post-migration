package io.github.mgadek84.qa.data;

import java.util.List;
import java.util.Map;

/**
 * Generated from resources/PageObject/TestData/SauceDemo_TestData.py by tools/generate_java.py.
 * Do not edit by hand: change the Robot Framework source and regenerate.
 */
public final class SauceDemoTestData {

    public static final String LOCKED_OUT_USER = "locked_out_user";
    public static final String UNKNOWN_USER = "unknown_user";
    public static final String WRONG_PASSWORD = "wrong_password";
    public static final String LOCKED_OUT_ERROR = "Epic sadface: Sorry, this user has been locked out.";
    public static final String INVALID_CREDENTIALS_ERROR = "Epic sadface: Username and password do not match any user in this service";
    public static final String USERNAME_REQUIRED_ERROR = "Epic sadface: Username is required";
    public static final String PASSWORD_REQUIRED_ERROR = "Epic sadface: Password is required";
    public static final String LOGIN_REQUIRED_ERROR = "Epic sadface: You can only access '/inventory.html' when you are logged in.";
    public static final String INVENTORY_TITLE = "Products";
    public static final String INVENTORY_PATH = "/inventory.html";
    public static final String DEFAULT_SORT_OPTION = "Name (A to Z)";
    public static final int EXPECTED_PRODUCT_COUNT = 6;
    public static final List<String> EXPECTED_PRODUCT_NAMES = List.of("Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket", "Sauce Labs Onesie", "Test.allTheThings() T-Shirt (Red)");
    public static final String BACKPACK = "Sauce Labs Backpack";
    public static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    private SauceDemoTestData() {
    }
}
