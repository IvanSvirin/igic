package com.cashback.rest.event;

public class CategoriesEvent {
    public final boolean isSuccess;
    public final String message;

    public CategoriesEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}

