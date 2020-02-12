package com.jinseonkim.photocloud.model;

public class PhotoModel {
    private String identifier;
    private String createdDate;
    private String image;
    private String thumbnail;
    private String name;

    public String getIdentifier() {
        return this.identifier;
    }

    public String getCreatedDate() {
        return this.createdDate;
    }

    public String getImage() {
        return this.image;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public String getName() {
        return this.name;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setName(String name) {
        this.name = name;
    }
}
