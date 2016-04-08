package com.cashback.model;

import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/7/2016.
 */
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
