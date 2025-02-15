package com.example.usersservices_mychatserver.exception.password_reset;

public class BadResetPasswordCodeException extends RuntimeException {
    public BadResetPasswordCodeException() {
        super();
    }

    public BadResetPasswordCodeException(String message) {
        super(message);
    }

    public BadResetPasswordCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadResetPasswordCodeException(Throwable cause) {
        super(cause);
    }
}
