package com.cashback.model;

import com.google.gson.annotations.Expose;

public class AuthObject {
    @Expose
    private String authType;
    @Expose
    private String firstName;   //only for iGive
    @Expose
    private String lastName;    //only for iGive
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String zip; //only for iGive
    @Expose
    private String token;
    @Expose
    private String incomingReferrerId;
    @Expose
    private String referrerEmail;   //only for iConsumer

    public AuthObject() {
    }

    public AuthObject(String authType, String firstName, String lastName, String email, String password, String zip, String token, String incomingReferrerId, String referrerEmail) {
        this.authType = authType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.zip = zip;
        this.token = token;
        this.incomingReferrerId = incomingReferrerId;
        this.referrerEmail = referrerEmail;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIncomingReferrerId() {
        return incomingReferrerId;
    }

    public void setIncomingReferrerId(String incomingReferrerId) {
        this.incomingReferrerId = incomingReferrerId;
    }

    public String getReferrerEmail() {
        return referrerEmail;
    }

    public void setReferrerEmail(String referrerEmail) {
        this.referrerEmail = referrerEmail;
    }
}
