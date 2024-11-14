package com.mogun.face_recognition.recognition

import android.graphics.PointF
import android.graphics.RectF
import android.util.SizeF

interface FaceAnalyzerListenr {
    fun detect()

    fun stopDetect()

    fun notDetect()

    fun detectProgress(progress: Float, message: String)

    fun faceSize(rectF: RectF, sizeF: SizeF, pointF: PointF)
}