package com.example.tomatoleafdetection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tomatoleafdetection.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set welcome message
        val welcomeMessage = findViewById<TextView>(R.id.welcomeMessage)
        welcomeMessage.text = "Welcome to Tomify!"

        // Continue button to go to LoginActivity
        val continueButton = findViewById<Button>(R.id.continueButton) // ID should match XML
        continueButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

