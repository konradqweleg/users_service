package com.example.usersservices_mychatserver.model;

public class Result<T> {
    private final T value;
    private final String errorMessage;

    private Result(T value, String error) {
        this.value = value;
        this.errorMessage = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(String error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return value != null;
    }

    public boolean isError() {
        return errorMessage != null;
    }

    public T getValue() {
        if (isSuccess()) {
            return value;
        } else {
            throw new IllegalStateException("Result does not contain a value");
        }
    }

    public String getError() {
        if (isError()) {
            return "{ \"ErrorMessage\" : \" "+errorMessage+" \" }";
        } else {
            throw new IllegalStateException("Result does not contain an error");
        }
    }
}