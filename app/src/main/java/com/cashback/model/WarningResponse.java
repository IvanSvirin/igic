package com.cashback.model;

/**
 * Created by I.Svirin on 4/18/2016.
 */
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
