package com.example.gcamera

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val data = arrayListOf<Int>()
        data.add(0x8A)
        data.add(0x01)
        data.add(0x01)
        data.add(0x9B)

        val str = "0x8A"
        val newByte = str.toByte()

        assertNotNull(newByte)
    }
}
