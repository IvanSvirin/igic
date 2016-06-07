package com.cashback.rest.event;

public class LoginEvent {
    public final boolean isSuccess;
    public final String message;
    public String token = null;

    public LoginEvent(boolean isSuccess, String message) {
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
