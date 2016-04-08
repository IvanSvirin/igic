package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class CashbackOrder {
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
    private int sharedStockAmount;
    @Expose
    private float amountCashback;

    public CashbackOrder(long vendorId, float purchaseTotal, int confirmationNumber, String orderDate, String postedDate, String vendorName, String vendorLogoUrl, int sharedStockAmount,
                         float amountCashback) {
        this.vendorId = vendorId;
        this.purchaseTotal = purchaseTotal;
        this.confirmationNumber = confirmationNumber;
        this.orderDate = orderDate;
        this.postedDate = postedDate;
        this.vendorName = vendorName;
        this.vendorLogoUrl = vendorLogoUrl;
        this.sharedStockAmount = sharedStockAmount;
        this.amountCashback = amountCashback;
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

    public float getAmountCashback() {
        return amountCashback;
    }

    public void setAmountCashback(float amountCashback) {
        this.amountCashback = amountCashback;
    }
}
