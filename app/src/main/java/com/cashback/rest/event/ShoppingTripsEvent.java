package com.cashback.rest.event;

public class ShoppingTripsEvent {
    public final boolean isSuccess;
    public final String message;

    public ShoppingTripsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
