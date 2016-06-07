package com.cashback.rest.event;

public class SearchEvent {
    public final boolean isSuccess;
    public final String message;

    public SearchEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
