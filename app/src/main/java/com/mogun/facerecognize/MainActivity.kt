package com.mogun.facerecognize

import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.util.SizeF
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.mogun.face_recognition.Camera
import com.mogun.face_recognition.recognition.FaceAnalyzerListenr
import com.mogun.facerecognize.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FaceAnalyzerListenr {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val camera = Camera(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setProgressText("시작하기를 눌러주세요.")
        camera.initCamera(binding.cameraLayout, this)

        binding.startDetectButton.setOnClickListener {
            it.isVisible = false
            binding.faceOverlayView.reset()
            camera.startFaceDetect()
            setProgressText("얼굴을 보여주세요.")
        }
    }

    override fun detect() {
    }

    override fun stopDetect() {
        camera.stopFaceDetect()
        reset()
    }

    override fun notDetect() {
        binding.faceOverlayView.reset()
    }

    override fun detectProgress(progress: Float, message: String) {
        setProgressText(message)
    }

    override fun faceSize(
        rectF: RectF,
        sizeF: SizeF,
        pointF: PointF
    ) {
        binding.faceOverlayView.setSize(rectF, sizeF, pointF)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun reset() {
        binding.startDetectButton.isVisible = true
    }

    private fun setProgressText(text: String) {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.progressTextView.text = text
    }
}