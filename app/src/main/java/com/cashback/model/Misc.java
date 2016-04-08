package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class Misc {
    @Expose
    private String tellAFriendBannerUrl;
    @Expose
    private String tellAFriendText;
    @Expose
    private String shareDealText;

    public Misc(String tellAFriendBannerUrl, String tellAFriendText, String shareDealText) {
        this.tellAFriendBannerUrl = tellAFriendBannerUrl;
        this.tellAFriendText = tellAFriendText;
        this.shareDealText = shareDealText;
    }

    public String getTellAFriendBannerUrl() {
        return tellAFriendBannerUrl;
    }

    public void setTellAFriendBannerUrl(String tellAFriendBannerUrl) {
        this.tellAFriendBannerUrl = tellAFriendBannerUrl;
    }

    public String getTellAFriendText() {
        return tellAFriendText;
    }

    public void setTellAFriendText(String tellAFriendText) {
        this.tellAFriendText = tellAFriendText;
    }

    public String getShareDealText() {
        return shareDealText;
    }

    public void setShareDealText(String shareDealText) {
        this.shareDealText = shareDealText;
    }
}
