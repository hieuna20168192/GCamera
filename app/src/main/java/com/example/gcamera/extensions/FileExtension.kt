package com.example.gcamera.extensions

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

fun Context.saveImageToInternalStorage(bitmap: Bitmap) {
    val file = createInternalDirectory()
    try {
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun Context.createInternalDirectory(): File {
    val contextWrapper = ContextWrapper(this)
    val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)
    val fileName = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    return File(directory, fileName)
}
