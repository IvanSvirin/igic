package com.cashback.rest.event;

public class ImageEvent {
    public final boolean isSuccess;
    public final String message;

    public ImageEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

}
