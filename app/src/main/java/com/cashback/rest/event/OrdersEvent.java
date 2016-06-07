package com.cashback.rest.event;

public class OrdersEvent {
    public final boolean isSuccess;
    public final String message;

    public OrdersEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
