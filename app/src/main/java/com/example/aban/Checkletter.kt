package com.example.aban
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.CheckletterBinding
import android.widget.CheckBox
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Checkletter : AppCompatActivity() {
    private lateinit var binding: CheckletterBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CheckletterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences and Firebase Auth
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        // Continue with your next button logic
        next()
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

                        // Store selected characters in Firebase Realtime Database
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val userCharactersRef =
                                FirebaseDatabase.getInstance().reference.child("users").child(userId)
                                    .child("selected_characters")
                            userCharactersRef.setValue(selectedCharacters)
                        }

                        // Start the Diagnosis activity
                        val intent = Intent(this@Checkletter, Diagnosis::class.java)
                        startActivity(intent)
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
            R.id.checkBox1,
            R.id.checkBox2,
            R.id.checkBox3,
            R.id.checkBox4,
            R.id.checkBox5,
            R.id.checkBox6,
            R.id.checkBox7,
            R.id.checkBox8,
            R.id.checkBox9,
            R.id.checkBox10,
            R.id.checkBox11,
            R.id.checkBox12,
            R.id.checkBox13,
            R.id.checkBox14,
            R.id.checkBox15,
            R.id.checkBox16,
            R.id.checkBox17,
            R.id.checkBox18,
            R.id.checkBox19,
            R.id.checkBox20,
            R.id.checkBox21,
            R.id.checkBox22,
            R.id.checkBox23,
            R.id.checkBox24,
            R.id.checkBox25,
            R.id.checkBox26,
            R.id.checkBox144,
            R.id.checkBox200,

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
            R.id.checkBox1,
            R.id.checkBox2,
            R.id.checkBox3,
            R.id.checkBox4,
            R.id.checkBox5,
            R.id.checkBox6,
            R.id.checkBox7,
            R.id.checkBox8,
            R.id.checkBox9,
            R.id.checkBox10,
            R.id.checkBox11,
            R.id.checkBox12,
            R.id.checkBox13,
            R.id.checkBox14,
            R.id.checkBox15,
            R.id.checkBox16,
            R.id.checkBox17,
            R.id.checkBox18,
            R.id.checkBox19,
            R.id.checkBox20,
            R.id.checkBox21,
            R.id.checkBox22,
            R.id.checkBox23,
            R.id.checkBox24,
            R.id.checkBox25,
            R.id.checkBox26,
            R.id.checkBox144,
            R.id.checkBox200,

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
