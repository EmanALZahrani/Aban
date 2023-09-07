package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityPage11Binding

private lateinit var binding: ActivityPage11Binding
class page11 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage11Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page11)
        setContentView(binding.root)
        next()
    }
    private fun next(){
        binding.apply {
            next.setOnClickListener(){
                startActivity(Intent(this@page11,page12::class.java))
            }
        }
    }
}