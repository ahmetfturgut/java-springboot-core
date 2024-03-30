package com.javaspringboot.javaspringbootcore.core.exception;

import lombok.Getter;

import java.util.Date;

@Getter
public class ApiError {

    private final int errorCode;
    private final Date timestamp = new Date();
    private final String message;


    public ApiError(int statusCode, String message) {
        this.errorCode = statusCode;
        this.message = message;
    }

    //auth
    public static ApiError TOKEN_ERROR = new ApiError(101, "Token error.");
    public static ApiError TOKEN_EXPIRED = new ApiError(102, "Token expired.");
    public static ApiError TOKEN_BLOCKED = new ApiError(103, "Token blocked.");
    public static ApiError DEFERRABLE_TOKEN_EXPIRED = new ApiError(104, "Defferrable Token expired.");
    public static ApiError REQUIRED_TERMS_AND_CONDITIONS = new ApiError(105, "Terms and conditions is required.");
    public static ApiError TOKEN_CODE_ERROR = new ApiError(106, "Token code error.");

    // USER
    public static ApiError USER_EXISTS = new ApiError(201, "User exists.");
    public static ApiError USER_EMAIL_EXISTS = new ApiError(202, "User email exists.");
    public static ApiError USER_NOT_FOUND = new ApiError(203, "User not found.");


}