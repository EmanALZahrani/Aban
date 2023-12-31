package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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


        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@account, Levels::class.java)
            startActivity(intent)}

        // button to show result page
        val button = findViewById<Button>(R.id.result)
        button.setOnClickListener {
            val intent = Intent(this@account, ResultType::class.java)
            startActivity(intent)
        }

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
                        val password = document.getString("password") ?: "N/A"
                        val maskedPassword = maskPassword(password)
                        // Display data on TextViews
                        val nameTextView = findViewById<TextView>(R.id.nameresult)
                        val emailTextView = findViewById<TextView>(R.id.emailresult)
                        val phoneTextView = findViewById<TextView>(R.id.phoneresult)
                        val passwordTextView = findViewById<TextView>(R.id.passwordresult)
                        nameTextView.text = username
                        emailTextView.text = email
                        phoneTextView.text = phoneNumber
                        passwordTextView.text = maskedPassword

                        // Update data on button click
                        val updateButton = findViewById<ImageButton>(R.id.update)
                        updateButton.setOnClickListener {
                            // Show EditText fields, OK button, and Cancel button
                            findViewById<EditText>(R.id.nameEditText).visibility = View.VISIBLE
                            findViewById<EditText>(R.id.emailEditText).visibility = View.VISIBLE
                            findViewById<EditText>(R.id.phoneEditText).visibility = View.VISIBLE
                            findViewById<EditText>(R.id.passwordEditText).visibility = View.VISIBLE
                            findViewById<Button>(R.id.ok).visibility = View.VISIBLE
                            findViewById<Button>(R.id.cancelButton).visibility = View.VISIBLE

                            findViewById<TextView>(R.id.nameresult).visibility = View.GONE
                            findViewById<TextView>(R.id.emailresult).visibility = View.GONE
                            findViewById<TextView>(R.id.phoneresult).visibility = View.GONE
                            findViewById<TextView>(R.id.passwordresult).visibility = View.GONE
                            findViewById<Button>(R.id.result).visibility = View.GONE
                            updateButton.visibility = View.GONE
                            // Disable TextViews
                            nameTextView.isEnabled = false
                            emailTextView.isEnabled = false
                            phoneTextView.isEnabled = false
                            passwordTextView.isEnabled = false

                            // Populate EditText fields with existing data
                            findViewById<EditText>(R.id.nameEditText).setText(username)
                            findViewById<EditText>(R.id.emailEditText).setText(email)
                            findViewById<EditText>(R.id.phoneEditText).setText(phoneNumber)
                            findViewById<EditText>(R.id.passwordEditText).setText(password)
                        }

                        // OK button onClickListener
                        val okButton = findViewById<Button>(R.id.ok)
                        okButton.setOnClickListener {
                            // Get updated data from EditText fields
                            val updatedName = findViewById<EditText>(R.id.nameEditText).text.toString()
                            val updatedEmail = findViewById<EditText>(R.id.emailEditText).text.toString()
                            val updatedPhoneNumber = findViewById<EditText>(R.id.phoneEditText).text.toString()
                            val updatedPassword = findViewById<EditText>(R.id.passwordEditText).text.toString()

                            // Create a map to hold the updated fields
                            val updatedFields = mutableMapOf<String, Any>()
                            // Check if the fields are updated and add them to the map
                            if (updatedName != username) {
                                updatedFields["UserName"] = updatedName
                            }
                            if (updatedEmail != email) {
                                updatedFields["Email"] = updatedEmail
                            }
                            if (updatedPhoneNumber != phoneNumber) {
                                updatedFields["PhoneNum"] = updatedPhoneNumber
                            }
                            if (updatedPassword != password) {
                                updatedFields["password"] = updatedPassword
                            }

                            // Update the document in Firebase Firestore
                            userDocRef.update(updatedFields)
                                .addOnSuccessListener {
                                    // Update successful
                                    if (updatedFields.containsKey("UserName")) {
                                        nameTextView.text = updatedFields["UserName"].toString()
                                        Toast.makeText(this@account, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show()
                                    }
                                    if (updatedFields.containsKey("Email")) {
                                        emailTextView.text = updatedFields["Email"].toString()

                                    }
                                    if (updatedFields.containsKey("PhoneNum")) {
                                        phoneTextView.text = updatedFields["PhoneNum"].toString()
                                        Toast.makeText(this@account, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show()
                                    }
                                    if (updatedFields.containsKey("password")) {
                                        passwordTextView.text = maskPassword(updatedFields["password"].toString())
                                    }
                                    // Update user authentication credentials if the email or the password was changed
                                    if (updatedFields.containsKey("Email")) {
                                        val updatedEmail = updatedFields["Email"].toString()
                                        user.updatePassword(updatedEmail)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(this@account, "تم تحديث البريد الإلكتروني بنجاح", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // Password update failed
                                                    Toast.makeText(this@account, "فشل تحديث  البريد الإلكتروني", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    }
                                    if (updatedFields.containsKey("password")) {
                                        val updatedPassword = maskPassword(updatedFields["password"].toString())
                                        user.updatePassword(updatedPassword)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(this@account, "تم تحديث كلمة المرور بنجاح", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // Password update failed
                                                    Toast.makeText(this@account, "فشل تحديث كلمة المرور", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    }

                                }
                                .addOnFailureListener { e ->
                                    // Update failed
                                    Toast.makeText(this@account, "لم يتم التحديث", Toast.LENGTH_SHORT).show()
                                }

                            findViewById<TextView>(R.id.nameresult).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.emailresult).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.phoneresult).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.passwordresult).visibility = View.VISIBLE
                            findViewById<Button>(R.id.result).visibility = View.VISIBLE
                            // Hide EditText fields, OK button, and Cancel button
                            findViewById<EditText>(R.id.nameEditText).visibility = View.GONE
                            findViewById<EditText>(R.id.emailEditText).visibility = View.GONE
                            findViewById<EditText>(R.id.phoneEditText).visibility = View.GONE
                            findViewById<EditText>(R.id.passwordEditText).visibility = View.GONE
                            findViewById<Button>(R.id.ok).visibility = View.GONE
                            findViewById<Button>(R.id.cancelButton).visibility = View.GONE

                            // Show Update button again
                            updateButton.visibility = View.VISIBLE

                            // Show TextViews
                            nameTextView.isEnabled = true
                            emailTextView.isEnabled = true
                            phoneTextView.isEnabled = true
                            passwordTextView.isEnabled = true
                        }
                        // Cancel button onClickListener
                        val cancelButton = findViewById<Button>(R.id.cancelButton)
                        cancelButton.setOnClickListener {
                            // Show TextViews
                            findViewById<TextView>(R.id.nameresult).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.emailresult).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.phoneresult).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.passwordresult).visibility = View.VISIBLE
                            findViewById<Button>(R.id.result).visibility = View.VISIBLE


                            // Hide EditText fields, OK button, and Cancel button
                            findViewById<EditText>(R.id.nameEditText).visibility = View.GONE
                            findViewById<EditText>(R.id.emailEditText).visibility = View.GONE
                            findViewById<EditText>(R.id.phoneEditText).visibility = View.GONE
                            findViewById<EditText>(R.id.passwordEditText).visibility = View.GONE
                            findViewById<Button>(R.id.ok).visibility = View.GONE
                            findViewById<Button>(R.id.cancelButton).visibility = View.GONE

                            // Show Update button again
                            updateButton.visibility = View.VISIBLE

                            // Show TextViews
                            nameTextView.isEnabled = true
                            emailTextView.isEnabled = true
                            phoneTextView.isEnabled = true
                            passwordTextView.isEnabled = true

                        }
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

    fun maskPassword(password: String): String {
        return "*".repeat(password.length)
    }



}