package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 4/18/2016.
 */
public class ImageEvent {
    public final boolean isSuccess;
    public final String message;

    public ImageEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

}
