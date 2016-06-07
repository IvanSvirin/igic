package com.cashback.rest;

import com.cashback.model.WarningResponse;

public class WarningRestException extends RuntimeException {
    private WarningResponse body;

    public WarningRestException(String status, WarningResponse body) {
        super(status);
        this.body = body;
    }

    public WarningResponse getBody() {
        return body;
    }
}