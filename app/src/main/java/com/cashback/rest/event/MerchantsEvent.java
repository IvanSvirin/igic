package com.cashback.rest.event;

public class MerchantsEvent {
    public final boolean isSuccess;
    public final String message;

    public MerchantsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
