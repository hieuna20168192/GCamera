package com.ghtk.internal.ekyc.ui.card.step;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.Nullable;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.base.factory.GlideFactory;
import com.ghtk.internal.base.factory.MoshiFactory;

import java.util.ArrayList;
import java.util.List;

import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.sdk.card.EKycCardIdService;
import com.ghtk.internal.ekyc.sdk.card.model.CardIDModel;
import com.ghtk.internal.ekyc.sdk.card.model.CardImageKycDto;
import com.ghtk.internal.ekyc.sdk.card.model.CardInfoKycDto;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.card.camera.CameraDetectCardActivity;
import com.ghtk.internal.ekyc.ui.card.guide.GuideDialogFragment;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycIdentityCardStep1Binding;
import com.ghtk.internal.ekyc.ui.face.KycFaceStep1Fragment;
import com.ghtk.internal.ekyc.ui.otp.model.PhoneStep;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;
import com.ghtk.internal.base.preference.GHTKSharePreferences;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

@SuppressLint("SetTextI18n")
// step 1 upload CMT
public class KycCardStep1Fragment extends BaseFragment<BaseViewModel, FragmentKycIdentityCardStep1Binding> {

    public static final String KEY_CARD_INFO_SYC_DTO = "KEY_CARD_ID_MODEL";
    public final static String KEY_PHONE_STEPS = "ekyc_kyc_phone_steps";

    private static final int REQUEST_CODE_IMAGE_BEFORE = 123;

    private static final int REQUEST_CODE_IMAGE_AFTER = 567;

    private CardIDModel cardIDModel = null;
    private CardInfoKycDto cardInfoKycDto = null;

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_identity_card_step1;
    }

    @Override
    public void doViewCreated(View view) {
        // get phone number
        String phoneNumber = "";
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(KEY_PHONE);
        }

        final String phoneSteps = GHTKSharePreferences.getString(KEY_PHONE_STEPS);
        if (!phoneSteps.isEmpty()) {

            String finalPhoneNumber = phoneNumber;
            MoshiFactory.getDataFromJsonObject(phoneSteps, PhoneStep.class, result -> {
                if (result.getStep() == 2) {
                    final Bundle bundleFace = new Bundle();
                    bundleFace.putString(KEY_PHONE, finalPhoneNumber);
                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep1Fragment(), bundleFace, false);
                }
            });
        }

        cardIDModel = new CardIDModel();
        cardIDModel.setPhone(phoneNumber);

        // lưu ý
        binding.tvGuide.setText("*Lưu ý: \n" +
                "- Bấm vào khung hình camera để mở màn hình chụp ảnh \n" +
                "- Đặt đúng chiều căn cước công dân và đưa vào khung được đánh dấu trên màn hình chụp ảnh");

        // hướng dẫn
        final String sourceTitleIntro = "Vui lòng tải lên ảnh chụp căn cước công dân của bạn. Hướng dẫn";
        final String input = "Hướng dẫn";
        Spannable spannable = formatString(sourceTitleIntro, input, getResources().getDimensionPixelSize(R.dimen.sp_17),
                getResources().getColor(R.color.colorPrimary));
        binding.txtTitle.setText(spannable);
        binding.txtTitle.setOnClickListener(v -> {
            GuideDialogFragment guideDialogFragment =
                    GuideDialogFragment.newInstance();
            showDialogFragment(guideDialogFragment);
        });

        initEvents();
    }

    private void initEvents() {
        // ĐÓNG
        binding.llHeader.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // CHỤP ẢNH TRƯỚC
        binding.llOpenCameraPrevious.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intentPrevious = new Intent(getContext(), CameraDetectCardActivity.class);
                startActivityForResult(intentPrevious, REQUEST_CODE_IMAGE_BEFORE);
            }
        });

        // CHỤP ẢNH SAU
        binding.llOpenCameraBehind.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intentPrevious = new Intent(getContext(), CameraDetectCardActivity.class);
                startActivityForResult(intentPrevious, REQUEST_CODE_IMAGE_AFTER);
            }
        });

        // XÁC NHẬN
        binding.btnConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (cardIDModel == null) {
                    return;
                }

                if (TextUtils.isEmpty(cardIDModel.getPhone())) {
                    showErrorMessage("Không tìm thấy số điện thoại", null);
                    return;
                }

                if (TextUtils.isEmpty(cardIDModel.getPeopleIdBeforeUrl())) {
                    showErrorMessage("Chưa có mặt trước của CMND", null);
                    return;
                }

                if (TextUtils.isEmpty(cardIDModel.getPeopleIdAfterUrl())) {
                    showErrorMessage("Chưa có mặt sau của CMND", null);
                    return;
                }

                getProfileInfo(cardIDModel);
            }
        });
    }

    private void getProfileInfo(CardIDModel model) {
        if (model == null) {
            return;
        }

        final List<String> cardImages = new ArrayList<>();
        cardImages.add(model.getPeopleIdBeforeUrl());
        cardImages.add(model.getPeopleIdAfterUrl());

        showLoadingView();
        EKycCardIdService.ekycProfile(model.getPhone(), cardImages, new OnLoadDataCompleted<CardInfoKycDto>() {
            @Override
            public void onFinish(CardInfoKycDto result) {
                hideLoadingView();

                if (result != null && isScreenAlive() && binding != null) {
                    cardInfoKycDto = result;

                    if (!result.isSuccess()) {
                        showPopupMessage("Thông báo", result.getMessage(), null);
                    } else {
                        if (result.getData() == null) {
                            showPopupMessage("Thông báo", "data null", null);
                            return;
                        }

                        if (result.getData().getStatus().equals("card_pending")) {
                            showPopupMessage("Thông báo", "Đang chờ xử lí ảnh CMND/CCCD", null);
                            return;
                        }

                        if (result.getData().getStatus().equals("confirm_pending")) {
                            showPopupMessage("Thông báo", "Đang chờ xử lí thông tin được cung cấp", null);
                            return;
                        }

                        // chuyển sang màn confirm thông tin CMT
                        if (result.getData().getStatus().equals("initial") ||
                                result.getData().getStatus().equals("card_matched") || result.getData().getStatus().equals("confirm_rejected")) {
                            showPopupMessage("Thông báo", "Ảnh CMND/CCCD hợp lệ, khởi tạo hồ sơ thành công", null);

                            final Bundle bundle = new Bundle();
                            bundle.putString(KEY_PHONE, cardIDModel.getPhone());
                            MoshiFactory.convertToJsonString(cardInfoKycDto, CardInfoKycDto.class, new com.ghtk.internal.base.remote.OnLoadDataCompleted<String>() {
                                @Override
                                public void onFinish(String result) {
                                    bundle.putString(KEY_CARD_INFO_SYC_DTO, result);
                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycCardStep2Fragment(), bundle, true);
                                }
                            });
                            return;
                        }

                        // chuyển sang màn xác thực khuôn mặt
                        if (result.getData().getStatus().equals("confirm_matched")) {
                            showPopupMessage("Thông báo", "Thông tin hợp lệ", null);

                            final Bundle bundle = new Bundle();
                            bundle.putString(KEY_PHONE, cardIDModel.getPhone());
                            bundle.putString(KEY_EKYC_ID, cardInfoKycDto.getData().getEkycId());
                            FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep1Fragment(), bundle, true);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                hideLoadingView();
                showErrorMessage(message, null);
            }
        });
    }

    public static Spannable formatString(String sourceString, String input, int textSize, int color) {
        Spannable spannable = new SpannableString(sourceString);
        int index = sourceString.indexOf(input);
        if (index >= 0) {
            spannable.setSpan(new ForegroundColorSpan(color), index, index + input.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new UnderlineSpan(), index, index + input.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AbsoluteSizeSpan(textSize), index, index + input.length(), 0); // set size
        }
        return spannable;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (data == null) {
            return;
        }

        if (resultCode != CameraDetectCardActivity.CAMERA_RESULT_CODE) {
            return;
        }

        final Uri fileUri = Uri.parse(data.getStringExtra("data"));
        if (fileUri == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_IMAGE_BEFORE) {
            uploadImage(REQUEST_CODE_IMAGE_BEFORE, fileUri);
        } else if (requestCode == REQUEST_CODE_IMAGE_AFTER) {
            uploadImage(REQUEST_CODE_IMAGE_AFTER, fileUri);
        }
    }

    // show loading view
    private void uploadImage(int requestCode, Uri uri) {
        final List<Uri> uriList = new ArrayList<>();
        uriList.add(uri);

        final List<String> nameList = new ArrayList<>();
        if (requestCode == REQUEST_CODE_IMAGE_BEFORE) {
            nameList.add("before");
        } else if (requestCode == REQUEST_CODE_IMAGE_AFTER) {
            nameList.add("after");
        }

        showLoadingView();
        EKycCardIdService.uploadImages(uriList, nameList, new OnLoadDataCompleted<CardImageKycDto>() {
            @Override
            public void onFinish(CardImageKycDto result) {
                if (!isScreenAlive()) {
                    return;
                }

                try {
                    hideLoadingView();

                    String urlImage;
                    if (result.getData().size() > 0) {
                        urlImage = result.getData().get(0).getUrl();
                    } else {
                        urlImage = "";
                    }

                    if (requestCode == REQUEST_CODE_IMAGE_BEFORE) {
                        GlideFactory.loadImage(urlImage, binding.ivImageIdPrevious);
                        binding.ivIconCameraIdPrevious.setVisibility(View.GONE);
                        binding.ivIconCameraIdGrayPrevious.setVisibility(View.VISIBLE);

                        cardIDModel.setPeopleIdBeforeUrl(urlImage);
                    } else if (requestCode == REQUEST_CODE_IMAGE_AFTER) {
                        GlideFactory.loadImage(urlImage, binding.ivImageIdBehind);
                        binding.ivIconCameraIdBehind.setVisibility(View.GONE);
                        binding.ivIconCameraIdGrayBehind.setVisibility(View.VISIBLE);

                        cardIDModel.setPeopleIdAfterUrl(urlImage);
                    }
                } catch (Exception e) {
                    LogUtils.e(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                hideLoadingView();
                showErrorMessage(message, null);
            }
        });
    }
}
