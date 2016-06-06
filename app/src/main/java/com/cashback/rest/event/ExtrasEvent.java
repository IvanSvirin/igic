package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 5/24/2016.
 */
public class ExtrasEvent {
    public final boolean isSuccess;
    public final String message;

    public ExtrasEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
