package com.ghtk.internal.detection

import android.graphics.RectF

internal typealias DetectionLenient = org.tensorflow.lite.task.vision.detector.Detection

data class Detection(
    val boundingBox: RectF,
    val category: Category
) {

    companion object {
        val EMPTY = Detection(RectF(), Category())
    }
}

internal fun List<DetectionLenient>.toDetection(): Detection {
    if (isEmpty()) return Detection.EMPTY

    val meaningDetection = first()
    val rect = meaningDetection.boundingBox
    val meaningCategory = meaningDetection.categories.first()

    return Detection(
        rect,
        Category(
            meaningCategory.label,
            (meaningCategory.score * 100).toInt()
        )
    )
}
