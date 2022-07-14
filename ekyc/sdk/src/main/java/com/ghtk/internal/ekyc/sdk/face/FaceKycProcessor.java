package com.ghtk.internal.ekyc.sdk.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.ghtk.internal.detection.Detection;
import com.ghtk.internal.detection.FacialEngine;
import com.ghtk.internal.detection.IFacialDetect;
import com.ghtk.internal.detection.Metrics;
import com.ghtk.logger.LogUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class FaceKycProcessor implements FaceKycApi {
    public final static int ROTATE_TO_LEFT = 100;
    public final static int ROTATE_TO_RIGHT = 200;
    public final static int HEAD_UP = 300;
    public final static int HEAD_DOWN = 400;
    //    public final static int CLOSE_EYE = 500;
    public final static int CLOSE_LEFT_EYE = 600;
    public final static int CLOSE_RIGHT_EYE = 700;
    public final static int SMILE = 800;
    public final static int HEAD_NOMAL = 900;
    //    public final static int ACTION_COUNT = 3;
    public final static int MAX_PROGRESS = 100;
    public final static int SPLIT_ACTION = 3;

    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private VisionImageProcessor imageProcessor;

    private IFacialDetect facialEngine;

    @Override
    public void startFaceKyc(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner, @NonNull PreviewView previewView, View viewFace, View tvLabel, @NonNull OnFaceKycDetectListener onFaceKycDetectListener) {
        facialEngine = new FacialEngine(
                context,
                new Metrics("face_mask_model.tflite", 2, 0.8f)
        );

        final CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(
                () -> {
                    try {
                        bindAllCameraUseCases(context, lifecycleOwner, previewView, tvLabel, cameraProviderFuture.get(), cameraSelector, onFaceKycDetectListener);
                    } catch (ExecutionException | InterruptedException e) {
                        LogUtils.e("Unhandled exception " + e.toString());
                    }
                },
                ContextCompat.getMainExecutor(context));
    }

    @Override
    public void stopFaceKyc() {
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    private void bindAllCameraUseCases(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView, View tvLabel, ProcessCameraProvider cameraProvider, CameraSelector cameraSelector, OnFaceKycDetectListener onFaceKycDetectListener) {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider.unbindAll();
            bindPreviewUseCase(lifecycleOwner, previewView, cameraProvider, cameraSelector);
            bindAnalysisUseCase(context, previewView, tvLabel, lifecycleOwner, cameraProvider, cameraSelector, onFaceKycDetectListener);
        }
    }

    private void bindPreviewUseCase(LifecycleOwner lifecycleOwner, PreviewView previewView, ProcessCameraProvider cameraProvider, CameraSelector cameraSelector) {
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
//        Size targetResolution = new Size(previewView.getMeasuredWidth(), previewView.getMeasuredHeight());
//        builder.setTargetResolution(targetResolution);
        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase);
    }

    private int currentProgress, currentAction;
    private final List<Integer> actions = new ArrayList<>();

    private void resetListFaces() {
        actions.clear();
        actions.add(ROTATE_TO_LEFT);
        actions.add(ROTATE_TO_RIGHT);
//        actions.add(HEAD_UP);
//        actions.add(HEAD_DOWN);
//        actions.add(CLOSE_EYE);
//        actions.add(CLOSE_LEFT_EYE);
//        actions.add(CLOSE_RIGHT_EYE);
//        actions.add(SMILE);
        final Random random = new Random();
        while (actions.size() > 2) {
            actions.remove(actions.get(random.nextInt(actions.size())));
        }
        actions.add(HEAD_NOMAL);
    }

    private int translate(int x, int widthView, int widthImageFaceView) {
        return x * widthView / widthImageFaceView;
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void bindAnalysisUseCase(Context context, PreviewView previewView, View tvLabel, LifecycleOwner lifecycleOwner, ProcessCameraProvider cameraProvider, CameraSelector cameraSelector, OnFaceKycDetectListener onFaceKycDetectListener) {
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }

        resetListFaces();
        FaceDetectorOptions faceDetectorOptions =
                new FaceDetectorOptions.Builder()
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setMinFaceSize(0.9f)
                        .enableTracking()
                        .build();
        currentProgress = 0;
        imageProcessor = new FaceDetectorProcessor(context, faceDetectorOptions, new FaceDetectorProcessor.OnFaceDetect() {
            @Override
            public void onFaceDetect(@NonNull List<Face> faces, Bitmap bitmapOrigin) {
                onFaceKycDetectListener.onFaceDetected(faces);
                if (faces.size() == 1) {
                    final Face face = faces.get(0);


                    final int centerXView = previewView.getLeft() + previewView.getWidth() / 2;
                    final int centerYView = previewView.getTop() + previewView.getHeight() / 2;

                    final int centerXF = translate(face.getBoundingBox().centerX(), previewView.getWidth(), bitmapOrigin.getWidth());
                    final int centerYF = translate(face.getBoundingBox().centerY(), previewView.getWidth(), bitmapOrigin.getWidth());
                    if (Math.abs(centerXF - centerXView) > 200 || Math.abs(centerYF - centerYView) > 200) {
                        return;
                    }

                    Bitmap avatar = cropAvatar(face.getBoundingBox(), bitmapOrigin);
                    if (avatar != null) {
                        Detection detection = facialEngine.detect(avatar);
                        if (detection != Detection.Companion.getEMPTY()) {
                            ((TextView) tvLabel).setText(detection.getCategory().getLabel());
                        }
                    }

                    LogUtils.d("Chinhnq" + "VIEW : " + " X : " + centerXView + " - Y : " + centerYView);
                    LogUtils.d("Chinhnq" + "FACE : " + " X : " + centerXF + " - Y : " + centerYF);
                    float rotY, rotX, closeLeftEye, closeRightEye, smile;
                    int progress;
                    if (actions.size() > 0) {
                        currentAction = actions.get(0);
                        final int count = actions.size();
                        final int doneStepProgress = (SPLIT_ACTION - count + 1) * MAX_PROGRESS / SPLIT_ACTION;
                        onFaceKycDetectListener.onNextAction(currentAction);
                        switch (currentAction) {
                            case HEAD_NOMAL:
                                rotX = face.getHeadEulerAngleX();
                                rotY = face.getHeadEulerAngleY();
                                smile = face.getSmilingProbability();
                                if (Math.abs(rotY) < 3 && Math.abs(rotX) < 10 && smile < 0.15f) {
                                    onFaceKycDetectListener.onUpdateProgress(doneStepProgress);
                                    final Bitmap bitmap = previewView.getBitmap();
                                    onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "front_face");
                                }
                                break;
                            case ROTATE_TO_LEFT:
                                rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                if (rotY > 0) {
                                    progress = (int) ((rotY / 30f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                    if (progress > currentProgress) currentProgress = progress;
                                    if (currentProgress >= doneStepProgress) {
                                        currentProgress = doneStepProgress;
                                        actions.remove(Integer.valueOf(currentAction));
                                        if (actions.size() > 0) {
                                            currentAction = actions.get(0);
                                        }
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                        final Bitmap bitmap = previewView.getBitmap();
                                        onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "left_face");
                                    } else {
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    }
                                }
                                break;
                            case ROTATE_TO_RIGHT:
                                rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                if (rotY < 0) {
                                    progress = (int) ((Math.abs(rotY) / 30f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                    if (progress > currentProgress) currentProgress = progress;
                                    if (currentProgress >= doneStepProgress) {
                                        currentProgress = doneStepProgress;
                                        actions.remove(Integer.valueOf(currentAction));
                                        if (actions.size() > 0) {
                                            currentAction = actions.get(0);
                                        }
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                        final Bitmap bitmap = previewView.getBitmap();
                                        onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "right_face");
                                    } else {
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    }
                                }
                                break;
                            case HEAD_UP:
                                rotX = face.getHeadEulerAngleX();  // Head is rotated to the right rotY degrees
                                if (rotX > 0) {
                                    progress = (int) ((rotX / 15f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                    if (progress > currentProgress) currentProgress = progress;
                                    if (currentProgress >= doneStepProgress) {
                                        currentProgress = doneStepProgress;
                                        actions.remove(Integer.valueOf(currentAction));
                                        if (actions.size() > 0) {
                                            currentAction = actions.get(0);
                                        }
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                        final Bitmap bitmap = previewView.getBitmap();
                                        onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "up_face");
                                    } else {
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    }
                                }
                                break;
                            case HEAD_DOWN:
                                rotX = face.getHeadEulerAngleX();
                                if (rotX < 0) {
                                    progress = (int) ((Math.abs(rotX) / 10f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                    if (progress > currentProgress) currentProgress = progress;
                                    if (currentProgress >= doneStepProgress) {
                                        currentProgress = doneStepProgress;
                                        actions.remove(Integer.valueOf(currentAction));
                                        if (actions.size() > 0) {
                                            currentAction = actions.get(0);
                                        }
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                        final Bitmap bitmap = previewView.getBitmap();
                                        onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "down_face");
                                    } else {
                                        onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    }
                                }
                                break;
//                            case CLOSE_EYE: close_eye
//
//                                break;
                            case CLOSE_LEFT_EYE:
                                closeRightEye = face.getLeftEyeOpenProbability();
                                closeLeftEye = face.getRightEyeOpenProbability();
                                if (closeRightEye > 0.8) {
                                    progress = (int) (((1 - closeLeftEye) / 0.95f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                    if (progress > currentProgress) currentProgress = progress;
                                }
                                if (closeLeftEye > 0 && closeLeftEye < 0.03f && closeRightEye > 0.8) {
                                    currentProgress = doneStepProgress;
                                    actions.remove(Integer.valueOf(currentAction));
                                    if (actions.size() > 0) {
                                        currentAction = actions.get(0);
                                    }
                                    onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    final Bitmap bitmap = previewView.getBitmap();
                                    onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "close_left_eye");
                                } else {
                                    onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                }
                                break;
                            case CLOSE_RIGHT_EYE:
                                closeLeftEye = face.getRightEyeOpenProbability();
                                closeRightEye = face.getLeftEyeOpenProbability();
                                if (closeLeftEye > 0.8) {
                                    progress = (int) (((1 - closeRightEye) / 0.95f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                    if (progress > currentProgress) currentProgress = progress;
                                }
                                if (closeRightEye > 0 && closeRightEye < 0.03f && closeLeftEye > 0.8) {
                                    currentProgress = doneStepProgress;
                                    actions.remove(Integer.valueOf(currentAction));
                                    if (actions.size() > 0) {
                                        currentAction = actions.get(0);
                                    }
                                    onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    final Bitmap bitmap = previewView.getBitmap();
                                    onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "close_right_eye");
                                } else {
                                    onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                }
                                break;
                            case SMILE:
                                smile = face.getSmilingProbability();
                                progress = (int) ((smile / 0.95f) * MAX_PROGRESS / SPLIT_ACTION) + (SPLIT_ACTION - count) * MAX_PROGRESS / SPLIT_ACTION;
                                if (progress > currentProgress) currentProgress = progress;
                                if (smile >= 0.95f) {
                                    currentProgress = doneStepProgress;
                                    actions.remove(Integer.valueOf(currentAction));
                                    if (actions.size() > 0) {
                                        currentAction = actions.get(0);
                                    }
                                    onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                    final Bitmap bitmap = previewView.getBitmap();
                                    onFaceKycDetectListener.onDetectCompleted(currentAction, bitmap, "smile");
                                } else {
                                    onFaceKycDetectListener.onUpdateProgress(currentProgress);
                                }
                                break;
                        }
                    }
                } else {
                    if (currentProgress != 0) {
                        currentProgress = 0;
                        resetListFaces();
                    }
                }
            }
        });

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
//        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
//        if (targetResolution != null) {
//            builder.setTargetResolution(targetResolution);
//        }
        analysisUseCase = builder.build();

        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(context),
                imageProxy -> {

                    try {
                        imageProcessor.processImageProxy(imageProxy);
                    } catch (IllegalStateException e1) {
                        LogUtils.e(e1.getMessage());
                    } catch (MlKitException e) {
                        LogUtils.e(e.getMessage());
                    }
                });

        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ lifecycleOwner, cameraSelector, analysisUseCase);
    }

    private Bitmap cropAvatar(Rect bounding, Bitmap origin) {
        int x = bounding.left;
        int y = bounding.top;
        int width = bounding.width();
        int height = bounding.height();

        if (x >= 0 && y >= 0 && width >= 0 && height >= 0 && y + height <= origin.getHeight() && x + width <= origin.getWidth()) {
            Bitmap cropAvatar = Bitmap.createBitmap(origin, x, y, width, height);
            return cropAvatar;
        }
        return null;
    }
}
