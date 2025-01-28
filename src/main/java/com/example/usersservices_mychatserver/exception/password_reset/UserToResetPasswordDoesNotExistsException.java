package com.example.usersservices_mychatserver.exception.password_reset;

public class UserToResetPasswordDoesNotExistsException extends RuntimeException {
    public UserToResetPasswordDoesNotExistsException() {
        super();
    }

    public UserToResetPasswordDoesNotExistsException(String message) {
        super(message);
    }

    public UserToResetPasswordDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserToResetPasswordDoesNotExistsException(Throwable cause) {
        super(cause);
    }
}