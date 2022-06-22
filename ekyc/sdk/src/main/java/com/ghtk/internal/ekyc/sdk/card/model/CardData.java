package com.ghtk.internal.ekyc.sdk.card.model;

import com.squareup.moshi.Json;

public class CardData {

        @Json(name = "date")
        private String date;
        @Json(name = "hometown")
        private String hometown;
        @Json(name = "address")
        private String address;
        @Json(name = "gender")
        private String gender;
        @Json(name = "nationality")
        private String nationality;
        @Json(name = "dob")
        private String dob;
        @Json(name = "name")
        private String name;
        @Json(name = "id")
        private String id;
        @Json(name = "source")
        private String source;
        @Json(name = "expired_date")
        private String expiredDate;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getHometown() {
            return hometown;
        }

        public void setHometown(String hometown) {
            this.hometown = hometown;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(String expiredDate) {
            this.expiredDate = expiredDate;
        }

    }
