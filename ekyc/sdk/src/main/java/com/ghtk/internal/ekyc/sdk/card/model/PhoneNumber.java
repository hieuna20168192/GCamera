package com.ghtk.internal.ekyc.sdk.card.model;

public class PhoneNumber {

        @com.squareup.moshi.Json(name = "phone")
        private String phone;
        @com.squareup.moshi.Json(name = "status")
        private String status;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
