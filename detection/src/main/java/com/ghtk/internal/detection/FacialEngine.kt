package com.ghtk.internal.detection

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class FacialEngine(
    ctx: Context,
    metrics: Metrics = Metrics()
) : IFacialDetect {

    private val detector: ObjectDetector by lazy {
        ObjectDetector.createFromFileAndOptions(
            ctx,
            metrics.modelPath,
            metrics.toOptions()
        )
    }

    override fun detect(param: Bitmap): Detection {
        val tensorImage = TensorImage.fromBitmap(param)
        val result = detector.detect(tensorImage)
        return result.toDetection()
    }
}
