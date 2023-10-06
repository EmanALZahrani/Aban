package com.example.aban

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.CancellationOverviewBinding

class CancellationOverview : AppCompatActivity() {
    private lateinit var binding: CancellationOverviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = CancellationOverviewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cancellation_overview)
        setContentView(binding.root)
        next()
        back()
        acc()
    }

    private fun next(){
        binding.apply {
            btnStart.setOnClickListener(){
                startActivity(Intent(this@CancellationOverview,Cancellation::class.java))
            }
        }
    }


    private fun back(){
        binding.apply {
            back.setOnClickListener(){
                startActivity(Intent(this@CancellationOverview,LevelOne::class.java))
            }
        }
    }

    private fun acc(){
        binding.apply {
            account.setOnClickListener(){
                startActivity(Intent(this@CancellationOverview,account::class.java))
            }
        }
    }

}