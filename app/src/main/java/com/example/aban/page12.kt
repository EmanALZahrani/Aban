package com.example.aban
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class page12 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page12)

        val resultButton = findViewById<Button>(R.id.result)

        resultButton.setOnClickListener {
            // Navigate to page13 when the "Result" button is clicked
            val intent = Intent(this, page13::class.java)
            startActivity(intent)
        }
    }
}
