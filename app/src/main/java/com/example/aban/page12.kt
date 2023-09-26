package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityPage12Binding

private lateinit var binding: ActivityPage12Binding
class page12 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage12Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page12)
        setContentView(binding.root)
        menu()
        logout()
    }


    private fun menu(){
        binding.apply {
            menu.setOnClickListener(){
                startActivity(Intent(this@page12,Levels::class.java))
            }
        }
    }

    private fun logout(){
        binding.apply {
            LogOut.setOnClickListener(){
                startActivity(Intent(this@page12,LogIn::class.java))
            }
        }
    }

  /*  private fun result(){
        binding.apply {
            result.setOnClickListener(){
                startActivity(Intent(this@page12,Result::class.java))
            }
        }
    }*/
}