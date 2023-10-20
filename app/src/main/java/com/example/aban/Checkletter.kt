// Checkletter.kt
package com.example.aban


import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.CheckletterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class Checkletter : AppCompatActivity() {
    private lateinit var binding: CheckletterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CheckletterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if the user has already selected a letter
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.getBoolean("letter_selected") == true) {
                        // Start the Diagnosis activity directly
                        val intent = Intent(this@Checkletter, Diagnosis::class.java)
                        startActivity(intent)
                        finish() // Finish the Checkletter activity to prevent going back
                    } else {
                        next()
                    }
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
    }

    private fun next() {
        binding.apply {
            next.setOnClickListener {
                // Check if at least one character checkbox is checked
                if (atLeastOneCharacterSelected()) {
                    // Retrieve the selected characters
                    val selectedCharacters = getSelectedCharacters()

                    // Store selected characters in Firestore
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userDocRef = firestore.collection("users").document(userId)

                        val userSelections = hashMapOf(
                            "selected_characters" to selectedCharacters,
                            "letter_selected" to true
                        )

                        userDocRef.set(userSelections, SetOptions.merge())
                            .addOnSuccessListener {
                                // User selections stored successfully in Firestore
                                // Proceed to the next step or show a success message
                                Toast.makeText(
                                    this@Checkletter,
                                    "Selections stored successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Start the Diagnosis activity
                                val intent = Intent(this@Checkletter, Diagnosis::class.java)
                                startActivity(intent)
                                finish() // Finish the Checkletter activity to prevent going back
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
            }
        }

        return selectedCharacters
    }
}


