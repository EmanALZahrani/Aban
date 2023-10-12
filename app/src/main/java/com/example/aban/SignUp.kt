package com.example.aban

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.SignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var binding: SignupBinding
    private lateinit var username: EditText
    private lateinit var phone: EditText
    private lateinit var mail: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var SignUpButton: Button
    private lateinit var signinButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI components
        username = binding.username
        phone = binding.phone
        mail = binding.mail
        password = binding.password
        confirmPassword = binding.confirmPassword
        signinButton = binding.SigninButton
        SignUpButton = binding.SignUpButton

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Set click listener for the "Sign up" button
        SignUpButton.setOnClickListener {
            val intent = Intent(this, Checkletter::class.java)
            startActivity(intent)
            // Handle user signup
            handleUserSignup()
        }

        // Set click listener for the "Sign In" button
        signinButton.setOnClickListener {
            // Navigate to the login page (replace LogIn::class.java with your actual login activity)
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
    }

    private fun handleUserSignup() {
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
                        // User account created successfully
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
                                    // You can proceed to the next step or show a success message
                                    // Save the user's email in SharedPreferences
                                    val editor = sharedPreferences.edit()
                                    editor.putString("email", enteredEmail)
                                    editor.apply()
                                    // Proceed to the next step or show a success message
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
            // Display an error message
            Toast.makeText(this, "Signup Failed! Please check your inputs.", Toast.LENGTH_SHORT).show()
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
        val isPasswordValid =
            password.length >= 6 // Check if password is at least 6 characters long
        val isPhoneNumberValid =
            phoneNumber.matches(Regex("\\d+")) && phoneNumber.length == 10 // Check if the phone number contains 10 numeric digits
        val isEmailValid = isValidEmail(email) // Check if the email address has the correct format
        val doPasswordsMatch =
            password == confirmPassword // Check if the password and confirm password fields match

        return isUsernameValid && isPasswordValid && isPhoneNumberValid && isEmailValid && doPasswordsMatch
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        return emailRegex.matches(email)
    }
}
