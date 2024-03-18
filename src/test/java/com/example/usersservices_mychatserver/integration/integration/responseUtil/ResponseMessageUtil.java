package com.example.usersservices_mychatserver.integration.integration.responseUtil;

public class ResponseMessageUtil {
    private static final String RESPONSE_NOT_AVAILABLE = "Response not available";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_ACCOUNT_NOT_ACTIVE = "Account not active";

    private static final String USER_ALREADY_EXIST = "User already exist";

    private static final String WRONG_RESET_PASSWORD_CODE = "Wrong reset password code";

    private static final String RESET_PASSWORD_CODE_NOT_FOUND = "Reset password code not found";

    public static String getWrongResetPasswordCode() {
        return WRONG_RESET_PASSWORD_CODE;
    }

    public static String getResetPasswordCodeNotFound() {
        return RESET_PASSWORD_CODE_NOT_FOUND;
    }

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
