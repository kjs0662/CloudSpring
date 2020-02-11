package com.jinseonkim.photocloud.model;

public class InfoModel {
    private final String identifier;
    private final String createdDate;

    public InfoModel(String identifier, String createdDate) {
        this.identifier = identifier;
        this.createdDate = createdDate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}
