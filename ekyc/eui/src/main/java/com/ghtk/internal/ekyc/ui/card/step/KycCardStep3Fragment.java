package com.ghtk.internal.ekyc.ui.card.step;

import android.os.Bundle;
import android.view.View;

import com.ghtk.internal.base.factory.MoshiFactory;

import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.ui.face.KycFaceStep1Fragment;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycIdentityCardStep3Binding;
import com.ghtk.internal.ekyc.ui.otp.model.PhoneStep;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;
import com.ghtk.internal.base.preference.GHTKSharePreferences;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID_STEPS;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

public class KycCardStep3Fragment extends BaseFragment<BaseViewModel, FragmentKycIdentityCardStep3Binding> {

    private String phone = "";
    private String ekycId = "";

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_identity_card_step3;
    }

    @Override
    public void doViewCreated(View view) {
        if (getArguments() != null) {
            phone = getArguments().getString(KEY_PHONE);
            ekycId = getArguments().getString(KEY_EKYC_ID);

            final PhoneStep phoneStep = new PhoneStep();
            phoneStep.setPhoneNumber(phone);
            phoneStep.setStep(2); // done 2
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

            GHTKSharePreferences.putString(KEY_EKYC_ID_STEPS, ekycId);
        }

        // BACK
        binding.header.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // NEXT
        binding.btnNext.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_PHONE, phone);
                bundle.putString(KEY_EKYC_ID, ekycId);

                FragmentUtils.pushFragment(getActivity(), R.id.host_frame, new KycFaceStep1Fragment(),
                        bundle, true);
            }
        });
    }
}
