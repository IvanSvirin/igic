package com.cashback.rest;

import com.cashback.model.ErrorResponse;

/**
 * Created by I.Svirin on 4/18/2016.
 */
public class ErrorRestException extends RuntimeException {

    private ErrorResponse body;

    public ErrorRestException(String status, ErrorResponse body) {
        super(status);
        this.body = body;
    }

    public ErrorResponse getBody() {
        return body;
    }
}
