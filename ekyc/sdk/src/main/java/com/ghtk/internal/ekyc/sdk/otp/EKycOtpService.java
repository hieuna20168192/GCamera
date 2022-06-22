package com.ghtk.internal.ekyc.sdk.otp;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.internalconfig.InternalConfig;
import com.ghtk.internal.repository.token.AuthenServiceImp;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.model.remote.BaseRemoteObject;
import com.ghtk.internal.model.remote.login.LoginResponseDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ghtk.internal.ekyc.sdk.otp.model.OtpGenerationDto;

public class EKycOtpService {
    private final static String OTP_VERIFICATION_API = "/api/v1/otp/verification";
    private final static String OTP_GENERATION_API = "/api/v1/otp/generation";

    public static void authenDemo(OnLoadDataCompleted<LoginResponseDto> onLoadDataCompleted) {
        final String body = "{\"scopes\":[]}";
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("scopes", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenServiceImp.getInstance().doJsonRequest("https://auth.ghtklab.com/api/v1/clients/token", jsonObject.toString(), onLoadDataCompleted, LoginResponseDto.class);
    }


    public static void generationOtp(String phoneNumber, String serviceCode, String content, int otpTtl, OnLoadDataCompleted<OtpGenerationDto> onLoadDataCompleted) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);
            jsonObject.put("mobile", phoneNumber);
            jsonObject.put("service_code", serviceCode);
            jsonObject.put("otp_ttl", otpTtl);
        } catch (JSONException e) {
            LogUtils.e(e.toString());
        }
        AuthenServiceImp.getInstance().doJsonRequest(InternalConfig.RepositoryModule.INSTANCE.getKycDomainOtp() + OTP_GENERATION_API, jsonObject.toString(), onLoadDataCompleted, OtpGenerationDto.class);
    }

    public static void verificationOtp(String phoneNumber, String serviceCode, String otpCode, OnLoadDataCompleted<BaseRemoteObject> onLoadDataCompleted) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", phoneNumber);
            jsonObject.put("service_code", serviceCode);
            jsonObject.put("otp_code", otpCode);
        } catch (JSONException e) {
            LogUtils.e(e.toString());
        }
        AuthenServiceImp.getInstance().doJsonRequest(InternalConfig.RepositoryModule.INSTANCE.getKycDomainOtp() + OTP_VERIFICATION_API, jsonObject.toString(), onLoadDataCompleted, BaseRemoteObject.class);
    }
}
