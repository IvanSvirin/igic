package com.cashback.model;

public class Promotion {
    private long vendorId;
    private String name;
    private float commission;
    private String exceptionInfo;
    private String description;
    private boolean giftCard;
    private String affiliateUrl;
    private String logoUrl;
    private String bannerUrl;
    private String expirationDate;
    private float commissionWas;

    public Promotion(long vendorId, String name, float commission, String exceptionInfo, String description, boolean giftCard, String affiliateUrl, String logoUrl, String bannerUrl,
                     String expirationDate, float commissionWas) {
        this.vendorId = vendorId;
        this.name = name;
        this.commission = commission;
        this.exceptionInfo = exceptionInfo;
        this.description = description;
        this.giftCard = giftCard;
        this.affiliateUrl = affiliateUrl;
        this.logoUrl = logoUrl;
        this.bannerUrl = bannerUrl;
        this.expirationDate = expirationDate;
        this.commissionWas = commissionWas;
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

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public float getCommissionWas() {
        return commissionWas;
    }

    public void setCommissionWas(float commissionWas) {
        this.commissionWas = commissionWas;
    }
}
