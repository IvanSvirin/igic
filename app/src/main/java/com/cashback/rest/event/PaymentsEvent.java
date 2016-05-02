package com.cashback.rest.event;

import android.support.annotation.Nullable;

/**
 * Created by ivansv on 02.05.2016.
 */
public class PaymentsEvent {
    public final boolean isSuccess;
    public final String message;

    public PaymentsEvent(boolean isSuccess, @Nullable String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }}
