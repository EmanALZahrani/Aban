package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ResultType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_type)
//

        val cardView = findViewById<ImageButton>(R.id.cardView)
        cardView.setOnClickListener {
            val intent1 = Intent(this@ResultType,ReViewDiagnosis ::class.java)
            startActivity(intent1)}

       val cardView2 = findViewById<ImageButton>(R.id.cardView2)
        cardView2.setOnClickListener {
        val intent = Intent(this@ResultType,LevelOneResult ::class.java)
        startActivity(intent)}
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