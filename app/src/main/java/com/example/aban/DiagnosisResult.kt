package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DiagnosisResult : AppCompatActivity() {
    //private lateinit var nextButton: AppCompatButton
      private lateinit var typeIntent: String
    //private lateinit var type : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis_result)

        //typeIntent = intent.getStringExtra("durationIntent") ?:""
        typeIntent = intent.getStringExtra("typeIntent") ?: ""

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DiagnosisResult, Levels::class.java)
            startActivity(intent)
        }

        gettingIntent()
    }

    private fun gettingIntent() {
        // Display the result in a TextView
        val resultTextView = findViewById<TextView>(R.id.nameresult)
        resultTextView.text = typeIntent
    }
}