
package com.ghtk.internal.ekyc.sdk.otp.model;

import com.ghtk.internal.model.remote.BaseRemoteObject;
import com.squareup.moshi.Json;

public class OtpGenerationDto extends BaseRemoteObject {

    @Json(name = "data")
    private OtpGenerationData data;

    public OtpGenerationData getData() {
        return data;
    }

    public void setData(OtpGenerationData data) {
        this.data = data;
    }

}
