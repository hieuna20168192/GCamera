package com.ghtk.internal.ekyc.ui.face;

import android.view.View;

import com.ghtk.internal.base.factory.MoshiFactory;
import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;

import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycFaceStep3Binding;
import com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment;
import com.ghtk.internal.ekyc.ui.otp.model.PhoneStep;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;
import com.ghtk.internal.base.preference.GHTKSharePreferences;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

public class KycFaceStep3Fragment extends BaseFragment<BaseViewModel, FragmentKycFaceStep3Binding> {

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_face_step3;
    }

    @Override
    public void doViewCreated(View view) {
        binding.header.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().onBackPressed();
            }
        });
        binding.header.txtTitleHeader.setText("Xác thực Khuôn mặt");
        binding.btnNext.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycPhoneStep1Fragment(), null, false);
            }
        });

        final PhoneStep phoneStep = new PhoneStep();
        final String phone = getArguments().getString(KEY_PHONE);
        phoneStep.setPhoneNumber(phone);
        phoneStep.setStep(3); // done 2
        MoshiFactory.convertToJsonString(phoneStep, PhoneStep.class, new com.ghtk.internal.base.remote.OnLoadDataCompleted<String>() {
            @Override
            public void onFinish(String result) {
                GHTKSharePreferences.putString(phone, result);
            }

            @Override
            public void onError(int errorCode, String message) {

            }
        });
    }
}
