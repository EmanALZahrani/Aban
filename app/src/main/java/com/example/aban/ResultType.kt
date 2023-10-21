package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore

class ResultType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_type)

        val digbtn = findViewById<CardView>(R.id.cardView)
        digbtn.setOnClickListener {
            val intent1 = Intent(this@ResultType,DiagnosisDisplayAudioFilesActivity ::class.java)
            startActivity(intent1)}
        val canbtn = findViewById<CardView>(R.id.cardView2)
        canbtn.setOnClickListener {
            val intent2 = Intent(this@ResultType,DisplayAudioFilesActivity ::class.java)
            startActivity(intent2)}
    }
    private lateinit var firestore: FirebaseFirestore
    // Function to create a Firestore document for user tracking
    private fun createUserDocument(userId: String?) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "ResultType" to true,
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