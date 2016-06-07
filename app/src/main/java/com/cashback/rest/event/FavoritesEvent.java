package com.cashback.rest.event;

public class FavoritesEvent {
    public final boolean isSuccess;
    public final String message;

    public FavoritesEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
