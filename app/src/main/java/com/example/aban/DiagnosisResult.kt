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
        type = findViewById(R.id.normal)
        next_bt = findViewById(R.id.next_bt)

        // next button listener
        next_bt.setOnClickListener {
            val intent = Intent(this, Diagnosis::class.java)
            startActivity(intent)
        }

//

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DiagnosisResult, Levels::class.java)
            startActivity(intent)
        }
        val button7 = findViewById<ImageButton>(R.id.account)
        button7.setOnClickListener {
            val intent1 = Intent(this@DiagnosisResult, account::class.java)
            startActivity(intent1)
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

    private fun hasCompletedActivity(userId: String, activity: String): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val userDocRef = firestore.collection("recordingsData").document(userId)
                val completionFlag =
                    userDocRef.get().await().getBoolean(activity + "_completed") ?: false
                completionFlag
            }
        }
    }

    private fun createUserDocument(userId: String?) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "DiagnosisResult" to true
            )

            // Specify the path for the user document
            val userDocumentRef = firestore.collection("recordingsData").document(userId)

            // Set the data in the Firestore document
            userDocumentRef.set(userData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "audio successfully stored!",
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