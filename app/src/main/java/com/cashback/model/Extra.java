package com.cashback.model;

import com.google.gson.annotations.Expose;

public class Extra {
    @Expose
    private long vendorId;
    @Expose
    private String name;
    @Expose
    private float commission;
    @Expose
    private String exceptionInfo;
    @Expose
    private String description;
    @Expose
    private boolean giftCard;
    @Expose
    private boolean isFavorite;
    @Expose
    private String affiliateUrl;
    @Expose
    private String logoUrl;
    @Expose
    private float commissionWas;

    public Extra(long vendorId, String name, float commission, String exceptionInfo, String description, boolean giftCard, String affiliateUrl, String logoUrl, float commissionWas,
                 boolean isFavorite) {
        this.vendorId = vendorId;
        this.name = name;
        this.commission = commission;
        this.exceptionInfo = exceptionInfo;
        this.description = description;
        this.giftCard = giftCard;
        this.affiliateUrl = affiliateUrl;
        this.logoUrl = logoUrl;
        this.commissionWas = commissionWas;
        this.isFavorite = isFavorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGiftCard() {
        return giftCard;
    }

    public void setGiftCard(boolean giftCard) {
        this.giftCard = giftCard;
    }

    public String getAffiliateUrl() {
        return affiliateUrl;
    }

    public void setAffiliateUrl(String affiliateUrl) {
        this.affiliateUrl = affiliateUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public float getCommissionWas() {
        return commissionWas;
    }

    public void setCommissionWas(float commissionWas) {
        this.commissionWas = commissionWas;
    }
}
