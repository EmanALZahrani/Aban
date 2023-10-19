package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class account : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)
        val button6 = findViewById<Button>(R.id.result)
        button6.setOnClickListener {
            val intent = Intent(this@account, DisplayAudioFilesActivity::class.java)
            startActivity(intent)}



        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            // Reference to user's document
            val userDocRef = db.collection("users").document(userId)

            // Fetch the document
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("UserName") ?: "N/A"
                        val email = document.getString("Email") ?: "N/A"
                        val phoneNumber = document.getString("PhoneNum") ?: "N/A"

                        // Display data on TextViews
                        findViewById<TextView>(R.id.nameresult).text = "$username"
                        findViewById<TextView>(R.id.emailresult).text = "$email"
                        findViewById<TextView>(R.id.phoneresult).text = "$phoneNumber"
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Failed with ", exception)
                }
        } else {
            // Handle the case when no user is signed in
            Log.d("FirebaseAuth", "No user is currently signed in.")
        }
    }




    // Retrieve current user's ID
   }