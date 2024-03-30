package com.javaspringboot.javaspringbootcore.core.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiError apiError;

    public ApiException(ApiError apiError) {
        super(apiError.getMessage());
        this.apiError = apiError;
    }

    public int getErrorCode() {
        return apiError.getErrorCode();
    }
}