package com.ghtk.internal.ekyc.sdk.card;

import android.net.Uri;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.internalconfig.InternalConfig;
import com.ghtk.internal.repository.token.AuthenServiceImp;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.model.remote.BaseRemoteObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.ghtk.internal.ekyc.sdk.card.model.CardData;
import com.ghtk.internal.ekyc.sdk.card.model.CardImageKycDto;
import com.ghtk.internal.ekyc.sdk.card.model.CardInfoKycDto;

public class EKycCardIdService {
    public final static String CONFIRM_CARD_DATA = "/api/v1/confirm-card-data";
    public final static String EKYC_PROFILE = "/api/v1/ekyc-profile";
    public final static String UPLOAD_IMAGE_API = "/file/upload";

    public static void ekycProfile(String phoneNumber, List<String> cardImages, OnLoadDataCompleted<CardInfoKycDto> onLoadDataCompleted) {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < cardImages.size(); i++) {
                final JSONObject itemCardObj = new JSONObject();
                itemCardObj.put("url", cardImages.get(i));
                itemCardObj.put("image_order", i + 1);
                jsonArray.put(itemCardObj);
            }
            jsonObject.put("card_type", "id_card");
            jsonObject.put("card_images", jsonArray);
            jsonObject.put("phone_number", phoneNumber);
        } catch (JSONException e) {
            LogUtils.e(e.toString());
        }
        AuthenServiceImp.getInstance().doJsonRequest(InternalConfig.RepositoryModule.INSTANCE.getKycDomain() + EKYC_PROFILE, jsonObject.toString(), onLoadDataCompleted, CardInfoKycDto.class);
    }

    public static void confirmCardData(String ekycId, CardData cardData, OnLoadDataCompleted<BaseRemoteObject> onLoadDataCompleted) {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject jCardDataObj = new JSONObject();
        try {
            jCardDataObj.put("hometown", cardData.getHometown());
            jCardDataObj.put("address", cardData.getAddress());
            jCardDataObj.put("dob", cardData.getDob());
            jCardDataObj.put("name", cardData.getName());
            jCardDataObj.put("id", cardData.getId());
            jCardDataObj.put("source", cardData.getSource());
            jCardDataObj.put("date", cardData.getDate());
            jCardDataObj.put("gender", cardData.getGender());
            jCardDataObj.put("nationality", cardData.getNationality());
            jCardDataObj.put("expired_date", cardData.getExpiredDate());

            jsonObject.put("ekyc_id", ekycId);
            jsonObject.put("card_data", jCardDataObj);
        } catch (JSONException e) {
            LogUtils.e(e.toString());
        }
        AuthenServiceImp.getInstance().doJsonRequest(InternalConfig.RepositoryModule.INSTANCE.getKycDomain() + CONFIRM_CARD_DATA, jsonObject.toString(), onLoadDataCompleted, BaseRemoteObject.class);
    }

    public static void uploadImages(List<Uri> uriList, List<String> nameList, OnLoadDataCompleted<CardImageKycDto> onLoadDataCompleted) {
        AuthenServiceImp.getInstance().doUploadImageFiles(InternalConfig.RepositoryModule.INSTANCE.getKycUploadDomain() + UPLOAD_IMAGE_API, "file", uriList, nameList, onLoadDataCompleted, CardImageKycDto.class);
    }
}
