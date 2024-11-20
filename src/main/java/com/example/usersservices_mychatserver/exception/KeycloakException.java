package com.example.usersservices_mychatserver.exception;

public class KeycloakException extends RuntimeException {
    public KeycloakException() {
        super();
    }

    public KeycloakException(String message) {
        super(message);
    }

    public KeycloakException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakException(Throwable cause) {
        super(cause);
    }
}