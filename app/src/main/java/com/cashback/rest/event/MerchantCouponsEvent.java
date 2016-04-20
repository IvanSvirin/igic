package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by I.Svirin on 4/20/2016.
 */
public class MerchantCouponsEvent {
    public final boolean isSuccess;
    public final String message;

    public MerchantCouponsEvent(boolean isSuccess, @Nullable String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
