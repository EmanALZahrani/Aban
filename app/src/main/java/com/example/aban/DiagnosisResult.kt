package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class DiagnosisResult : AppCompatActivity() {
    //private lateinit var nextButton: AppCompatButton
      private lateinit var typeIntent: String

    private lateinit var firestore: FirebaseFirestore

    private fun hasCompletedActivity(userId: String): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val userDocRef = firestore.collection("users").document(userId)
                val completionFlag = userDocRef.get().await().getBoolean("DiagnosisResult_completed") ?: false
                completionFlag
            }
        }
    }
    //private lateinit var type : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis_result)

        //typeIntent = intent.getStringExtra("durationIntent") ?:""
        typeIntent = intent.getStringExtra("typeIntent") ?: ""

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DiagnosisResult, Levels::class.java)
            startActivity(intent)
        }

        gettingIntent()
    }

    private fun gettingIntent() {
        // Display the result in a TextView
        val resultTextView = findViewById<TextView>(R.id.nameresult)
        resultTextView.text = typeIntent
    }
    // Function to create a Firestore document for user tracking
    private fun createUserDocument(userId: String?) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "DiagnosisResult" to true,
            )

            // Specify the path for the user document
            val userDocumentRef = firestore!!.collection("users").document(userId)

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