package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 5/31/2016.
 */
public class SearchEvent {
    public final boolean isSuccess;
    public final String message;

    public SearchEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
