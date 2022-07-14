package com.ghtk.internal.detection

import org.tensorflow.lite.task.vision.detector.ObjectDetector

data class Metrics(
    val modelPath: String = "",
    val numOfObjects: Int = 1,
    val scoreThreshold: Float = 0.0f
)

internal fun Metrics.toOptions() =
    ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(numOfObjects)
        .setScoreThreshold(scoreThreshold)
        .build()
