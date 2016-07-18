package com.cashback.model;

public class AuthObject {
    private String authType;
    private String firstName;   //only for iGive
    private String lastName;    //only for iGive
    private String email;
    private String password;
    private String zip; //only for iGive
    private String token;
    private String incomingReferrerId;
    private String referrerEmail;   //only for iConsumer
    private String userId;

    public AuthObject() {
    }

    public AuthObject(String authType, String firstName, String lastName, String email, String password, String zip, String token, String incomingReferrerId, String referrerEmail, String userId) {
        this.authType = authType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.zip = zip;
        this.token = token;
        this.incomingReferrerId = incomingReferrerId;
        this.referrerEmail = referrerEmail;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
