package com.example.usersservices_mychatserver.exception;

public class SaveDataInRepositoryException extends RuntimeException {
    public SaveDataInRepositoryException() {
        super();
    }

    public SaveDataInRepositoryException(String message) {
        super(message);
    }

    public SaveDataInRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveDataInRepositoryException(Throwable cause) {
        super(cause);
    }
}