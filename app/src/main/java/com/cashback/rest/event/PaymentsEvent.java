package com.cashback.rest.event;

public class PaymentsEvent {
    public final boolean isSuccess;
    public final String message;

    public PaymentsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
