package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class Coupon {
//    enum CouponType {COUPON, SALE, CLEARANCE, FREE_GIFT, FREE_SHIPPING, SPECIAL_SHIPPING, SPECIAL_OFFER}

    @Expose
    private long couponId;
    @Expose
    private long vendorId;
    @Expose
    private String couponType;
//    @Expose
//    private CouponType couponType;
    @Expose
    private String restrictions;
    @Expose
    private String couponCode;
    @Expose
    private String expirationDate;
    @Expose
    private String affiliateUrl;
    @Expose
    private String vendorLogoUrl;
    @Expose
    private float vendorCommission;

    public Coupon(long couponId, long vendorId, String couponType, String restrictions, String couponCode, String expirationDate, String affiliateUrl, String vendorLogoUrl,
                  float vendorCommission) {
        this.couponId = couponId;
        this.vendorId = vendorId;
        this.couponType = couponType;
        this.restrictions = restrictions;
        this.couponCode = couponCode;
        this.expirationDate = expirationDate;
        this.affiliateUrl = affiliateUrl;
        this.vendorLogoUrl = vendorLogoUrl;
        this.vendorCommission = vendorCommission;
    }

    public Coupon() {
    }
//    public Coupon(long couponId, long vendorId, CouponType couponType, String restrictions, String couponCode, String expirationDate, String affiliateUrl, String vendorLogoUrl,
//                  float vendorCommission) {
//        this.couponId = couponId;
//        this.vendorId = vendorId;
//        this.couponType = couponType;
//        this.restrictions = restrictions;
//        this.couponCode = couponCode;
//        this.expirationDate = expirationDate;
//        this.affiliateUrl = affiliateUrl;
//        this.vendorLogoUrl = vendorLogoUrl;
//        this.vendorCommission = vendorCommission;
//    }

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
//    public CouponType getCouponType() {
//        return couponType;
//    }
//
//    public void setCouponType(CouponType couponType) {
//        this.couponType = couponType;
//    }

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
