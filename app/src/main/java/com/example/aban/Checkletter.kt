// Checkletter.kt
package com.example.aban

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.aban.databinding.CheckletterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Checkletter : AppCompatActivity() {
    private lateinit var binding: CheckletterBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nextButton: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences, Firebase Auth, and FirebaseFirestore
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        binding = CheckletterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //for show the page only ones
        val userId = auth.currentUser?.uid
        val checkletterCompleted = sharedPreferences.getBoolean("checkletter_completed", false)
        if (userId != null && !checkletterCompleted) {
            next()
        } else {
            startDiagnosisActivity()
        }


        nextButton = findViewById(R.id.back)
        nextButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    Levels::class.java
                )
            )
        })
    }

    private fun hasCompletedActivity(userId: String): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val userDocRef = firestore.collection("users").document(userId)
                val completionFlag = userDocRef.get().await().getBoolean("checkletter_completed") ?: false
                completionFlag
            }
        }
    }

    private suspend fun setActivityCompleted(userId: String) {
        withContext(Dispatchers.IO) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.update("checkletter_completed", true).await()

            // Update the flag in SharedPreferencess
            val editor = sharedPreferences.edit()
            editor.putBoolean("checkletter_completed", true)
            editor.apply()
        }
    }
    private fun next() {

        binding.apply {
            next.setOnClickListener {

                // Check if at least one character checkbox is checked
                if (atLeastOneCharacterSelected()) {
                    // Retrieve the user's email from SharedPreferences
                    val userEmail = sharedPreferences.getString("email", "")

                    if (!userEmail.isNullOrEmpty()) {
                        // Retrieve the selected characters
                        val selectedCharacters = getSelectedCharacters()
                        // Store selected characters in Firestore
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val userDocRef = firestore.collection("users").document(userId)

                            val userSelections = hashMapOf(
                                "selected_characters" to selectedCharacters

                            )


                            userDocRef.set(userSelections, SetOptions.merge())
                                .addOnSuccessListener {
                                    // User selections stored successfully in Firestore

                                    Toast.makeText(
                                        this@Checkletter,
                                        " ",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Mark the activity as completed
                                    lifecycleScope.launch {
                                        setActivityCompleted(userId)
                                    }
                                    startDiagnosisActivity()
                                    // Start the Diagnosis activity
                                    //val intent = Intent(this@Checkletter, Diagnosis::class.java)
                                    //startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // Handle the error
                                    Toast.makeText(
                                        this@Checkletter,
                                        "Firestore Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        // Handle the case where the user's email is not available
                        Toast.makeText(
                            this@Checkletter,
                            "User email not found. Please login again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Display a message indicating that the user must select at least one character
                    Toast.makeText(
                        this@Checkletter,
                        "Please select at least one character.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun atLeastOneCharacterSelected(): Boolean {
        // Define your character CheckBox IDs as you did before
        val characterCheckBoxIds = arrayOf(
            R.id.checkBox6,
            R.id.checkBox7,
            R.id.checkBox20
        )

        // Loop through the CheckBox IDs and check if at least one is checked
        for (checkBoxId in characterCheckBoxIds) {
            val checkBox = findViewById<CheckBox>(checkBoxId)
            if (checkBox.isChecked) {
                return true

            }
        }

        // None of the character CheckBoxes are checked
        return false
    }

    private fun getSelectedCharacters(): List<String> {
        val selectedCharacters = mutableListOf<String>()

        // Define your character CheckBox IDs as you did before
        val characterCheckBoxIds = arrayOf(
            R.id.checkBox6,
            R.id.checkBox7,
            R.id.checkBox20
        )

        // Loop through the CheckBox IDs and add the selected characters to the list
        for (checkBoxId in characterCheckBoxIds) {
            val checkBox = findViewById<CheckBox>(checkBoxId)
            if (checkBox.isChecked) {
                selectedCharacters.add(checkBox.text.toString())
                val intent = Intent(this, Cancellation::class.java)
                intent.putExtra("selectedCharacters", "selectedCharacters")
            }
        }


        return selectedCharacters
    }
    private fun startDiagnosisActivity() {
        // Start the Diagnosis activity
        val intent = Intent(this@Checkletter, Diagnosis::class.java)
        startActivity(intent)
    }
}
