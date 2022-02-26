package com.example.gcamera.actions

import androidx.annotation.VisibleForTesting
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.math.sqrt

abstract class SlideAction(var realValue: Float) {
    abstract fun onSlide(): Int
}

@VisibleForTesting
class ApertureSlideAction(realValue: Float) : SlideAction(realValue) {
    override fun onSlide(): Int {
        val apertureStops = (-1) * ln(realValue) / ln(sqrt(2.0))
        return apertureStops.roundToInt()
    }
}

@VisibleForTesting
class IsoSlideAction(realValue: Float) : SlideAction(realValue) {
    override fun onSlide(): Int {
        val isoStops = ln(realValue / 100) / ln(2.0)
        return isoStops.roundToInt()
    }
}

@VisibleForTesting
class ShutterSpeedSlideAction(realValue: Float) : SlideAction(realValue) {
    override fun onSlide(): Int {
        val shutterSpeedStops = log2(1 / (realValue))
        return shutterSpeedStops.roundToInt()
    }
}

@VisibleForTesting
class EVProducer(
    private val ev: (Int) -> Unit
) {
    private val actions = mutableListOf<SlideAction>()

    val realValues: List<Float>
        get() = actions.map { it.realValue }

    fun invalidate(action: SlideAction) {
        if (hasExisted(action)) {
            replaceAction(action)
        } else {
            actions.add(action)
        }
        updateExposureValue()
    }

    private fun replaceAction(action: SlideAction) {
        actions.forEach {
            if (it::class == action::class) {
                it.realValue = action.realValue
            }
        }
    }

    private fun hasExisted(action: SlideAction) = actions.any { action::class == it::class }

    private fun updateExposureValue() {
        var stopsCount = 0
        for (i in actions.indices) {
            val action = actions[i]
            val stopCount =
                if (action is ShutterSpeedSlideAction) action.onSlide() else action.onSlide() * (-1)
            stopsCount += stopCount
        }
        ev(stopsCount)
    }
}
