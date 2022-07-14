package com.ghtk.internal.detection

import android.graphics.Bitmap

interface IDetect<P, R> {
    fun detect(param: P): R
}

interface IFacialDetect : IDetect<Bitmap, Detection>
