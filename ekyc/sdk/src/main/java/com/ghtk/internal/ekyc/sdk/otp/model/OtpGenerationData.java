package com.ghtk.internal.ekyc.sdk.otp.model;

import com.squareup.moshi.Json;

public class OtpGenerationData {

        @Json(name = "otp_ttl")
        private int otpTtl;
        @Json(name = "next_generation")
        private long nextGeneration;
        @Json(name = "max_verification_time")
        private long maxVerificationTime;

        public int getOtpTtl() {
            return otpTtl;
        }

        public void setOtpTtl(int otpTtl) {
            this.otpTtl = otpTtl;
        }

        public long getNextGeneration() {
            return nextGeneration;
        }

        public void setNextGeneration(Long nextGeneration) {
            this.nextGeneration = nextGeneration;
        }

        public long getMaxVerificationTime() {
            return maxVerificationTime;
        }

        public void setMaxVerificationTime(Long maxVerificationTime) {
            this.maxVerificationTime = maxVerificationTime;
        }

    }
