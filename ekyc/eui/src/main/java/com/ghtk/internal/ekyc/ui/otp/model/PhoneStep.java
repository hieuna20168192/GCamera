
package com.ghtk.internal.ekyc.ui.otp.model;

public class PhoneStep {

    @com.squareup.moshi.Json(name = "phone_number")
    private String phoneNumber;
    @com.squareup.moshi.Json(name = "step")
    private int step;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

}
