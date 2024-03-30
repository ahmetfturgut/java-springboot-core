package com.javaspringboot.javaspringbootcore.core.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Map;

@Getter
public class ApiResponse<T> {
    final private Date timestamp;
    final private boolean success;
    final private T data;
    final private Object message; // String yerine Object tipinde olacak
    final private Integer errorCode;

    // SuccessResponse
    public ApiResponse(T data) {
        this.timestamp = new Date();
        this.success = true;
        this.data = data;
        this.message = "Success";
        this.errorCode = null;
    }

    // ErrorResponse
    public ApiResponse(boolean success, Object message, Integer errorCode) {
        this.timestamp = new Date();
        this.success = success;
        this.data = null;
        this.message = message;
        this.errorCode = errorCode;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static ApiResponse<Object> error(Object message, Integer errorCode) {
        return new ApiResponse<>(false, message, errorCode);
    }

    public static ApiResponse<Object> validationError(Map<String, String> validationErrors) {
        return ApiResponse.error(validationErrors, HttpStatus.BAD_REQUEST.value());
    }
}
