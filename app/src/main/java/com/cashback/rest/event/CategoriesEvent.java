package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 4/29/2016.
 */
public class CategoriesEvent {
    public final boolean isSuccess;
    public final String message;

    public CategoriesEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}

