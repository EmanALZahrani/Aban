package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth


class LevelOne : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.levelone)
        fun hasCompletedActivity(userId: String): Boolean {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    val userDocRef = firestore.collection("recordingsData").document(userId)
                    val completionFlag = userDocRef.get().await().getBoolean("LevelOne_completed") ?: false
                    completionFlag
                }
            }
        }

        //Connect -> button with  previous page
        val button7 = findViewById<ImageButton>(R.id.back)
        button7.setOnClickListener {
            val intent = Intent(this@LevelOne, Levels::class.java)
            startActivity(intent)
        }
        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        // Log a user activity
        val activityData = hashMapOf(
            "timestamp" to FieldValue.serverTimestamp(),
            "activityType" to "button_click", // buttonClick activity
            "Email" to "enteredEmail"
        )

// Add a new document with an automatically generated ID to the "user_activities" collection
        db.collection("user_activities")
            .add(activityData)
            .addOnSuccessListener { documentReference ->
                // Activity data logged successfully
            }
            .addOnFailureListener { e ->
                // Handle the error
            }

        //click on "الالغاء" page
        val cancellationButton = findViewById<Button>(R.id.button12)
        cancellationButton.setOnClickListener {
            val intent = Intent(this@LevelOne, CancellationOverview::class.java)
            startActivity(intent)
        }
        //click on an unavailable page
        val unavailablePageButton = findViewById<Button>(R.id.button9)
        unavailablePageButton.setOnClickListener {
            PopupUtils.PopupUtils.showUnavailablePageDialog(this)
        }
        //click on an unavailable page
        val unavailablePageButton2 = findViewById<Button>(R.id.button15)
        unavailablePageButton2.setOnClickListener {
            PopupUtils.PopupUtils.showUnavailablePageDialog(this)
        }
        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@LevelOne, account::class.java)
            startActivity(intent)
        }
    }
    // Function to create a Firestore document for user tracking
    private fun createUserDocument(userId: String?) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "LevelOne" to true,
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