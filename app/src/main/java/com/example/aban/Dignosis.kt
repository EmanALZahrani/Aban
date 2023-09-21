package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Dignosis : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.dignosis)
        //Connect التالي button
        val button = findViewById<Button>(R.id.button4)
        button.setOnClickListener {
            val intent = Intent(this@Dignosis, ResultActivity::class.java)
            startActivity(intent)
        }


    }
}