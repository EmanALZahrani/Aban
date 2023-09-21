package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.CheckletterBinding

class Checkletter : AppCompatActivity() {
    private lateinit var binding: CheckletterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CheckletterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        next()
    }

    private fun next() {
        binding.apply {
            next.setOnClickListener {

                val intent = Intent(this@Checkletter, Checkletter::class.java)
                startActivity(intent)
            }
        }
    }
}