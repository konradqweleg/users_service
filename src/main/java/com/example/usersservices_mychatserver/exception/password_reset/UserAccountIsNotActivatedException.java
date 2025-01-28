package com.example.usersservices_mychatserver.exception.password_reset;

public class UserAccountIsNotActivatedException extends RuntimeException {
    public UserAccountIsNotActivatedException() {
        super();
    }

    public UserAccountIsNotActivatedException(String message) {
        super(message);
    }

    public UserAccountIsNotActivatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAccountIsNotActivatedException(Throwable cause) {
        super(cause);
    }
}
