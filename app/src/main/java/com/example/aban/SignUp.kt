package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var phone: EditText
    private lateinit var mail: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signUpButton: Button
    private lateinit var signinButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI components
        username = findViewById(R.id.username)
        phone = findViewById(R.id.phone)
        mail = findViewById(R.id.mail)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        signUpButton = findViewById(R.id.SignUpButton)
        signinButton = findViewById(R.id.SigninButton)

        // Set click listener for the "Sign In" button
        signinButton.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        // Set click listener for the "Sign Up" button
        signUpButton.setOnClickListener {
            val enteredUsername = username.text.toString()
            val enteredPassword = password.text.toString()
            val enteredPhoneNumber = phone.text.toString()
            val enteredEmail = mail.text.toString()

            if (isSignupValid(
                    enteredUsername,
                    enteredPassword,
                    enteredPhoneNumber,
                    enteredEmail,
                    confirmPassword.text.toString()
                )
            ) {
                // Create a new user with email and password using Firebase Authentication
                auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Save user data to Firestore
                            val user = auth.currentUser
                            user?.let {
                                val userId = it.uid
                                val userDocRef = firestore.collection("users").document(userId)

                                val userData = hashMapOf(
                                    "Email" to enteredEmail,
                                    "UserName" to enteredUsername,
                                    "PhoneNum" to enteredPhoneNumber,
                                    "password" to enteredPassword
                                )

                                userDocRef.set(userData)
                                    .addOnSuccessListener {
                                        // User data stored successfully
                                        // Navigate to the Checkletter activity
                                        val intent = Intent(this@SignUp, Checkletter::class.java)
                                        startActivity(intent)

                                        // Show a success message
                                        Toast.makeText(
                                            this@SignUp,
                                            "Signup Successful!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle the error
                                        Toast.makeText(
                                            this@SignUp,
                                            "Firestore Error: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            // Display an error message
                            Toast.makeText(this, "Signup Failed! Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Display an error message for invalid input
                Toast.makeText(this, "Signup Failed! Please check your inputs.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isSignupValid(
        username: String,
        password: String,
        phoneNumber: String,
        email: String,
        confirmPassword: String
    ): Boolean {
        val isUsernameValid = username.isNotBlank() // Check if username is not empty
        val isPasswordValid = password.length >= 6 // Check if the password is at least 6 characters long
        val isPhoneNumberValid =
            phoneNumber.matches(Regex("\\d+")) && phoneNumber.length == 10 // Check if the phone number contains 10 numeric digits
        val isEmailValid = isValidEmail(email) // Check if the email address has the correct format
        val doPasswordsMatch = password == confirmPassword // Check if the password and confirm password fields match

        return isUsernameValid && isPasswordValid && isPhoneNumberValid && isEmailValid && doPasswordsMatch
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        return emailRegex.matches(email)
    }
}
