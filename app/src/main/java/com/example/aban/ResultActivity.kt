package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result)
        //Connect the -> with the تشخيص page
        val button2 = findViewById<Button>(R.id.button2);
        button2.setOnClickListener {
            val intent = Intent(this@ResultActivity, Dignosis::class.java)
            startActivity(intent)
        }
        //Connect التالي button with levels page
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            val intent = Intent(this@ResultActivity, Levels::class.java)
            startActivity(intent)
        }

    }
}