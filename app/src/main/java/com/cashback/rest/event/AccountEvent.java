package com.cashback.rest.event;

public class AccountEvent {
    public final boolean isSuccess;
    public final String message;

    public AccountEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
