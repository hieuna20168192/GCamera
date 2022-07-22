package com.ghtk.internal.ekyc.sdk.face;

import android.graphics.Bitmap;

import com.google.mlkit.vision.face.Face;

import java.util.List;

public interface OnFaceKycDetectListener {
    void onFaceDetected(List<Face> faces, boolean isMaskOrGlass);
    void onNextAction(int action);
    void onUpdateProgress(int progress);
    void onDetectCompleted(int action, Bitmap b1, String name);
    void onDetectMaskOrGlass();
}
