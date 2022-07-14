package com.example.gcamera

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gcamera.actions.ApertureSlideAction
import com.example.gcamera.actions.IsoSlideAction
import com.example.gcamera.actions.ShutterSpeedSlideAction
import com.example.gcamera.actions.EVProducer
import com.example.gcamera.base.BaseActivity
import com.example.gcamera.databinding.ActivityMainBinding
import com.example.gcamera.extensions.createInternalDirectory
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import java.io.File
import java.lang.Exception
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("VisibleForTests")
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    private val evProducer: EVProducer = EVProducer { ev ->
        camera?.cameraControl?.setExposureCompensationIndex(ev)
        Log.d("EVProducer", "$ev")
    }

    override fun initComponents() {
        downloadModel("face_mask_model")
        applyCameraPlugin()
//        binding.viewCrop.post {
//            Log.d("viewCrop.x", "${binding.viewCrop.x}")
//            Log.d("viewCrop.y", "${binding.viewCrop.y}")
//            Log.d("viewCrop.w", "${binding.viewCrop.width}")
//            Log.d("viewCrop.h", "${binding.viewCrop.height}")
//        }
    }

    private fun downloadModel(modelName: String) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(modelName, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                initDetectProcessor(modelFile)
            }
    }

    private fun initDetectProcessor(modelFile: File?) {
        if (preCondition(modelFile)) {

        }
    }

    private fun preCondition(modelFile: File?): Boolean {
        if (modelFile == null) {
            showToast("Failed to initialize model file: $modelFile")
        }
        return modelFile != null
    }

    private fun showToast(text: String) {
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun applyCameraPlugin() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRE_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val metrics = windowManager.currentWindowMetrics.bounds
            val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val viewPort = ViewPort.Builder(Rational(51, 86), Surface.ROTATION_0).build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                        detectImage(imageProxy)
                    }
                }

            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture!!)
                .addUseCase(imageAnalyzer)
                .setViewPort(viewPort)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    useCaseGroup
                )

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, getString(R.string.msg_err_bind_use_cases), e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectImage(imageProxy: ImageProxy) {
        val bitmap = toBitmap(imageProxy)!!

//        val result = facialEngine.detect(bitmap)
//        Log.d("detectImage", result.toString())
        imageProxy.close()
    }

    private fun allPermissionsGranted() = REQUIRE_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun initListeners() {
        initTakePhotoListener()
        initCompensationListener()
    }

    private fun initCompensationListener() {
        binding.includeController.sliderAperture.apply {
            addOnChangeListener { _, value, _ ->
                evProducer.invalidate(ApertureSlideAction(value))
            }
            setLabelFormatter { value ->
                "f/${(value.roundToInt())}"
            }
        }

        binding.includeController.sliderISO.apply {
            addOnChangeListener { _, value, _ ->
                evProducer.invalidate(IsoSlideAction(value))
            }
        }

        binding.includeController.sliderShutterSpeed.apply {
            addOnChangeListener { _, value, _ ->
                val realValue = 1 / value
                evProducer.invalidate(ShutterSpeedSlideAction(realValue))
            }
            setLabelFormatter { value ->
                "1/${(value.roundToInt())}"
            }
        }
    }

    private fun initTakePhotoListener() {
        binding.includeController.buttonTakePhoto.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = applicationContext.createInternalDirectory()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = getString(
                        R.string.msg_capture_success,
                        outputFileResults.savedUri
                    )
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    requestViewPhoto(outputFileResults.savedUri?.path)
                    Log.d(TAG, msg)
                }

                override fun onError(exception: ImageCaptureException) {
                    val msg = getString(R.string.msg_capture_failed, exception.message)
                    Toast.makeText(
                        baseContext,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun requestViewPhoto(path: String?) {
        if (path == null) return
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra(EXTRAS_IMAGE, path)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.msg_err_no_camera_permissions),
                    Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            }
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun toBitmap(imageProxy: ImageProxy): Bitmap? {
        val yuvToRgbConverter = YuvToRgbConverter(this)
        var bitmapBuffer: Bitmap
        var rotationMatrix: Matrix

        val image = imageProxy.image ?: return null

        // Initialise Buffer
        // The image rotation and RGB image buffer are initialized only once
        Log.d(TAG, "Initalise toBitmap()")
        rotationMatrix = Matrix()
        rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
        )

        // Pass image to an image analyser
        yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            rotationMatrix,
            false
        )
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    companion object {
        private const val TAG = "GCamera"
        const val EXTRAS_IMAGE = "EXTRAS_IMAGE"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRE_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}

data class DetectionResult(val boundingBox: RectF, val text: String)
