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
        //click on "الالغاء" page
        val cancellationButton = findViewById<Button>(R.id.button12)
        cancellationButton.setOnClickListener {
            val intent = Intent(this@LevelOne, page9::class.java)
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
        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@LevelOne, page12::class.java)
            startActivity(intent)
        }
    }
}