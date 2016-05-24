package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 5/24/2016.
 */
public class FavoritesEvent {
    public final boolean isSuccess;
    public final String message;

    public FavoritesEvent(boolean isSuccess, @Nullable String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
