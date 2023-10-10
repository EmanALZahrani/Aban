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
       // typeIntent = intent.getStringExtra("typeIntent") ?: ""
        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DiagnosisResult, ResultType::class.java)
            startActivity(intent)
        }
        // create the get Intent object
        // create the get Intent object
        val intent = intent
        // receive the value by getStringExtra() method and
        // key must be same which is send by first activity
        // receive the value by getStringExtra() method and
        // key must be same which is send by first activity
        val str = intent.getStringExtra("typeIntent")
        // display the string into textView
        // display the string into textView
        val resultTextView = findViewById<TextView>(R.id.nameresult)
        resultTextView.text = str
       // gettingIntent()
    }

    private fun gettingIntent() {
        // Display the result in a TextView
        val resultTextView = findViewById<TextView>(R.id.nameresult)
        resultTextView.text = typeIntent
    }
}