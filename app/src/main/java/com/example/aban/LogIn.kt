package com.example.aban
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.LoginBinding


class LogIn : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI components
        username = binding.username
        password = binding.password
        loginButton = binding.loginButton

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            val enteredUsername = username.text.toString()
            val enteredPassword = password.text.toString()

            // Retrieve stored username and password from SharedPreferences
            val storedUsername = sharedPreferences.getString("username", "")
            val storedPassword = sharedPreferences.getString("password", "")

            if (enteredUsername == storedUsername && enteredPassword == storedPassword) {
                // If login is successful, navigate to the Check Letter activity
                val intent = Intent(this, Checkletter::class.java)
                startActivity(intent)
                finish() // Finish the login activity so you can't go back to it from the Check Letter activity
            } else {
                Toast.makeText(
                    this,
                    "Username or password is incorrect, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}