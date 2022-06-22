package com.ghtk.internal.ekyc.sdk.card.model;

public class CardIDModel {
    private String phone = "";
    private String peopleIdBeforeUrl = "";
    private String peopleIdAfterUrl = "";

    public CardIDModel() {
    }

    public CardIDModel(String phone, String peopleIdBeforeUrl, String peopleIdAfterUrl) {
        this.phone = phone;
        this.peopleIdBeforeUrl = peopleIdBeforeUrl;
        this.peopleIdAfterUrl = peopleIdAfterUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPeopleIdBeforeUrl() {
        return peopleIdBeforeUrl;
    }

    public void setPeopleIdBeforeUrl(String peopleIdBeforeUrl) {
        this.peopleIdBeforeUrl = peopleIdBeforeUrl;
    }

    public String getPeopleIdAfterUrl() {
        return peopleIdAfterUrl;
    }

    public void setPeopleIdAfterUrl(String peopleIdAfterUrl) {
        this.peopleIdAfterUrl = peopleIdAfterUrl;
    }
}
