package com.cashback.model;

public class ErrorResponse {
    private int ErrorCode;
    private String ErrorMessage;

    public ErrorResponse(int code, String message) {
        this.ErrorCode = code;
        this.ErrorMessage = message;
    }

    public int getCode() {
        return ErrorCode;
    }


    public String getMessage() {
        return ErrorMessage;
    }

}
