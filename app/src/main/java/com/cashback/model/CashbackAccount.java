package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class CashbackAccount {
    @Expose
    private String token;
    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private String email;
    @Expose
    private String memberDate;
    @Expose
    private int referrerId;
    @Expose
    private float cachePendingAmount;
    @Expose
    private String nextPaymentDate;
    @Expose
    private float paymentsTotalAmount;

    public CashbackAccount(String token, String firstName, String lastName, String email, String memberDate, int referrerId, float cachePendingAmount, String nextPaymentDate,
                           float paymentsTotalAmount) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.memberDate = memberDate;
        this.referrerId = referrerId;
        this.cachePendingAmount = cachePendingAmount;
        this.nextPaymentDate = nextPaymentDate;
        this.paymentsTotalAmount = paymentsTotalAmount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMemberDate() {
        return memberDate;
    }

    public void setMemberDate(String memberDate) {
        this.memberDate = memberDate;
    }

    public int getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(int referrerId) {
        this.referrerId = referrerId;
    }

    public float getCachePendingAmount() {
        return cachePendingAmount;
    }

    public void setCachePendingAmount(float cachePendingAmount) {
        this.cachePendingAmount = cachePendingAmount;
    }

    public String getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(String nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public float getPaymentsTotalAmount() {
        return paymentsTotalAmount;
    }

    public void setPaymentsTotalAmount(float paymentsTotalAmount) {
        this.paymentsTotalAmount = paymentsTotalAmount;
    }
}
