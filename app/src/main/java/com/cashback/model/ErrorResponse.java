package com.cashback.model;

/**
 * Created by I.Svirin on 4/18/2016.
 */
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
