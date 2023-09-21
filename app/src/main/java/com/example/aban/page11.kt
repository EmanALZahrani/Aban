package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityPage11Binding//مايحتاج تكتبونه بينضاف على طول

private lateinit var binding: ActivityPage11Binding//ActivityPage11Binding
class page11 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage11Binding.inflate(layoutInflater)//ActivityPage11Binding
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page11)
        setContentView(binding.root)
        next()
    }
    private fun next(){
        binding.apply {
            next.setOnClickListener(){//next
                startActivity(Intent(this@page11,page12::class.java))//@page11,page12
            }
        }
    }
}