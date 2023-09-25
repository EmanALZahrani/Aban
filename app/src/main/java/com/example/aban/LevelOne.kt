package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LevelOne : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.levelone)

        //Connect -> button with  previous page
        val button7 = findViewById<ImageButton>(R.id.back)
        button7.setOnClickListener {
            val intent = Intent(this@LevelOne, Levels::class.java)
            startActivity(intent)
        }
        //click on an unavailable page
        val unavailablePageButton = findViewById<Button>(R.id.button9)
        unavailablePageButton.setOnClickListener {
            PopupUtils.PopupUtils.showUnavailablePageDialog(this)
        }
        //click on an unavailable page
        val unavailablePageButton2 = findViewById<Button>(R.id.button15)
        unavailablePageButton2.setOnClickListener {
            PopupUtils.PopupUtils.showUnavailablePageDialog(this)
        }
    }
}