package com.cashback.rest.event;

public class SettingsEvent {
    public final boolean isSuccess;
    public final String message;

    public SettingsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
