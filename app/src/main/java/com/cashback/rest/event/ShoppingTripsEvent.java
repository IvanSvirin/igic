package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 5/4/2016.
 */
public class ShoppingTripsEvent {
    public final boolean isSuccess;
    public final String message;

    public ShoppingTripsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
