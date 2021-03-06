package com.cashback.model;

public class CashbackSettings {
    private boolean dealsNotify;
    private boolean cashbackNotify;
    private boolean paymentNotify;

    public CashbackSettings(boolean dealsNotify, boolean cashbackNotify, boolean paymentNotify) {
        this.dealsNotify = dealsNotify;
        this.cashbackNotify = cashbackNotify;
        this.paymentNotify = paymentNotify;
    }

    public boolean isDealsNotify() {
        return dealsNotify;
    }

    public void setDealsNotify(boolean dealsNotify) {
        this.dealsNotify = dealsNotify;
    }

    public boolean isCashbackNotify() {
        return cashbackNotify;
    }

    public void setCashbackNotify(boolean cashbackNotify) {
        this.cashbackNotify = cashbackNotify;
    }

    public boolean isPaymentNotify() {
        return paymentNotify;
    }

    public void setPaymentNotify(boolean paymentNotify) {
        this.paymentNotify = paymentNotify;
    }
}
