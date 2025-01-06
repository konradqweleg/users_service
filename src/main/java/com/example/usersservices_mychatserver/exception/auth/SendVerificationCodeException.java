package com.example.usersservices_mychatserver.exception.auth;

public class SendVerificationCodeException extends RuntimeException {
    public SendVerificationCodeException() {
        super();
    }

    public SendVerificationCodeException(String message) {
        super(message);
    }

    public SendVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendVerificationCodeException(Throwable cause) {
        super(cause);
    }
}