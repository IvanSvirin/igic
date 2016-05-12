package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 5/12/2016.
 */
public class LoginEvent {
    public final boolean isSuccess;
    public final String message;

    public LoginEvent(boolean isSuccess, @Nullable String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
