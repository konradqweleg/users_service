package com.example.usersservices_mychatserver.exception.password_reset;

public class UserToResetPasswordDoesNotExistsException extends RuntimeException {
    public UserToResetPasswordDoesNotExistsException(String message) {
        super(message);
    }

}