package com.example.gcamera

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import java.lang.Exception
import kotlin.math.roundToInt

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private val evProducer: EVProducer = EVProducer { ev ->
        camera?.cameraControl?.setExposureCompensationIndex(ev)
    }

    override fun initComponents() {
        applyCameraPlugin()
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

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

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
    }
}
