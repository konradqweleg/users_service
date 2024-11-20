package com.example.usersservices_mychatserver.exception;

public class UnexpectedError extends RuntimeException {
    public UnexpectedError() {
        super();
    }

    public UnexpectedError(String message) {
        super(message);
    }

    public UnexpectedError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedError(Throwable cause) {
        super(cause);
    }
}