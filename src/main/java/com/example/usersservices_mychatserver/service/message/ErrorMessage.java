package com.example.usersservices_mychatserver.service.message;

public enum ErrorMessage {
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    RESPONSE_NOT_AVAILABLE("Response not available"),

    CODE_NOT_FOUND_FOR_THIS_USER ("Code not found for this user"),

    BAD_CODE ("Bad code"),
    USER_OR_RESET_PASSWORD_CODE_NOT_FOUND ("User or reset password code not found"),

    BAD_CHANGE_PASSWORD_CODE ("Bad change password code");

    private String message;

    public String getMessage(){
        return message;
    }
    ErrorMessage(String message) {
        this.message = message;
    }
}
