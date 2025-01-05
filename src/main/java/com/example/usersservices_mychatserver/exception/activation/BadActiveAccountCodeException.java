package com.example.usersservices_mychatserver.exception.activation;

public class BadActiveAccountCodeException extends RuntimeException {
    public BadActiveAccountCodeException() {
        super();
    }

    public BadActiveAccountCodeException(String message) {
        super(message);
    }

    public BadActiveAccountCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadActiveAccountCodeException(Throwable cause) {
        super(cause);
    }
}
