package com.cashback.model;

import com.google.gson.annotations.Expose;

public class Account {
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

    public Account(String token, String firstName, String lastName, String email, String memberDate, int referrerId) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.memberDate = memberDate;
        this.referrerId = referrerId;
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
}
