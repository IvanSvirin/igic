package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 4/18/2016.
 */
public class CouponsEvent {
    public final boolean isSuccess;
    public final String message;

    public CouponsEvent(boolean isSuccess, @Nullable String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
