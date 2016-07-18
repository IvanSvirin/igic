package com.cashback.model;

public class CharitySettings {
    private boolean dealsNotify;
    private boolean donationNotify;

    public CharitySettings(boolean dealsNotify, boolean donationNotify) {
        this.dealsNotify = dealsNotify;
        this.donationNotify = donationNotify;
    }

    public boolean isDealsNotify() {
        return dealsNotify;
    }

    public void setDealsNotify(boolean dealsNotify) {
        this.dealsNotify = dealsNotify;
    }

    public boolean isDonationNotify() {
        return donationNotify;
    }

    public void setDonationNotify(boolean donationNotify) {
        this.donationNotify = donationNotify;
    }
}
