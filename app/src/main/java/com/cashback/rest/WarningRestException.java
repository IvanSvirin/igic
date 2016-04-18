package com.cashback.rest;

import com.cashback.model.WarningResponse;

/**
 * Created by I.Svirin on 4/18/2016.
 */
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