package com.cashback.model;

public class WarningResponse {
    private String field;
    private String error;

    public WarningResponse(String email, String message) {
        this.field = email;
        this.error = message;
    }

    public String getEmail() {
        return field;
    }


    public String getMessage() {
        return error;
    }
}
