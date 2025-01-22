package com.example.usersservices_mychatserver.exception.activation;

public class UserToResendActiveAccountCodeNotExistsException extends RuntimeException {
    public UserToResendActiveAccountCodeNotExistsException() {
        super();
    }

    public UserToResendActiveAccountCodeNotExistsException(String message) {
        super(message);
    }

    public UserToResendActiveAccountCodeNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserToResendActiveAccountCodeNotExistsException(Throwable cause) {
        super(cause);
    }
}
