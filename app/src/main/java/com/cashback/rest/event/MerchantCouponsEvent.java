package com.cashback.rest.event;

public class MerchantCouponsEvent {
    public final boolean isSuccess;
    public final String message;

    public MerchantCouponsEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
