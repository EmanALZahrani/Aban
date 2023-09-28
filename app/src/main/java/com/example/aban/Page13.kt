package com.example.aban

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageButton

class page13 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page13)

        val imageButton = findViewById<ImageButton>(R.id.imageButton)

        imageButton.setOnClickListener {
            // Start the Page14 activity
            val intent = Intent(this, Page14::class.java)
            startActivity(intent)
        }
    }
}
