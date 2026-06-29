package org.ibs.productservice.dto;

import lombok.Getter;

public class ApiResponse<T> {
    @Getter
    private String message;
    private final T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public T getData() { return data; }
}