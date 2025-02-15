package com.example.usersservices_mychatserver.exception.password_reset;

public class UserAccountIsNotActivatedException extends RuntimeException {
    public UserAccountIsNotActivatedException(String message) {
        super(message);
    }

}
