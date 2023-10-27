package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView

class CancellationResult : AppCompatActivity() {
    private lateinit var nextButton: AppCompatButton
    private lateinit var accountButton: AppCompatButton
    lateinit var show_button: AppCompatButton
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
        val back_button = findViewById<ImageButton>(R.id.back)
        back_button.setOnClickListener {
            val intent = Intent(this@CancellationResult, Levels::class.java)
            startActivity(intent)}
        val account_button = findViewById<ImageButton>(R.id.account)
        account_button.setOnClickListener {
            val intent1 = Intent(this@CancellationResult,account ::class.java)
            startActivity(intent1)}
        val show_button = findViewById<Button>(R.id.CanNextbtn)
        show_button.setOnClickListener {
            val intent2 = Intent(this@CancellationResult, account::class.java)
            startActivity(intent2)}

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