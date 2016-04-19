package com.cashback.model;

import com.google.gson.annotations.Expose;

// TODO: 4/19/2016 TEST - will be deleted

public class OfferCoupon {

    @Expose private int id;
    @Expose private String payoutmsg;
    @Expose private String description;
    @Expose private String code;
    @Expose private String expire;
    @Expose private String expire_iso;
    @Expose private String logo;
    @Expose private String url;

    public OfferCoupon(int id, String payoutSizeMsg, String description, String couponCode, String expireDate, String expireRawDate, String logoUrl, String activateUrl) {
        this.id = id;
        this.payoutmsg = payoutSizeMsg;
        this.description = description;
        this.code = couponCode;
        this.expire = expireDate;
        this.expire_iso = expireRawDate;
        this.logo = logoUrl;
        this.url = activateUrl;
    }

    public int getId() {
        return id;
    }

    public String getPayoutSizeMsg() {
        return payoutmsg;
    }

    public String getDescription() {
        return description;
    }

    public String getCouponCode() {
        return code;
    }

    public String getExpireDate() {
        return expire;
    }

    public String getLogoUrl() {
        return logo;
    }

    public String getActivateUrl() {
        return url;
    }

    public String getExpireRawDate() {
        return expire_iso;
    }
}
