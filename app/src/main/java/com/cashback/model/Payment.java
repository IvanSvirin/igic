package com.cashback.model;

public class Payment {
    private String paymentDate;
    private float paymentAmount;
    private String paymentAccount;

    public Payment(String paymentDate, float paymentAmount, String paymentAccount) {
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentAccount = paymentAccount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }
}
