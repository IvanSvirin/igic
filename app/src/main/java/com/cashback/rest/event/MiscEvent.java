package com.cashback.rest.event;

public class MiscEvent {
    public final boolean isSuccess;
    public final String message;

    public MiscEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

}
