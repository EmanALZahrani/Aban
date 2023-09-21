package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Levels : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.levels)

        //Connect -> button with  previous page
        val button6 = findViewById<Button>(R.id.button8)
        button6.setOnClickListener {
            val intent = Intent(this@Levels, ResultActivity::class.java)
            startActivity(intent)
        }

        //Connect level on button with  level one page
        val button2 = findViewById<Button>(R.id.button10)
        button2.setOnClickListener {
            val intent = Intent(this@Levels, LevelOne::class.java)
            startActivity(intent)
        }
        //click on an unavailable page
        val unavailablePageButton = findViewById<Button>(R.id.button11)
        unavailablePageButton.setOnClickListener {
            PopupUtils.PopupUtils.showUnavailablePageDialog(this)
        }



    }
}