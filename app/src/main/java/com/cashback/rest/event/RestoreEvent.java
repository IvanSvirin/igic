package com.cashback.rest.event;

public class RestoreEvent {
    public final boolean isSuccess;
    public final String message;

    public RestoreEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
