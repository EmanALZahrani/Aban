package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)
        val button6 = findViewById<Button>(R.id.result)
        button6.setOnClickListener {
            val intent = Intent(this@account, DisplayAudioFilesActivity::class.java)
            startActivity(intent)}
    }
}