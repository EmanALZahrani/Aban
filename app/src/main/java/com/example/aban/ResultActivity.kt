package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result)
        //Connect the -> with the تشخيص page
        val button2 = findViewById<ImageButton>(R.id.back);
        button2.setOnClickListener {
            val intent = Intent(this@ResultActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        //Connect next button with levels page
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            val intent = Intent(this@ResultActivity, Levels::class.java)
            startActivity(intent)
        }
        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@ResultActivity, page12::class.java)
            startActivity(intent)
        }

    }
}