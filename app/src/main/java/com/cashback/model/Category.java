package com.cashback.model;


import com.google.gson.annotations.Expose;

/**
 * Created by I.Svirin on 4/6/2016.
 */
public class Category {
    @Expose
    private long categoryId;
    @Expose
    private String name;

    public Category(long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
