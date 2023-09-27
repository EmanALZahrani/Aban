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
    private lateinit var username: EditText
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
