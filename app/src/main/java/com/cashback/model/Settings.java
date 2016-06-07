package com.cashback.model;

import com.google.gson.annotations.Expose;

public class Settings {
    @Expose
    private boolean dealsNotify;

    public Settings(boolean dealsNotify) {
        this.dealsNotify = dealsNotify;
    }

    public boolean isDealsNotify() {
        return dealsNotify;
    }

    public void setDealsNotify(boolean dealsNotify) {
        this.dealsNotify = dealsNotify;
    }
}
