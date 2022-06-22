package com.ghtk.internal.ekyc.ui.otp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.model.remote.BaseRemoteObject;

import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.sdk.otp.EKycOtpService;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycPhoneStep2Binding;
import com.ghtk.internal.ekyc.ui.views.OnCompleteListener;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

public class KycPhoneStep2Fragment extends BaseFragment<BaseViewModel, FragmentKycPhoneStep2Binding> {

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_phone_step2;
    }

    @Override
    public void doViewCreated(View view) {
        binding.header.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().onBackPressed();
            }
        });
        final Bundle bundle = new Bundle();
        if (getArguments() != null) {
            bundle.putString(KEY_PHONE, getArguments().getString(KEY_PHONE)); // put tạm số điện thoại sang
        }
        binding.header.txtTitleHeader.setText("Xác thực Số điện thoại");
        binding.otpView.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(String value) {
                showLoadingView();
                EKycOtpService.verificationOtp(getArguments().getString(KEY_PHONE), "GHTK", value, new OnLoadDataCompleted<BaseRemoteObject>() {
                    @Override
                    public void onFinish(BaseRemoteObject result) {
                        hideLoadingView();
                        if (result.isSuccess()) {
                            hideKeyBroad();
                            FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycPhoneStep3Fragment(), bundle, false);
                        } else {
                            showErrorMessage(result.getMessage(), null);
                            FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycPhoneStep3Fragment(), bundle, false);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        hideLoadingView();
                        showErrorMessage(message, new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                binding.otpView.setText("");
                                showKeyBroad(binding.otpView);
                            }
                        });
                    }
                });
            }
        });
        binding.otpView.post(new Runnable() {
            @Override
            public void run() {
                showKeyBroad(binding.otpView);
            }
        });
    }
}
