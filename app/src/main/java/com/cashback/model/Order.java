package com.cashback.model;

import com.google.gson.annotations.Expose;

public class Order {
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
    // TODO: 5/5/2016 may be it must be float?????
    @Expose
    private int sharedStockAmount;  //only for iConsumer
    @Expose
    private float cashBack; // added myself

    public Order(long vendorId, float purchaseTotal, int confirmationNumber, String orderDate, String postedDate, String vendorName, String vendorLogoUrl, int sharedStockAmount) {
        this.vendorId = vendorId;
        this.purchaseTotal = purchaseTotal;
        this.confirmationNumber = confirmationNumber;
        this.orderDate = orderDate;
        this.postedDate = postedDate;
        this.vendorName = vendorName;
        this.vendorLogoUrl = vendorLogoUrl;
        this.sharedStockAmount = sharedStockAmount;
        this.cashBack = cashBack;
    }

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

    public int getSharedStockAmount() {
        return sharedStockAmount;
    }

    public void setSharedStockAmount(int sharedStockAmount) {
        this.sharedStockAmount = sharedStockAmount;
    }

    public float getCashBack() {
        return cashBack;
    }

    public void setCashBack(float cashBack) {
        this.cashBack = cashBack;
    }
}
