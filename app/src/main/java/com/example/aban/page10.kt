package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityPage10Binding

private lateinit var binding: ActivityPage10Binding
class page10 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage10Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page10)
        setContentView(binding.root)
        next()
    }

    private fun next(){
        binding.apply {
            btnnext.setOnClickListener(){
                startActivity(Intent(this@page10,page11::class.java))
            }
        }
    }
}