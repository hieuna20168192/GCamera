package com.example.gcamera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.gcamera.base.BaseActivity
import com.example.gcamera.databinding.ActivityImageViewerBinding
import com.example.gcamera.extensions.FileUtils
import com.example.gcamera.extensions.saveImage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File

class ImageViewerActivity : BaseActivity<ActivityImageViewerBinding>() {

    override val layoutId: Int = R.layout.activity_image_viewer

    override fun initComponents() {
        initImage()
    }

    private fun initImage() {
        val filePath = intent.getStringExtra(MainActivity.EXTRAS_IMAGE)
        if (!filePath.isNullOrEmpty()) {
//            val resizedFile = FileUtils.resizeFile(File(filePath))
//            binding.imgFinder.setImageBitmap(BitmapFactory.decodeFile(resizedFile.path))
//            requestCroppingImage(filePath)

            val original = BitmapFactory.decodeFile(filePath)
            val heightUnit = (original.width / 7.2).toInt()

            val offsetY = (original.height - (heightUnit * 5.2).toInt() * 51 / 86) / 2
            val cropped = Bitmap.createBitmap(
                original,
                heightUnit,
                offsetY,
                (heightUnit * 5.2).toInt(),
                (heightUnit * 5.2).toInt() * 51 / 86
            )
            val file = saveImage(cropped)

            val resizeFile = FileUtils.resizeFile(file)

            val msg = getString(
                R.string.msg_capture_success,
                resizeFile.path
            )
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            Log.d("File.path", "${resizeFile.path}")
            Log.d("File.length", "${resizeFile.length() / 1024}")
            binding.imgFinder.setImageBitmap(cropped)
        }
    }

    private fun requestCroppingImage(filePath: String) {
        val imageUri = Uri.fromFile(File(filePath))
        CropImage.activity(imageUri).start(this)
    }

    override fun initListeners() {
        initNavUpListener()
    }

    private fun initNavUpListener() {
        binding.imgNavUp.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                val resizeFile = FileUtils.resizeFile(File(resultUri.path))
                binding.imgFinder.setImageBitmap(BitmapFactory.decodeFile(resizeFile.path))
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val err = result.error
                err.printStackTrace()
            }
        }
    }
}
