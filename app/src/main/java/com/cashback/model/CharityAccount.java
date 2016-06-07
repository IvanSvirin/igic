package com.cashback.model;

import com.google.gson.annotations.Expose;

public class CharityAccount {
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
    private float nextCheckAmount;
    @Expose
    private float pendingAmount;
    @Expose
    private float totalPaidAmount;
    @Expose
    private String totalPaidDate;
    @Expose
    private String totalEarned;
    @Expose
    private float totalRaised;
    @Expose
    private String causeDashboardUrl;
    @Expose
    private String selectCauseUrl;
    @Expose
    private String referrerId;

    public CharityAccount(String token, String firstName, String lastName, String email, String memberDate, float nextCheckAmount, float pendingAmount, float totalPaidAmount,
                          String totalPaidDate, String totalEarned, float totalRaised, String causeDashboardUrl, String selectCauseUrl, String referrerId) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.memberDate = memberDate;
        this.nextCheckAmount = nextCheckAmount;
        this.pendingAmount = pendingAmount;
        this.totalPaidAmount = totalPaidAmount;
        this.totalPaidDate = totalPaidDate;
        this.totalEarned = totalEarned;
        this.totalRaised = totalRaised;
        this.causeDashboardUrl = causeDashboardUrl;
        this.selectCauseUrl = selectCauseUrl;
        this.referrerId = referrerId;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public String getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(String totalEarned) {
        this.totalEarned = totalEarned;
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

    public float getNextCheckAmount() {
        return nextCheckAmount;
    }

    public void setNextCheckAmount(float nextCheckAmount) {
        this.nextCheckAmount = nextCheckAmount;
    }

    public float getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(float pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public float getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(float totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public String getTotalPaidDate() {
        return totalPaidDate;
    }

    public void setTotalPaidDate(String totalPaidDate) {
        this.totalPaidDate = totalPaidDate;
    }

    public float getTotalRaised() {
        return totalRaised;
    }

    public void setTotalRaised(float totalRaised) {
        this.totalRaised = totalRaised;
    }

    public String getCauseDashboardUrl() {
        return causeDashboardUrl;
    }

    public void setCauseDashboardUrl(String causeDashboardUrl) {
        this.causeDashboardUrl = causeDashboardUrl;
    }

    public String getSelectCauseUrl() {
        return selectCauseUrl;
    }

    public void setSelectCauseUrl(String selectCauseUrl) {
        this.selectCauseUrl = selectCauseUrl;
    }
}
