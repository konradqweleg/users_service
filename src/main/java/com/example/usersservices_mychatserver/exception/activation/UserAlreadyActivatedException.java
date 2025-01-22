package com.example.usersservices_mychatserver.exception.activation;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
        super();
    }

    public UserAlreadyActivatedException(String message) {
        super(message);
    }

    public UserAlreadyActivatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyActivatedException(Throwable cause) {
        super(cause);
    }
}
