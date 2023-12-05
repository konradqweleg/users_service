package com.example.usersservices_mychatserver.integration.integration.responseUtil;

public class ResponseMessageUtil {
    private static final String RESPONSE_NOT_AVAILABLE = "Response not available";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_ACCOUNT_NOT_ACTIVE = "Account not active";

    private static final String USER_ALREADY_EXIST = "User already exist";

    public static String getResponseNotAvailable() {
        return RESPONSE_NOT_AVAILABLE;
    }

    public static String getUserAlreadyExist() {
        return USER_ALREADY_EXIST;
    }

    public static String getUserNotFound() {
        return USER_NOT_FOUND;
    }

    public static String getUserAccountNotActive() {
        return USER_ACCOUNT_NOT_ACTIVE;
    }
}
