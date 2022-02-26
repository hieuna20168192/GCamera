package com.example.gcamera

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gcamera.actions.ApertureSlideAction
import com.example.gcamera.actions.IsoSlideAction
import com.example.gcamera.actions.ShutterSpeedSlideAction
import com.example.gcamera.actions.EVProducer
import com.example.gcamera.base.BaseActivity
import com.example.gcamera.databinding.ActivityMainBinding
import java.lang.Exception
import kotlin.math.roundToInt

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private val actionManager: EVProducer = EVProducer { ev ->
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
                actionManager.invalidate(ApertureSlideAction(value))
            }
            setLabelFormatter { value ->
                "f/${(value.roundToInt())}"
            }
        }

        binding.includeController.sliderISO.apply {
            addOnChangeListener { _, value, _ ->
                actionManager.invalidate(IsoSlideAction(value))
            }
        }

        binding.includeController.sliderShutterSpeed.apply {
            addOnChangeListener { _, value, _ ->
                val realValue = 1 / value
                actionManager.invalidate(ShutterSpeedSlideAction(realValue))
            }
            setLabelFormatter { value ->
                "1/${(value.roundToInt())}"
            }
        }
    }

    private fun initTakePhotoListener() {
        binding.includeController.buttonTakePhoto.setOnClickListener {

        }
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
