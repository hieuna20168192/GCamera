package com.ghtk.internal.ekyc.ui.otp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ghtk.internal.base.factory.MoshiFactory;
import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.internalconfig.InternalConfig;
import com.ghtk.internal.model.remote.local.AuthenDataLocal;
import com.ghtk.internal.model.remote.login.LoginResponseDto;

import com.ghtk.internal.repository.token.AuthenServiceImp;
import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.sdk.otp.EKycOtpService;
import com.ghtk.internal.ekyc.sdk.otp.model.OtpGenerationDto;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.card.step.KycCardStep1Fragment;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycPhoneStep1Binding;
import com.ghtk.internal.ekyc.ui.face.KycFaceStep1Fragment;
import com.ghtk.internal.ekyc.ui.face.KycFaceStep3Fragment;
import com.ghtk.internal.ekyc.ui.otp.model.PhoneStep;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;
import com.ghtk.internal.base.preference.GHTKSharePreferences;

public class KycPhoneStep1Fragment extends BaseFragment<BaseViewModel, FragmentKycPhoneStep1Binding> {
    public final static String KEY_PHONE = "ekyc_kyc_phone";
    //    public final static String KEY_PHONE_STEPS = "ekyc_kyc_phone_steps";
    public final static String KEY_EKYC_ID = "ekyc_ekyc_id";

    //public final static String KEY_PHONE_STEPS = "ekyc_kyc_phone_steps";
    public final static String KEY_EKYC_ID_STEPS = "ekyc_ekyc_id_steps";

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_phone_step1;
    }

    @Override
    public void doViewCreated(View view) {

        binding.header.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().onBackPressed();
            }
        });
        binding.header.txtTitleHeader.setText("Xác thực Số điện thoại");
        binding.btnVerify.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (TextUtils.isEmpty(binding.edtUsername.getText())) {
                    binding.edtUsername.setError("Vui lòng nhập SĐT của bạn!");
//                    binding.edtUsername.setFocusable(true);
                    showKeyBroad(binding.edtUsername);
                    return;
                }
                showLoadingView();
                EKycOtpService.authenDemo(new OnLoadDataCompleted<LoginResponseDto>() {
                    @Override
                    public void onFinish(LoginResponseDto result) {
                        AuthenServiceImp.getInstance().saveLoginData(InternalConfig.RepositoryModule.INSTANCE.getHostUrl(), null, null, result, new OnLoadDataCompleted<AuthenDataLocal>() {
                            @Override
                            public void onFinish(AuthenDataLocal result) {

                                final String phoneSteps = GHTKSharePreferences.getString(binding.edtUsername.getText().toString().trim());
                                if (phoneSteps.isEmpty()) {
                                    EKycOtpService.generationOtp(binding.edtUsername.getText().toString().trim(), "GHTK", "Kich hoat tai khoan. Nhap OTP {}. Khong gui cho nguoi khac", 600, new OnLoadDataCompleted<OtpGenerationDto>() {
                                        @Override
                                        public void onFinish(OtpGenerationDto result) {
                                            hideLoadingView();
                                            final Bundle bundle = new Bundle();
                                            bundle.putString(KEY_PHONE, binding.edtUsername.getText().toString().trim());
                                            FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycPhoneStep2Fragment(), bundle, true);
                                        }

                                        @Override
                                        public void onError(int errorCode, String message) {
                                            hideLoadingView();
                                            showErrorMessage(message, null);
                                        }
                                    });
                                } else {
                                    hideLoadingView();
                                    MoshiFactory.getDataFromJsonObject(phoneSteps, PhoneStep.class, new com.ghtk.internal.base.remote.OnLoadDataCompleted<PhoneStep>() {
                                        @Override
                                        public void onFinish(PhoneStep result) {
                                            switch (result.getStep()) {
                                                case 1:
                                                    final Bundle bundle = new Bundle();
                                                    bundle.putString(KEY_PHONE, binding.edtUsername.getText().toString().trim());
                                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycCardStep1Fragment(), bundle, true);
                                                    break;
                                                case 2:
                                                    final Bundle bundleFace = new Bundle();
                                                    bundleFace.putString(KEY_PHONE, binding.edtUsername.getText().toString().trim());
                                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep1Fragment(), bundleFace, true);
                                                    break;
                                                case 3:
                                                    final Bundle bundleCompleted = new Bundle();
                                                    bundleCompleted.putString(KEY_PHONE, binding.edtUsername.getText().toString().trim());
                                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep3Fragment(), bundleCompleted, true);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    });
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
                    public void onError(int errorCode, String message) {
                        hideLoadingView();
                        showErrorMessage(message, null);
                    }
                });

            }
        });
    }
}
