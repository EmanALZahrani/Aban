package com.example.aban

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var username: EditText
    private lateinit var phone: EditText
    private lateinit var mail: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI components
        username = binding.username
        phone = binding.phone
        mail = binding.mail
        password = binding.password
        confirmPassword = binding.confirmPassword
        signupButton = binding.SignUpButton

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Set click listener for the sign-up button
        signupButton.setOnClickListener {
            val enteredUsername = username.text.toString()
            val enteredPassword = password.text.toString()
            val enteredPhoneNumber = phone.text.toString()
            val enteredEmail = mail.text.toString()

            if (isSignupValid(enteredUsername, enteredPassword, enteredPhoneNumber, enteredEmail)) {
                // Store the new user's credentials in SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("username", enteredUsername)
                    putString("password", enteredPassword)
                    apply()
                }

                // Display a success message
                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()

                // Navigate to the login page
                val intent = Intent(this,LogIn::class.java)
                startActivity(intent)
                finish() // Finish the sign-up activity to prevent going back to it from the login page
            } else {
                // Display an error message
                Toast.makeText(this, "Signup Failed! Please check your inputs.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isSignupValid(username: String, password: String, phoneNumber: String, email: String): Boolean {
        val isUsernameValid = username.isNotBlank() // Check if username is not empty
        val isPasswordValid = password.length >= 6 // Check if password is at least 6 characters long
        val isPhoneNumberValid = phoneNumber.matches(Regex("\\d+")) && phoneNumber.length == 10 // Check if the phone number contains 10 numeric digits
        val isEmailValid = isValidEmail(email) // Check if the email address has the correct format
        val doPasswordsMatch = password == confirmPassword.text.toString() // Check if the password and confirm password fields match

        return isUsernameValid && isPasswordValid && isPhoneNumberValid && isEmailValid && doPasswordsMatch
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        return emailRegex.matches(email)
    }
}
