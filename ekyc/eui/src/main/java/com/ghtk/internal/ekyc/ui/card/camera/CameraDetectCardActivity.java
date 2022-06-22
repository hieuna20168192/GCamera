package com.ghtk.internal.ekyc.ui.card.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ghtk.internal.base.factory.DialogFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.PictureFormat;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

import com.ghtk.internal.utils.factory.PermissionUtils;
import com.ghtk.internal.ekyc.ui.R;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

// camera chụp ảnh CMT
public class CameraDetectCardActivity extends AppCompatActivity {

    public static int CAMERA_RESULT_CODE = 2703196;

    private CameraView mCameraView;
    private View flashButton;
    private ImageView takePhoto;
    private ImageView ivImageId;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Bitmap objectThumbnailForBottomSheet;

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, System.currentTimeMillis() + "CMND", null);
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPause() {
        if (mCameraView != null) {
            mCameraView.close();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mCameraView != null) {
            mCameraView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_card_id);

        initViews();
        initEvents();

        setUpBottomSheet();
        setUpCamera();

        checkPermissionCamera();
    }

    private void setUpCamera() {
        mCameraView.setLifecycleOwner(this);
        mCameraView.setPictureFormat(PictureFormat.JPEG);
        mCameraView.setUseDeviceOrientation(false);

        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                result.toBitmap(bitmap -> {
                    if (bitmap == null) {
                        DialogFactory.showMessage(CameraDetectCardActivity.this, "Thông báo",
                                "Có lỗi xảy ra, vui lòng thử lại", null);
                        return;
                    }

                    mCameraView.close();
                    objectThumbnailForBottomSheet = bitmap;

                    bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelOffset(R.dimen.dp_100));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    takePhoto.setVisibility(View.GONE);
                    ivImageId.setVisibility(View.VISIBLE);
                    ivImageId.setImageBitmap(objectThumbnailForBottomSheet);
                });
            }


        });
    }

    private void initEvents() {
        // đóng
        findViewById(R.id.close_button).setOnClickListener(v -> {
            if (mCameraView != null) {
                mCameraView.destroy();
            }

            finish();
        });

        // xác nhận chụp ảnh
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (getImageUri(getApplicationContext(), objectThumbnailForBottomSheet) != null) {
                Intent intent = new Intent();
                intent.putExtra("data", getImageUri(getApplicationContext(),
                        objectThumbnailForBottomSheet).toString());
                setResult(CameraDetectCardActivity.CAMERA_RESULT_CODE, intent);
                finish();
            } else {
                DialogFactory.showMessage(CameraDetectCardActivity.this, "Thông báo",
                        "Có lỗi xảy ra, vui lòng thử lại", null);
            }
        });

        // flash button
        findViewById(R.id.flash_button).setOnClickListener(v -> {
            if (flashButton.isSelected()) {
                flashButton.setSelected(false);
                mCameraView.setFlash(Flash.OFF);
            } else {
                flashButton.setSelected(true);
                mCameraView.setFlash(Flash.TORCH);
            }
        });

        // chụp lại
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            takePhoto.setVisibility(View.VISIBLE);
            ivImageId.setVisibility(View.GONE);
            mCameraView.open();
        });

        // chụp ảnh
        takePhoto.setOnClickListener(v -> {
            if (mCameraView.isTakingPicture()) {
                return;
            }
            mCameraView.takePictureSnapshot();
        });
    }

    private void initViews() {
        RelativeLayout mLayoutCropView = findViewById(R.id.layout_crop_view);
        mLayoutCropView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));

        mCameraView = findViewById(R.id.camera_view);
        flashButton = findViewById(R.id.flash_button);
        flashButton.setSelected(false);
        ivImageId = findViewById(R.id.iv_image_id);
        takePhoto = findViewById(R.id.takePhoto);
    }

    private void setUpBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    ivImageId.setVisibility(View.GONE);
                    takePhoto.setVisibility(View.VISIBLE);
                    mCameraView.open();
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void checkPermissionCamera() {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!PermissionUtils.hasPermissions(this, permissions)) {
            PermissionUtils.hasPermissions(this, permissions, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            for (int granResult : grantResults) {
                if (granResult != PERMISSION_GRANTED) {
                    DialogFactory.showMessage(this,
                            "Thông báo", "Vui lòng cấp quyền cho ứng dụng để sử dụng tính năng này!",
                            null);
                    return;
                }
            }
            mCameraView.open();
            takePhoto.setVisibility(View.VISIBLE);
        }
    }
}
