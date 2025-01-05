package com.example.usersservices_mychatserver.exception;

public class UnexpectedInternalException extends RuntimeException {
    public UnexpectedInternalException() {
        super();
    }

    public UnexpectedInternalException(String message) {
        super(message);
    }

    public UnexpectedInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedInternalException(Throwable cause) {
        super(cause);
    }
}