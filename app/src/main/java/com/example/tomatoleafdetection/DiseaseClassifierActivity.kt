package com.example.tomatoleafdetection

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.tomatoleafdetection.databinding.ActivityDiseaseClassifierBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class DiseaseClassifierActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiseaseClassifierBinding
    private lateinit var uploadLauncher: ActivityResultLauncher<Intent>
    private lateinit var interpreter: Interpreter
    private lateinit var drawerLayout: DrawerLayout
    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_IMAGE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DiseaseClassifierActivity", "onCreate called")
        binding = ActivityDiseaseClassifierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        try {
            interpreter = Interpreter(loadModelFile())
            Log.d("DiseaseClassifierActivity", "Model loaded successfully")
        } catch (e: Exception) {
            Log.e("DiseaseClassifierActivity", "Error loading model", e)
        }

        uploadLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImage: Uri? = result.data?.data
                Glide.with(this)
                    .load(selectedImage)
                    .into(binding.imageView)
                imageUri = selectedImage
            }
        }

        binding.buttonUpload.setOnClickListener {
            val uploadIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            uploadLauncher.launch(uploadIntent)
        }

        binding.buttonPredict.setOnClickListener {
            if (imageUri != null) {
                showLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    val result = classifyImage()
                    binding.predictionResult.text = result
                    hideLoading()
                }, 3000) // Delay for demo purposes
            } else {
                binding.predictionResult.text = "Please select an image first."
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_contact_us -> {
                    val intent = Intent(this, ContactUsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_about_us -> {
                    val intent = Intent(this, AboutUsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        binding.buttonCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
            } else {
                openCamera()
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Image")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        binding.imageView.setImageURI(uri)
                        imageUri = uri
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    binding.imageView.setImageURI(imageUri)
                }
            }
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = assets.openFd("tomato_disease_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun classifyImage(): String {
        val bitmap = imageUri?.let { uriToBitmap(it) }
        if (bitmap != null) {
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val inputBuffer = convertBitmapToByteBuffer(scaledBitmap)
            val output = Array(1) { FloatArray(10) }
            interpreter.run(inputBuffer, output)

            val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            val diseases = listOf(
                "Tomato mosaic virus", "Target Spot", "Bacterial spot",
                "Tomato Yellow Leaf Curl Virus", "Late blight", "Leaf Mold",
                "Early blight", "Spider mites Two spotted spider mite",
                "Tomato healthy", "Septoria leaf spot"
            )
            val confidence = (output[0][predictedIndex] * 100).toInt()
            return "${diseases[predictedIndex]} - $confidence% match"
        } else {
            Log.e("DiseaseClassifierActivity", "Bitmap conversion failed")
            return "Image processing error."
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            Log.e("DiseaseClassifierActivity", "Failed to convert URI to Bitmap", e)
            null
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        inputBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixelValue in intValues) {
            inputBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255f)
            inputBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255f)
            inputBuffer.putFloat((pixelValue and 0xFF) / 255f)
        }
        return inputBuffer
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.imageView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.imageView.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close()
    }
}
