package com.cashback.model;

public class Extra {
    private long vendorId;
    private String name;
    private float commission;
    private String exceptionInfo;
    private String description;
    private boolean giftCard;
    private boolean isFavorite;
    private String affiliateUrl;
    private String logoUrl;
    private float commissionWas;
    private boolean ownersBenefit;

    public Extra(long vendorId, String name, float commission, String exceptionInfo, String description, boolean giftCard, boolean isFavorite, String affiliateUrl, String logoUrl,
                 float commissionWas, boolean ownersBenefit) {
        this.vendorId = vendorId;
        this.name = name;
        this.commission = commission;
        this.exceptionInfo = exceptionInfo;
        this.description = description;
        this.giftCard = giftCard;
        this.isFavorite = isFavorite;
        this.affiliateUrl = affiliateUrl;
        this.logoUrl = logoUrl;
        this.commissionWas = commissionWas;
        this.ownersBenefit = ownersBenefit;
    }

    public boolean isOwnersBenefit() {
        return ownersBenefit;
    }

    public void setOwnersBenefit(boolean ownersBenefit) {
        this.ownersBenefit = ownersBenefit;
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
