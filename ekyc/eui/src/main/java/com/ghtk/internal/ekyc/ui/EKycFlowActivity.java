package com.ghtk.internal.ekyc.ui;

import android.view.View;

import com.ghtk.internal.base.BaseActivity;
import com.ghtk.internal.base.fragment.BaseViewModel;

import com.ghtk.internal.ekyc.ui.face.KycFaceStep1Fragment;
import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.ui.databinding.ActivityEKycBinding;
import com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment;

public class EKycFlowActivity extends BaseActivity<BaseViewModel, ActivityEKycBinding> {

    @Override
    public int getIdLayout() {
        return R.layout.activity_e_kyc;
    }

    @Override
    public void doViewCreated(View view) {
        FragmentUtils.pushFragment(this, R.id.host_frame, new KycFaceStep1Fragment(), null, false);
    }
}
