package com.ghtk.internal.ekyc.ui.otp;

import android.os.Bundle;
import android.view.View;

import com.ghtk.internal.base.factory.MoshiFactory;
import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.card.step.KycCardStep1Fragment;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycPhoneStep3Binding;
import com.ghtk.internal.ekyc.ui.otp.model.PhoneStep;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;
import com.ghtk.internal.base.preference.GHTKSharePreferences;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

public class KycPhoneStep3Fragment extends BaseFragment<BaseViewModel, FragmentKycPhoneStep3Binding> {

    private String phone = "";

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_phone_step3;
    }

    @Override
    public void doViewCreated(View view) {
        binding.header.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().onBackPressed();
            }
        });
        if (getArguments() != null) {
            phone = getArguments().getString(KEY_PHONE);
            final PhoneStep phoneStep = new PhoneStep();
            phoneStep.setPhoneNumber(phone);
            phoneStep.setStep(1);
            MoshiFactory.convertToJsonString(phoneStep, PhoneStep.class, new OnLoadDataCompleted<String>() {
                @Override
                public void onFinish(String result) {
                    GHTKSharePreferences.putString(phone, result);
                }

                @Override
                public void onError(int errorCode, String message) {
                    OnLoadDataCompleted.super.onError(errorCode, message);
                }
            });
        }
        binding.header.txtTitleHeader.setText("Xác thực Số điện thoại");
        binding.btnNext.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final Bundle bundle = new Bundle();
                bundle.putString(KEY_PHONE, phone);
                FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycCardStep1Fragment(),
                        bundle, false);
            }
        });
    }
}
