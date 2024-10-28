package com.example.tomatoleafdetection

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var topImageView: ImageView
    private lateinit var registerTextView: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        topImageView = findViewById(R.id.topImageView)
        registerTextView = findViewById(R.id.registerTextView)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Set a professional image at the top
        topImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.top_image)) // Replace with your drawable resource

        // Enable/disable login button based on input validation
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate user credentials with DatabaseHelper
            if (databaseHelper.validateUser(email, password)) {
                // Successful login, navigate to DiseaseClassifierActivity
                val intent = Intent(this, DiseaseClassifierActivity::class.java)
                startActivity(intent)
                finish() // Close the login activity
            } else {
                // Show error if login fails
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle register link click
        registerTextView.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    // TextWatcher to monitor input fields and enable login button when both fields are non-empty
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}
    }
}






//package com.example.tomatoleafdetection
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var emailEditText: EditText
//    private lateinit var passwordEditText: EditText
//    private lateinit var loginButton: Button
//    private lateinit var topImageView: ImageView
//    private lateinit var registerTextView: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        // Initialize views
//        emailEditText = findViewById(R.id.emailEditText)
//        passwordEditText = findViewById(R.id.passwordEditText)
//        loginButton = findViewById(R.id.loginButton)
//        topImageView = findViewById(R.id.topImageView)
//        registerTextView = findViewById(R.id.registerTextView)
//
//        // Set top image
//        topImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.top_image))
//
//        // Enable/disable login button based on input
//        emailEditText.addTextChangedListener(textWatcher)
//        passwordEditText.addTextChangedListener(textWatcher)
//
//        // Login button click
//        loginButton.setOnClickListener {
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//            if (validateLogin(email, password)) {
//                val intent = Intent(this, DiseaseClassifierActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Register link click
//        registerTextView.setOnClickListener {
//            val intent = Intent(this, SignupActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//    private val textWatcher = object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//            loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
//        }
//        override fun afterTextChanged(s: Editable?) {}
//    }
//
//    private fun validateLogin(email: String, password: String): Boolean {
//        return email.contains("@") && password.length >= 6
//    }
//}
