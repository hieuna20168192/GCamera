
package com.ghtk.internal.ekyc.sdk.card.model;

import com.ghtk.internal.model.remote.BaseRemoteObject;

public class CardInfoKycDto extends BaseRemoteObject {

    @com.squareup.moshi.Json(name = "data")
    private CardInfoKycData data;

    public CardInfoKycData getData() {
        return data;
    }

    public void setData(CardInfoKycData data) {
        this.data = data;
    }
}
