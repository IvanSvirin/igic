package com.cashback.model;

public class Product {
    private long vendorId;
    private String title;
    private float price;
    private String description;
    private String imageUrl;
    private String vendorLogoUrl;
    private float vendorCommission;
    private String vendorAffiliateUrl;
    private float estimatedPriceTotal;
    private float estimatedShipping;
    private float priceMerchant;
    private float priceRetail;

    public Product(long vendorId, String title, float price, String description, String imageUrl, String vendorLogoUrl, float vendorCommission, String vendorAffiliateUrl,
                   float estimatedPriceTotal, float estimatedShipping, float priceMerchant, float priceRetail) {
        this.vendorId = vendorId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.vendorLogoUrl = vendorLogoUrl;
        this.vendorCommission = vendorCommission;
        this.vendorAffiliateUrl = vendorAffiliateUrl;
        this.estimatedPriceTotal = estimatedPriceTotal;
        this.estimatedShipping = estimatedShipping;
        this.priceMerchant = priceMerchant;
        this.priceRetail = priceRetail;
    }

    public Product() {
    }

    public float getEstimatedPriceTotal() {
        return estimatedPriceTotal;
    }

    public void setEstimatedPriceTotal(float estimatedPriceTotal) {
        this.estimatedPriceTotal = estimatedPriceTotal;
    }

    public float getEstimatedShipping() {
        return estimatedShipping;
    }

    public void setEstimatedShipping(float estimatedShipping) {
        this.estimatedShipping = estimatedShipping;
    }

    public float getPriceMerchant() {
        return priceMerchant;
    }

    public void setPriceMerchant(float priceMerchant) {
        this.priceMerchant = priceMerchant;
    }

    public float getPriceRetail() {
        return priceRetail;
    }

    public void setPriceRetail(float priceRetail) {
        this.priceRetail = priceRetail;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getVendorAffiliateUrl() {
        return vendorAffiliateUrl;
    }

    public void setVendorAffiliateUrl(String vendorAffiliateUrl) {
        this.vendorAffiliateUrl = vendorAffiliateUrl;
    }
}
