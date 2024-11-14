package com.mogun.face_recognition

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executors

class Camera(private val context: Context): ActivityCompat.OnRequestPermissionsResultCallback {
    private val preview by lazy {
        Preview.Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }
    }

    private val cameraSelector by lazy {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView

    private var cameraExecutor = Executors.newSingleThreadExecutor()

    fun initCamera(layout: ViewGroup) {
        previewView = PreviewView(context)
        layout.addView(previewView)
        permissionCheck(context)
    }

    private fun permissionCheck(context: Context) {
        val permissionList = listOf(Manifest.permission.CAMERA)

        if(!PermissionUtil.checkPermission(context, permissionList)) {
            PermissionUtil.requestPermission(context as Activity, permissionList)
        } else {
            openPreView()
        }
    }

    private fun openPreView() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            .also { providerFuture ->
                providerFuture.addListener({

                }, ContextCompat.getMainExecutor(context))
            }
    }

    private fun startPreview(context: Context) {
        val cameraProvider = cameraProviderFuture.get()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        var flag = true
        if(grantResults.isNotEmpty()) {
            for((i, _) in permissions.withIndex()) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    flag = false
                }
                if(flag) {
                    openPreView()
                } else {
                    Toast.makeText(context, "카메라 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                    (context as Activity).finish()
                }
            }
        }
    }
}