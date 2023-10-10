package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView

class CancellationResult : AppCompatActivity() {
    private lateinit var nextButton: AppCompatButton
    private lateinit var playButton: AppCompatImageView
    var pitchTv: AppCompatTextView? = null
    var durationTv: AppCompatTextView? = null
    var loudnessTv: AppCompatTextView? = null
    var playClicked = true
    private lateinit var lottieAnimationView: LottieAnimationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cancellation_result)
        loudnessTv = findViewById(R.id.loudnessCard_Id)
        durationTv = findViewById(R.id.durationCard_Id)
        pitchTv = findViewById(R.id.pitchCard_Id)

        gettingIntent()



        //back button
        val button = findViewById<ImageButton>(R.id.back)
        button.setOnClickListener {
            val intent = Intent(this@CancellationResult, ResultType::class.java)
            startActivity(intent)
        }
        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@CancellationResult, account::class.java)
            startActivity(intent)
        }

        //        Lottie animation ******************************************
        //lottieAnimationView = findViewById(R.id.lottie_animation_view_medium)
        // Set animation file (assuming it's in the res/raw directory)
       // lottieAnimationView.setAnimation(R.raw.animation_wave)

        // Optional: Set animation speed
       // lottieAnimationView.setSpeed(1f) // Default speed is 1x

        // Optional: Set looping
       // lottieAnimationView.loop(true) // Default is false


        // ***********************************************************************
        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    Levels::class.java
                )
            )
        })
        playButton = findViewById(R.id.drawable)
        playButton.setOnClickListener(View.OnClickListener { v: View? ->
            if (playClicked) {
                playClicked = false
                lottieAnimationView.setVisibility(View.VISIBLE)
                findViewById<View>(R.id.audioTitleTextView).visibility = View.GONE
                // Start the animation
                lottieAnimationView.playAnimation()
            } else {
                playClicked = true
                lottieAnimationView.setVisibility(View.GONE)
                findViewById<View>(R.id.audioTitleTextView).visibility = View.VISIBLE
            }
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