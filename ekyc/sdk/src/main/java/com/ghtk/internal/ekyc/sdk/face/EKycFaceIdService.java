package com.ghtk.internal.ekyc.sdk.face;

import android.graphics.Bitmap;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.internalconfig.InternalConfig;
import com.ghtk.internal.repository.token.AuthenServiceImp;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.model.remote.BaseRemoteObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.ghtk.internal.ekyc.sdk.card.model.CardImageKycDto;

public class EKycFaceIdService {
    public final static String CONFIRM_CARD_DATA = "/api/v1/confirm-card-data";
    public final static String EKYC_PROFILE = "/api/v1/ekyc-profile";
    public final static String UPLOAD_IMAGE_API = "/file/upload";

    public static void uploadImagesFaceId(String ekycId, List<String> typeImageList, List<String> urlImageList,
                                          OnLoadDataCompleted<BaseRemoteObject> onLoadDataCompleted) {

        final JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArrayImage = new JSONArray();
            for (int i = 0, length = typeImageList.size(); i < length; i++) {
                final JSONObject urlObj = new JSONObject();
                urlObj.put("type", typeImageList.get(i));
                urlObj.put("image_url", urlImageList.get(i));

                jsonArrayImage.put(urlObj);
            }

            jsonObject.put("user_id", "test");
            jsonObject.put("images", jsonArrayImage);

        } catch (JSONException e) {
            LogUtils.e(e.toString());
        }

        AuthenServiceImp.getInstance().doJsonRequest(getUrlFaceLiveUpload(ekycId), jsonObject.toString(), onLoadDataCompleted, BaseRemoteObject.class);
    }

    public static void uploadImagesGetUrl(List<Bitmap> bitmapList, List<String> nameList, OnLoadDataCompleted<CardImageKycDto> onLoadDataCompleted) {
        AuthenServiceImp.getInstance().doUploadBitmaps(InternalConfig.RepositoryModule.INSTANCE.getKycUploadDomain() + UPLOAD_IMAGE_API, "file", bitmapList, nameList, onLoadDataCompleted, CardImageKycDto.class);
    }

    public static String getUrlFaceLiveUpload(String ekycId) {
        return InternalConfig.RepositoryModule.INSTANCE.getKycDomain() + "/api/v1/ekyc/" + ekycId + "/face-liveness";
    }
}
