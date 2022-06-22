package com.ghtk.internal.ekyc.ui.face;

import android.os.Bundle;
import android.view.View;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.base.factory.DialogFactory;
import com.ghtk.internal.base.factory.GlideFactory;
import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.model.remote.BaseRemoteObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.utils.thread.GRunnable;
import com.ghtk.internal.utils.thread.GWorkerThread;
import com.ghtk.internal.utils.thread.OnCompleted;
import com.ghtk.internal.ekyc.sdk.face.EKycFaceIdService;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycFaceStep2Binding;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

public class KycFaceStep2Fragment extends BaseFragment<BaseViewModel, FragmentKycFaceStep2Binding> {

    public static final String KEY_IMAGE_DONE = "key_image_done";
    public String ekycId = "";

    final List<String> imageUrlList = new ArrayList<>();
    final List<String> imageTypeList = new ArrayList<>();

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_face_step2;
    }

    @Override
    public void doViewCreated(View view) {
        if (getArguments() != null) {
            final String imageDone = getArguments().getString(KEY_IMAGE_DONE);
            ekycId = getArguments().getString(KEY_EKYC_ID);

            String jsonImageUrl = getArguments().getString(KycFaceStep1Fragment.KEY_IMAGE_URL);
            String jsonImageType = getArguments().getString(KycFaceStep1Fragment.KEY_IMAGE_TYPE);

            GWorkerThread.doOnWorkerThread(new GRunnable() {
                @Override
                public List<String> run() {
                    return new Gson().fromJson(jsonImageUrl, new TypeToken<List<String>>(){}.getType());
                }
            }, new OnCompleted<List<String>>() {
                @Override
                public void onFinish(List<String> object) {
                    imageUrlList.clear();
                    imageUrlList.addAll(object);
                }

                @Override
                public void onError(String error) {
                    showErrorMessage(error, null);
                }
            });

            GWorkerThread.doOnWorkerThread(new GRunnable() {
                @Override
                public List<String> run() {
                    return new Gson().fromJson(jsonImageType, new TypeToken<List<String>>(){}.getType());
                }
            }, new OnCompleted<List<String>>() {
                @Override
                public void onFinish(List<String> object) {
                    imageTypeList.clear();
                    imageTypeList.addAll(object);
                }

                @Override
                public void onError(String error) {
                    showErrorMessage(error, null);
                }
            });

            GlideFactory.loadImage(imageDone, binding.imvDone);
        }

        binding.header.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        binding.btnPrevious.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            //
            uploadImageFaceId(imageUrlList, imageTypeList);
        });

        binding.header.txtTitleHeader.setText("Xác thực Khuôn mặt");

        binding.txtWarning.setText("Lưu ý:\n" +
                "- Hình ảnh khuôn mặt rõ ràng, không nhắm mắt, không đeo kính hoặc bị che khuất\n" +
                "- Không nên thực hiện trong điều kiện thiếu ánh sáng hoặc chói sáng");
    }

    private void uploadImageFaceId(List<String> imageUrlList, List<String> imageTypeList) {
        if (ekycId.isEmpty()) {
            DialogFactory.showErrorMessage(getActivity(), "ekyc-id null", null);
            return;
        }

        showLoadingView();
        EKycFaceIdService.uploadImagesFaceId(ekycId, imageTypeList, imageUrlList, new OnLoadDataCompleted<BaseRemoteObject>() {
            @Override
            public void onFinish(BaseRemoteObject result) {
                hideLoadingView();
                LogUtils.d("OK");
                Bundle bundle = new Bundle();
                bundle.putString(KEY_PHONE, getArguments().getString(KEY_PHONE));
                FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep3Fragment(), bundle, true);
            }

            @Override
            public void onError(int errorCode, String message) {
                hideLoadingView();
                showErrorMessage(message, null);
            }

        });
    }
}
