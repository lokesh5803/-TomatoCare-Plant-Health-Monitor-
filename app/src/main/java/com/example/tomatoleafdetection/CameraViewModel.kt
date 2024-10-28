package com.example.tomatoleafdetection.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraViewModel : ViewModel() {
    var imageCapture: ImageCapture? = null

    fun startCamera(context: Context, viewFinder: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll() // Unbind use cases before rebinding
            cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto(context: Context, onImageCaptured: (Uri) -> Unit) {
        val photoFile = createFile(context)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(Uri.fromFile(photoFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle the error
                }
            }
        )
    }

    private fun createFile(context: Context): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(System.currentTimeMillis())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "IMG_$timestamp.jpg")
    }
}
