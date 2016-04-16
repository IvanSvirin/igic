package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by ivansv on 16.04.2016.
 */
public class AccountEvent {
    public final boolean isSuccess;
    public final String message;

    public AccountEvent(boolean isSuccess, @Nullable String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
