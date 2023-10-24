package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DiagnosisResult : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var type: AppCompatTextView
    private lateinit var next_bt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis_result)
        type = findViewById(R.id.nameresult)
        next_bt = findViewById(R.id.next_bt)

        // next button listener
        next_bt.setOnClickListener {
            val intent = Intent(this, Levels::class.java)
            startActivity(intent)
        }

//

        // Back button listener
        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DiagnosisResult, ResultType::class.java)
            startActivity(intent)
        }

        val error = intent.getStringExtra("error")
        val typeIntent = intent.getStringExtra("typeIntent")

        if (error != null) {

            type.text = error // for the error message
        } else if (typeIntent != null) {

            type.text = typeIntent // for the result
        } else {

        }
    }

    private fun hasCompletedActivity(userId: String): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val userDocRef = firestore.collection("users").document(userId)
                val completionFlag =
                    userDocRef.get().await().getBoolean("DiagnosisResult_completed") ?: false
                completionFlag
            }
        }
    }

    // Create a Firestore document for user tracking
    private fun createUserDocument(userId: String?) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "DiagnosisResult" to true
            )

            // Specify the path for the user document
            val userDocumentRef = firestore.collection("users").document(userId)

            // Set the data in the Firestore document
            userDocumentRef.set(userData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Document successfully written!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error writing document: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}