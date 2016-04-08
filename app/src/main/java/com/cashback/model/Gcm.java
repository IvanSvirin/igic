package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class Gcm {
    enum OsType {ANDROID, IOS, WINDOWS}
    @Expose
    private String tokenGcm;
    @Expose
    private OsType osType;

    public Gcm(String tokenGcm, OsType osType) {
        this.tokenGcm = tokenGcm;
        this.osType = osType;
    }

    public String getTokenGcm() {
        return tokenGcm;
    }

    public void setTokenGcm(String tokenGcm) {
        this.tokenGcm = tokenGcm;
    }

    public OsType getOsType() {
        return osType;
    }

    public void setOsType(OsType osType) {
        this.osType = osType;
    }
}
