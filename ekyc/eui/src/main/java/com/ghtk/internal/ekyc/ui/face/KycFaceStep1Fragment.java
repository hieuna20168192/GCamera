package com.ghtk.internal.ekyc.ui.face;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.camera.view.LifecycleCameraController;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.base.factory.DialogFactory;
import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.google.gson.Gson;
import com.google.mlkit.vision.face.Face;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.sdk.card.model.CardImageInfo;
import com.ghtk.internal.ekyc.sdk.card.model.CardImageKycDto;
import com.ghtk.internal.ekyc.sdk.face.EKycFaceIdService;
import com.ghtk.internal.ekyc.sdk.face.FaceKycApi;
import com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor;
import com.ghtk.internal.ekyc.sdk.face.OnFaceKycDetectListener;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.card.step.KycCardStep1Fragment;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycFaceStep1Binding;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;
import com.ghtk.internal.base.preference.GHTKSharePreferences;

import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.CLOSE_LEFT_EYE;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.CLOSE_RIGHT_EYE;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.HEAD_DOWN;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.HEAD_NOMAL;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.HEAD_UP;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.ROTATE_TO_LEFT;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.ROTATE_TO_RIGHT;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.SMILE;
import static com.ghtk.internal.ekyc.sdk.face.FaceKycProcessor.SPLIT_ACTION;
import static com.ghtk.internal.ekyc.ui.face.KycFaceStep2Fragment.KEY_IMAGE_DONE;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID_STEPS;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

public class KycFaceStep1Fragment extends BaseFragment<BaseViewModel, FragmentKycFaceStep1Binding> {

    public static final String KEY_IMAGE_URL = "key-image-url";
    public static final String KEY_IMAGE_TYPE = "key-image-type";

    private FaceKycApi faceKycApi;
    private String ekycId = "";
    private String frontFaceImage = "";

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_face_step1;
    }

    @Override
    public void doViewCreated(View view) {
//        ekycId = GHTKSharePreferences.getString(KEY_EKYC_ID_STEPS);
        if (getArguments() != null) {
            if (ekycId.isEmpty()) {
                ekycId = getArguments().getString(KEY_EKYC_ID);
                String phone = getArguments().getString(KEY_PHONE);

                // không tìm thấy ekycId -> back màn xác thực CMT
                if (ekycId == null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_PHONE, phone);
                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycCardStep1Fragment(), bundle, false);
                    return;
                }

                if (ekycId.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_PHONE, phone);
                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycCardStep1Fragment(), bundle, false);
                    return;
                }
            }
        }

        faceKycApi = new FaceKycProcessor();
        binding.txtTitleHeader.setText("Xác thực khuôn mặt");
        binding.txtWarning.setText("Lưu ý:\n" +
                "- Hình ảnh khuôn mặt rõ ràng, không nhắm mắt, không đeo kính hoặc bị che khuất\n" +
                "- Không nên thực hiện trong điều kiện thiếu ánh sáng hoặc chói sáng");
        LifecycleCameraController cameraController = new LifecycleCameraController(getActivity());
//        cameraController.setZoomRatio(1000);
//        cameraController.setLinearZoom(0.1f);
        binding.previewView.setController(cameraController);

        // BACK
        binding.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startFaceDetection();
    }

    private void startFaceDetection() {
        if (faceKycApi == null) {
            return;
        }
        faceKycApi.startFaceKyc(getActivity().getApplicationContext(), this, binding.previewView, binding.faceProgressBar, new OnFaceKycDetectListener() {

            @Override
            public void onFaceDetected(List<Face> faces) {
                switch (faces.size()) {
                    case 0:
                        if (binding.tvWaring.getVisibility() != View.VISIBLE) {
                            binding.tvWaring.setText("Vui lòng đặt khuôn mặt vào khung hình");
                            binding.tvWaring.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                        if (binding.tvWaring.getVisibility() == View.VISIBLE) {
                            binding.tvWaring.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        if (binding.tvWaring.getVisibility() != View.VISIBLE) {
                            binding.tvWaring.setText("Vui lòng di chuyển camera đến khu vực chỉ có mình bạn");
                            binding.tvWaring.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }

            @Override
            public void onNextAction(int currentAction) {
                binding.tvWaring.setVisibility(View.GONE);
                LogUtils.d(currentAction + "- action");
                switch (currentAction) {
                    case HEAD_NOMAL:
                        binding.txtActionDes.setText("Vui lòng nhìn thẳng");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.VISIBLE);
                        binding.imvActionFace.setImageResource(R.drawable.ic_face_nomal);
                        break;
                    case ROTATE_TO_LEFT:
                        binding.txtActionDes.setText("Vui lòng quay mặt từ từ sang trái");
                        binding.imvActionLeft.setVisibility(View.VISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.VISIBLE);
                        binding.imvActionFace.setImageResource(R.drawable.ic_face_left);
                        break;
                    case ROTATE_TO_RIGHT:
                        binding.txtActionDes.setText("Vui lòng quay mặt từ từ sang phải");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.VISIBLE);
                        binding.imvActionFace.setVisibility(View.VISIBLE);
                        binding.imvActionFace.setImageResource(R.drawable.ic_face_right);
                        break;
                    case HEAD_UP:
                        binding.txtActionDes.setText("Vui ngước lên phía trên");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.INVISIBLE);
                        break;
                    case HEAD_DOWN:
                        binding.txtActionDes.setText("Vui ngước xuống dưới");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.INVISIBLE);
                        break;
//                            case CLOSE_EYE:
//
//                                break;
                    case CLOSE_LEFT_EYE:
                        binding.txtActionDes.setText("Nhắm mắt trái");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.INVISIBLE);
                        break;
                    case CLOSE_RIGHT_EYE:
                        binding.txtActionDes.setText("Nhắm mắt phải");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.INVISIBLE);
                        break;
                    case SMILE:
                        binding.txtActionDes.setText("Vui lòng mỉm cười");
                        binding.imvActionLeft.setVisibility(View.INVISIBLE);
                        binding.imvActionRight.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setVisibility(View.INVISIBLE);
                        binding.imvActionFace.setImageResource(R.drawable.ic_face_nomal);
                        break;
                }
            }

            @Override
            public void onUpdateProgress(int progress) {
                binding.faceProgressBar.setProgress(progress);
                LogUtils.d(progress + "- progress");
            }
            private LinkedHashMap<String, Bitmap> bitmapListMap = new LinkedHashMap<>();
            @Override
            public void onDetectCompleted(int action, Bitmap b1, String name) {
                LogUtils.d(action + "- onDetectCompleted");
                bitmapListMap.put(name, b1);
                if (bitmapListMap.size() == SPLIT_ACTION) {
//                    showLoadingView();
                    final List<Bitmap> bitmapList = new ArrayList<>(bitmapListMap.values());
                    final List<String> bitmapListName = new ArrayList<>(bitmapListMap.keySet());
                    faceKycApi.stopFaceKyc();

                    if (ekycId.isEmpty()) {
                        DialogFactory.showConfirmDialog(getActivity(),
                                "Thông báo",
                                "Không tìm thấy ekyc-id, vui lòng " +
                                        "quay lại xác thực CMND/Căn Cước",
                                (dialog, which) -> {
                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycCardStep1Fragment(), null, false);
                                });
                        return;
                    }

                    uploadImageGetUrl(bitmapList, bitmapListName);
                }
            }
        });
    }

    private void uploadImageGetUrl(List<Bitmap> bitmapList, List<String> nameList) {
        // thêm đuôi key ảnh
        List<String> nameKeyList = new ArrayList<>();
        for (String name : nameList) {
            String nameBuilder = name +
                    ".jpg";
            nameKeyList.add(nameBuilder);
        }

        showLoadingView();
        EKycFaceIdService.uploadImagesGetUrl(bitmapList, nameKeyList, new OnLoadDataCompleted<CardImageKycDto>() {
            @Override
            public void onFinish(CardImageKycDto result) {
                hideLoadingView();
                if (result == null) {
                    return;
                }

                if (result.getData() == null) {
                    return;
                }

                if (result.getData().size() > 0) {
                    final List<String> urlFaceList = new ArrayList<>();
                    final List<String> imageTypeList = new ArrayList<>();

                    for (CardImageInfo cardImageInfo : result.getData()) {
                        urlFaceList.add(cardImageInfo.getUrl());

                        final String[] nameKeys = cardImageInfo.getName().split("\\.");
                        if (nameKeys.length > 0) {
                            imageTypeList.add(nameKeys[0]);
                        }

                        if (cardImageInfo.getName().contains("front_face")) {
                            frontFaceImage = cardImageInfo.getUrl();
                        }
                    }

                    if (urlFaceList.size() == 0 || imageTypeList.size() == 0) {
                        showErrorMessage("Không lấy được link ảnh", null);
                        return;
                    }

                    if (urlFaceList.size() != imageTypeList.size()) {
                        showErrorMessage("link ảnh và số lượng type ảnh khác nhau", null);
                        return;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_PHONE, getArguments().getString(KEY_PHONE));
                    bundle.putString(KEY_IMAGE_DONE, frontFaceImage);
                    bundle.putString(KEY_EKYC_ID, ekycId);
                    bundle.putString(KEY_IMAGE_URL, new Gson().toJson(urlFaceList));
                    bundle.putString(KEY_IMAGE_TYPE, new Gson().toJson(imageTypeList));

                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep2Fragment(), bundle, true);
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                hideLoadingView();
                showErrorMessage(message, null);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (faceKycApi != null) {
            faceKycApi.stopFaceKyc();
        }
    }
}
