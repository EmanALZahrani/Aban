package com.example.aban

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LogIn : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var loginButton2: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        loginButton2 = findViewById(R.id.loginButton2)

        // Set click listener for the "loginButton2" button
        loginButton2.setOnClickListener {
            // Navigate to the signup page (replace SignUp::class.java with your actual signup activity)
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            val enteredUsername = email.text.toString()
            val enteredPassword = password.text.toString()

            // Authenticate the user with Firebase
            auth.signInWithEmailAndPassword(enteredUsername, enteredPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success
                        val user: FirebaseUser? = auth.currentUser
                        Toast.makeText(
                            this,
                            "Login Successful! Welcome, ${user?.email}",
                            Toast.LENGTH_SHORT
                        ).show()



                        // Navigate to the Check Letter activity
                        val intent = Intent(this, Checkletter::class.java)
                        startActivity(intent)
                        finish() // Finish the login activity
                    } else {
                        // Login failed
                        Toast.makeText(
                            this,
                            "Login Failed. Please check your credentials.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }



}
