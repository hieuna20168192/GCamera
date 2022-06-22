package com.ghtk.internal.ekyc.sdk.card.model;

import java.util.List;

public class CardInfoKycData {

        @com.squareup.moshi.Json(name = "ekyc_id")
        private String ekycId;
        @com.squareup.moshi.Json(name = "status")
        private String status;
        @com.squareup.moshi.Json(name = "card_type")
        private String cardType;
        @com.squareup.moshi.Json(name = "card_data")
        private CardData cardData;
        @com.squareup.moshi.Json(name = "phone_numbers")
        private List<PhoneNumber> phoneNumbers = null;

        public String getEkycId() {
            return ekycId;
        }

        public void setEkycId(String ekycId) {
            this.ekycId = ekycId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public CardData getCardData() {
            return cardData;
        }

        public void setCardData(CardData cardData) {
            this.cardData = cardData;
        }

        public List<PhoneNumber> getPhoneNumbers() {
            return phoneNumbers;
        }

        public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

    }
