package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView

class CancellationResult : AppCompatActivity() {
    private lateinit var nextButton: AppCompatButton
    private lateinit var accountButton: AppCompatButton
    var pitchTv: AppCompatTextView? = null
    var durationTv: AppCompatTextView? = null
    var loudnessTv: AppCompatTextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cancellation_result)
        loudnessTv = findViewById(R.id.nameresult2)
        durationTv = findViewById(R.id.nameresult3)
        pitchTv = findViewById(R.id.number)

        gettingIntent()


        // ***********************************************************************
        nextButton = findViewById(R.id.back)
        nextButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    Levels::class.java
                )
            )
        })

        accountButton = findViewById(R.id.account)
        nextButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    account::class.java
                )
            )
        })

    }

    private fun gettingIntent() {
        val recIntent = intent
        val pitchRec = recIntent.getStringExtra("pitchIntent")
        val durationRec = recIntent.getStringExtra("durationIntent")
        val loudnessRec = recIntent.getStringExtra("loudnessIntent")
        pitchTv!!.text = pitchRec
        durationTv!!.text = durationRec
        loudnessTv!!.text = loudnessRec
    }
}