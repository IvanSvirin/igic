package com.cashback.rest.event;

public class SignUpEvent {
    public final boolean isSuccess;
    public final String message;
    public String token = null;

    public SignUpEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
