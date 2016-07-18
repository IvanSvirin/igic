package com.cashback.model;

public class CharityOrder {
    private long vendorId;
    private float purchaseTotal;
    private int confirmationNumber;
    private String orderDate;
    private String postedDate;
    private String vendorName;
    private String vendorLogoUrl;
    private String causeName;
    private String causeLogoUrl;
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
