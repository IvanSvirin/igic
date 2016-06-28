package com.cashback.rest.event;

public class SignInEvent {
    public final boolean isSuccess;
    public final String message;

    public SignInEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
