package com.example.gcamera.actions

import org.junit.Assert.*
import org.junit.Test

class ShutterSpeedSlideActionTest {

    // range from 1/1 -> 1/2000
    private val shutterSpeedSlideAction = ShutterSpeedSlideAction(1.0f)

    // range from 100 -> 25600
    private val isoSpeedSlideAction = IsoSlideAction(25600f)

    // range from 1 -> 22
    private val apertureSlideAction = ApertureSlideAction(1f)

    private lateinit var actionManager: EVProducer

    @Test
    fun checkDefaultShutterSpeedValue() {
        assertEquals(1, shutterSpeedSlideAction.onSlide())
    }

    @Test
    fun checkDefaultISOValue() {
        assertEquals(6, isoSpeedSlideAction.onSlide())
    }

    @Test
    fun checkDefaultApertureValue() {
        assertEquals(-3, apertureSlideAction.onSlide())
    }

    @Test
    fun checkDuplicateDerivations() {
        assertEquals(false, IsoSlideAction::class == IsoSlideAction::class)
    }

    @Test
    fun checkEV() {
        assertEquals(
            -2,
            shutterSpeedSlideAction.onSlide() - isoSpeedSlideAction.onSlide() - apertureSlideAction.onSlide()
        )
    }

    @Test
    fun testFrontiers() {
        actionManager = EVProducer {

        }

        actionManager.invalidate(shutterSpeedSlideAction)
        actionManager.invalidate(isoSpeedSlideAction)
        actionManager.invalidate(isoSpeedSlideAction)
        assertEquals(listOf(1.0f, 25600f), actionManager.realValues)
    }
}
