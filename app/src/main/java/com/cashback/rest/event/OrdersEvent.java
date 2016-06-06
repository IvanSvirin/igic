package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 5/5/2016.
 */
public class OrdersEvent {
    public final boolean isSuccess;
    public final String message;

    public OrdersEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
