package com.cashback.model;

public class SearchResult {
    private Merchant[] merchants;
    private Coupon[] coupons;
    private Product[] products;

    public SearchResult(Merchant[] merchants, Coupon[] coupons, Product[] products) {
        this.merchants = merchants;
        this.coupons = coupons;
        this.products = products;
    }

    public Merchant[] getMerchants() {
        return merchants;
    }

    public void setMerchants(Merchant[] merchants) {
        this.merchants = merchants;
    }

    public Coupon[] getCoupons() {
        return coupons;
    }

    public void setCoupons(Coupon[] coupons) {
        this.coupons = coupons;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }
}
