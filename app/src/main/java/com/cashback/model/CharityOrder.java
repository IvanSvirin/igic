package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class CharityOrder {
    @Expose
    private long vendorId;
    @Expose
    private float purchaseTotal;
    @Expose
    private int confirmationNumber;
    @Expose
    private String orderDate;
    @Expose
    private String postedDate;
    @Expose
    private String vendorName;
    @Expose
    private String vendorLogoUrl;
    @Expose
    private String causeName;
    @Expose
    private String causeLogoUrl;
    @Expose
    private float amountDonated;

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public float getPurchaseTotal() {
        return purchaseTotal;
    }

    public void setPurchaseTotal(float purchaseTotal) {
        this.purchaseTotal = purchaseTotal;
    }

    public int getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(int confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorLogoUrl() {
        return vendorLogoUrl;
    }

    public void setVendorLogoUrl(String vendorLogoUrl) {
        this.vendorLogoUrl = vendorLogoUrl;
    }

    public String getCauseName() {
        return causeName;
    }

    public void setCauseName(String causeName) {
        this.causeName = causeName;
    }

    public String getCauseLogoUrl() {
        return causeLogoUrl;
    }

    public void setCauseLogoUrl(String causeLogoUrl) {
        this.causeLogoUrl = causeLogoUrl;
    }

    public float getAmountDonated() {
        return amountDonated;
    }

    public void setAmountDonated(float amountDonated) {
        this.amountDonated = amountDonated;
    }
}
