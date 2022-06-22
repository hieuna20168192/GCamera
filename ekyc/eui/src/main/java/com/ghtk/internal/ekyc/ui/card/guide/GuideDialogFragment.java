package com.ghtk.internal.ekyc.ui.card.guide;

import android.view.View;

import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.base.fragment.dialog.BaseDialogFragment;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.databinding.DialogFragmentGuideBinding;

// hướng dẫn upload CMT
public class GuideDialogFragment extends BaseDialogFragment<BaseViewModel, DialogFragmentGuideBinding> {

    public static GuideDialogFragment newInstance() {
        return new GuideDialogFragment();
    }

    @Override
    public int getIdLayout() {
        return R.layout.dialog_fragment_guide;
    }

    @Override
    protected float getWidth() {
        return 0.9f;
    }

    @Override
    protected float getHeight() {
        return 0f;
    }

    @Override
    public void doViewCreated(View view) {
        binding.tvOk.setOnClickListener(v -> dismissAllowingStateLoss());
    }
}
