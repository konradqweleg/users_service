package com.example.usersservices_mychatserver.exception.get_user;

public class UserDoesNotExistsException extends RuntimeException {

    public UserDoesNotExistsException(String message) {
        super(message);
    }

}