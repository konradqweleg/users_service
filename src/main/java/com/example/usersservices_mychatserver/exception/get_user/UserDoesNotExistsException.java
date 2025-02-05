package com.example.usersservices_mychatserver.exception.get_user;

public class UserDoesNotExistsException extends RuntimeException {
    public UserDoesNotExistsException() {
        super();
    }

    public UserDoesNotExistsException(String message) {
        super(message);
    }

    public UserDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDoesNotExistsException(Throwable cause) {
        super(cause);
    }
}