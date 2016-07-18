package com.cashback.model;

public class Gcm {
    private String tokenGcm;
    private String osType;

    public Gcm(String tokenGcm, String osType) {
        this.tokenGcm = tokenGcm;
        this.osType = osType;
    }

    public String getTokenGcm() {
        return tokenGcm;
    }

    public void setTokenGcm(String tokenGcm) {
        this.tokenGcm = tokenGcm;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }
}
