package com.cashback.model;

public class Coupon {
    private long couponId;
    private long vendorId;
    private String couponType;
    private String restrictions;
    private String couponCode;
    private String expirationDate;
    private String affiliateUrl;
    private String vendorLogoUrl;
    private float vendorCommission;
    private String label;
    private boolean ownersBenefit;

    public Coupon(long couponId, long vendorId, String couponType, String restrictions, String couponCode, String expirationDate, String affiliateUrl, String vendorLogoUrl,
                  float vendorCommission, String label, boolean ownersBenefit) {
        this.couponId = couponId;
        this.vendorId = vendorId;
        this.couponType = couponType;
        this.restrictions = restrictions;
        this.couponCode = couponCode;
        this.expirationDate = expirationDate;
        this.affiliateUrl = affiliateUrl;
        this.vendorLogoUrl = vendorLogoUrl;
        this.vendorCommission = vendorCommission;
        this.label = label;
        this.ownersBenefit = ownersBenefit;
    }

    public Coupon() {
    }

    public boolean isOwnersBenefit() {
        return ownersBenefit;
    }

    public void setOwnersBenefit(boolean ownersBenefit) {
        this.ownersBenefit = ownersBenefit;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAffiliateUrl() {
        return affiliateUrl;
    }

    public void setAffiliateUrl(String affiliateUrl) {
        this.affiliateUrl = affiliateUrl;
    }

    public String getVendorLogoUrl() {
        return vendorLogoUrl;
    }

    public void setVendorLogoUrl(String vendorLogoUrl) {
        this.vendorLogoUrl = vendorLogoUrl;
    }

    public float getVendorCommission() {
        return vendorCommission;
    }

    public void setVendorCommission(float vendorCommission) {
        this.vendorCommission = vendorCommission;
    }
}
