package com.ghtk.internal.ekyc.sdk.card.model;

public class CardImageInfo {

    @com.squareup.moshi.Json(name = "id")
    private String id;

    @com.squareup.moshi.Json(name = "name")
    private String name;

    @com.squareup.moshi.Json(name = "height")
    private int height;

    @com.squareup.moshi.Json(name = "width")
    private int width;

    @com.squareup.moshi.Json(name = "url")
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
