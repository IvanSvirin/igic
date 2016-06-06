package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 6/1/2016.
 */
public class CategoryMerchantsEvent {
    public final boolean isSuccess;
    public final String message;

    public CategoryMerchantsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
