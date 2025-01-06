package com.example.usersservices_mychatserver.exception.auth;

public class InactiveUserAccountException extends RuntimeException {
    public InactiveUserAccountException() {
        super();
    }

    public InactiveUserAccountException(String message) {
        super(message);
    }

    public InactiveUserAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InactiveUserAccountException(Throwable cause) {
        super(cause);
    }
}