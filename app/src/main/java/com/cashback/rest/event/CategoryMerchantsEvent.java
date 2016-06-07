package com.cashback.rest.event;

public class CategoryMerchantsEvent {
    public final boolean isSuccess;
    public final String message;

    public CategoryMerchantsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
