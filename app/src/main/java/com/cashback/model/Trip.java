package com.cashback.model;

public class Trip {
    private long vendorId;
    private int confirmationNumber;
    private String tripDate;
    private String vendorName;
    private String vendorLogoUrl;

    public Trip(long vendorId, int confirmationNumber, String tripDate, String vendorName, String vendorLogoUrl) {
        this.vendorId = vendorId;
        this.confirmationNumber = confirmationNumber;
        this.tripDate = tripDate;
        this.vendorName = vendorName;
        this.vendorLogoUrl = vendorLogoUrl;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public int getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(int confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
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
}
