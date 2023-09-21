package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityPage9Binding

class page9 : AppCompatActivity() {
    private lateinit var binding: ActivityPage9Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage9Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page9)
        setContentView(binding.root)
        next()
    }

    private fun next(){
        binding.apply {
            btnStart.setOnClickListener(){
                startActivity(Intent(this@page9,page10::class.java))
            }
        }
    }
}