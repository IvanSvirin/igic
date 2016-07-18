package com.cashback.model;

public class CashbackAccount {
    private String token;
    private String firstName;
    private String lastName;
    private String email;
    private String memberDate;
    private int referrerId;
    private float cachePendingAmount;
    private String nextPaymentDate;
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
