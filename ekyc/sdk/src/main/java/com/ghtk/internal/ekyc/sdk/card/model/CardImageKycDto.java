package com.ghtk.internal.ekyc.sdk.card.model;

import com.ghtk.internal.model.remote.BaseRemoteObject;

import java.util.List;

public class CardImageKycDto extends BaseRemoteObject {

    @com.squareup.moshi.Json(name = "data")
    private List<CardImageInfo> data;

    public List<CardImageInfo> getData() {
        return data;
    }

    public void setData(List<CardImageInfo> data) {
        this.data = data;
    }
}
