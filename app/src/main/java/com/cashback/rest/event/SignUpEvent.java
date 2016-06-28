package com.cashback.rest.event;

public class SignUpEvent {
    public final boolean isSuccess;
    public final String message;

    public SignUpEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
