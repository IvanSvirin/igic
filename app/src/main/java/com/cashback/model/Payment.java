package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class Payment {
    @Expose
    private String paymentDate;
    @Expose
    private float paymentAmount;
    @Expose
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
