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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LogIn : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Check if the user is already authenticated
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already logged in, navigate to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the login activity
        }

        // Initialize UI components
        email = binding.email
        password = binding.password
        loginButton = binding.loginButton

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

                        // Navigate to the main activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Finish the login activity
                    } else {
                        // Login failed, display the error message
                        val errorMessage = task.exception?.message
                        Toast.makeText(
                            this,
                            "Login Failed: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
