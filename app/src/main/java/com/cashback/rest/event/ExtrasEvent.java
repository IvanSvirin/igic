package com.cashback.rest.event;

public class ExtrasEvent {
    public final boolean isSuccess;
    public final String message;

    public ExtrasEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
