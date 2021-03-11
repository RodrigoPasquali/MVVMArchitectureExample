package com.example.mvvmarchitectureexample.repository;

public class Resource<T> {
    private final RequestStatus requestStatus;
    private final T data;
    private final String message;

    public Resource(RequestStatus requestStatus, T data, String message) {
        this.requestStatus = requestStatus;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(RequestStatus.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(T data, String message) {
        return new Resource<>(RequestStatus.ERROR, data, message);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(RequestStatus.LOADING, data, null);
    }
}
