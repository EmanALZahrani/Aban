package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.CancellationOverviewBinding
import com.google.firebase.firestore.FirebaseFirestore

class CancellationOverview : AppCompatActivity() {
    private lateinit var binding: CancellationOverviewBinding
    private lateinit var firestore: FirebaseFirestore



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
    // Function to create a Firestore document for user tracking
    private fun createUserDocument(userId: String?) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "CancellationOverview" to true,
            )

            // Specify the path for the user document
            val userDocumentRef = firestore!!.collection("recordingsData").document(userId)

            // Set the data in the Firestore document
            userDocumentRef.set(userData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        " ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "  ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


}