package com.cashback.rest.event;

public class CouponsEvent {
    public final boolean isSuccess;
    public final String message;

    public CouponsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
