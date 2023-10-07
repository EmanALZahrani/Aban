package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView

class DiagnosisResult : AppCompatActivity() {

    private lateinit var nextButton: AppCompatButton
    private lateinit var typeIntent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis_result)
        typeIntent = intent.getStringExtra("typeIntent") ?: ""

        gettingIntent()

        nextButton = findViewById(R.id.back)
        nextButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    Levels::class.java
                )
            )
        })

    }
    private fun gettingIntent() {
        // Display the result in a TextView
        val resultTextView = findViewById<TextView>(R.id.nameresult)
        resultTextView.text = typeIntent

    }

}