package io.github.mgadek84.qa.data;

import java.util.List;
import java.util.Map;

/**
 * Generated from resources/PageObject/TestData/Reqres_TestData.py by tools/generate_java.py.
 * Do not edit by hand: change the Robot Framework source and regenerate.
 */
public final class ReqresTestData {

    public static final Map<String, Object> VALID_LOGIN = Map.of("email", "eve.holt@reqres.in", "password", "cityslicka");
    public static final Map<String, Object> LOGIN_WITHOUT_PASSWORD = Map.of("email", "peter@klaven");
    public static final Map<String, Object> LOGIN_UNKNOWN_USER = Map.of("email", "unknown.user@reqres.in", "password", "not-registered");
    public static final String MISSING_PASSWORD_ERROR = "Missing password";
    public static final String UNKNOWN_USER_ERROR = "user not found";
    public static final String INVALID_API_KEY = "invalid-api-key";
    public static final int USERS_PER_PAGE = 6;
    public static final int USERS_TOTAL = 12;
    public static final int USERS_TOTAL_PAGES = 2;
    public static final List<Integer> FIRST_PAGE_USER_IDS = List.of(1, 2, 3, 4, 5, 6);
    public static final List<Integer> SECOND_PAGE_USER_IDS = List.of(7, 8, 9, 10, 11, 12);
    public static final List<Integer> NO_USER_IDS = List.of();
    public static final Map<String, Object> EXISTING_USER = Map.of("id", 2, "email", "janet.weaver@reqres.in", "first_name", "Janet", "last_name", "Weaver");
    public static final int UNKNOWN_USER_ID = 23;
    public static final Map<String, Object> NEW_USER = Map.of("name", "morpheus", "job", "leader");
    public static final Map<String, Object> UPDATED_USER = Map.of("name", "morpheus", "job", "zion resident");
    public static final String ISO_TIMESTAMP_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";

    private ReqresTestData() {
    }
}
