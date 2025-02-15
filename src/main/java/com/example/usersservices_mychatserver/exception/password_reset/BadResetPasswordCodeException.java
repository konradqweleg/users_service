package com.example.usersservices_mychatserver.exception.password_reset;

public class BadResetPasswordCodeException extends RuntimeException {
    public BadResetPasswordCodeException(String message) {
        super(message);
    }

}
