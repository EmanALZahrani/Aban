package com.example.aban

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class Diagnosis : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.dignosis)
        //Connect next button
    /*    val button = findViewById<Button>(R.id.button4)
        button.setOnClickListener {
            val intent = Intent(this@Diagnosis, ResultActivity::class.java)
            startActivity(intent)
        }
        //Connect previous page "Sing up"
        val previousButton = findViewById<ImageButton>(R.id.back)
        previousButton.setOnClickListener {
            val intent = Intent(this@Diagnosis, MainActivity::class.java)
            startActivity(intent)
        }

        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@Diagnosis, page12::class.java)
            startActivity(intent)
        }

        //////////////////////////////////////////
        val bt = findViewById<Button>(R.id.requred)
        bt.setOnClickListener {
            startRecording()
        */}


        /*
    private fun startRecording() {
        // Call your Python code here to start recording audio
        val process = Runtime.getRuntime().exec("python C:/Users/dell/audio.py")
        process.waitFor()

        val errorStream = process.errorStream
        val reader = BufferedReader(InputStreamReader(errorStream))

        var line = reader.readLine()
        while (line != null) {
            Log.e("Diagnosis", line)
            line = reader.readLine()
        }

        val exitCode = process.exitValue()
        if (exitCode != 0) {
            Log.e("Diagnosis", "Python script exited with code $exitCode")
        }

    }*/


}