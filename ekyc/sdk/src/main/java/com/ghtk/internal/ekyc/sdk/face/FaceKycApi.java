package com.ghtk.internal.ekyc.sdk.face;

import android.content.Context;
import android.view.View;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;

public interface FaceKycApi {
    void startFaceKyc(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView, View viewFace, OnFaceKycDetectListener onFaceKycDetectListener);
    void stopFaceKyc();
}
