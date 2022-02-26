package com.example.gcamera

import android.graphics.BitmapFactory
import com.example.gcamera.base.BaseActivity
import com.example.gcamera.databinding.ActivityImageViewerBinding

class ImageViewerActivity : BaseActivity<ActivityImageViewerBinding>() {

    override val layoutId: Int = R.layout.activity_image_viewer

    override fun initComponents() {
        initImage()
    }

    private fun initImage() {
        val filePath = intent.getStringExtra(MainActivity.EXTRAS_IMAGE)
        if (!filePath.isNullOrEmpty()) {
            binding.imgFinder.setImageBitmap(BitmapFactory.decodeFile(filePath))
        }
    }

    override fun initListeners() {
        initNavUpListener()
    }

    private fun initNavUpListener() {
        binding.imgNavUp.setOnClickListener { finish() }
    }
}
