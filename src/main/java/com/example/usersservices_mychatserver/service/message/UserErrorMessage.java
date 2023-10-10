package com.example.usersservices_mychatserver.service.message;

public enum UserErrorMessage {
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists");
    private String message;

    public String getMessage(){
        return message;
    }
    UserErrorMessage(String message) {
        this.message = message;
    }
}
