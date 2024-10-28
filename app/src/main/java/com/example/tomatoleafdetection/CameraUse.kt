package com.example.tomatoleafdetection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tomatoleafdetection.databinding.CameraUseBinding
import com.example.tomatoleafdetection.viewmodel.CameraViewModel

class CameraUse : AppCompatActivity() {
    private lateinit var binding: CameraUseBinding
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = CameraUseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the CameraViewModel
        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)

        // Start the camera preview
        cameraViewModel.startCamera(this, binding.viewFinder)

        // Capture button click listener
        binding.btnCapture.setOnClickListener {
            cameraViewModel.capturePhoto(this) { savedUri ->
                displayCapturedImage(savedUri)
                sendImageUriBack(savedUri) // Send the URI back to the calling activity
            }
        }
    }

    // Function to display the captured image in ImageView
    private fun displayCapturedImage(imageUri: Uri) {
        binding.imageView.visibility = View.VISIBLE
        binding.imageView.setImageURI(imageUri) // Set the captured image in ImageView
    }

    // Function to send the captured image URI back to the previous activity
    private fun sendImageUriBack(imageUri: Uri) {
        val intent = Intent()
        intent.putExtra("imageUri", imageUri.toString())
        setResult(Activity.RESULT_OK, intent)
        finish() // Close this activity
    }
}
