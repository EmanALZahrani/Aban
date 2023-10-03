package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.databinding.ActivityPage10Binding


private lateinit var binding: ActivityPage10Binding

class page10 : AppCompatActivity() {
    private lateinit var userChar: String // الحرف المختار من قبل المستخدم
    private lateinit var textView: TextView // TextView لعرض الكلمة
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage10Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page10)
        setContentView(binding.root)

        next()
        back()
        acc()

       /* val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        // استعراض كلمات وحروف من Firebase Firestore
        val wordsCollection = db.collection("words")
        val currentUser = FirebaseAuth.getInstance().currentUser

// Check if a user is signed in
        if (currentUser != null) {
            // Assuming your user documents are stored in a "users" collection
            val db = FirebaseFirestore.getInstance()
            val usersCollection = db.collection("users")

            // Get the current user's document
            val userId = currentUser.uid

            usersCollection.document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve the character field from the user's document
                        val userChar = documentSnapshot.getString("selected_characters")

                        if (userChar != null) {
                            // Use the character value as needed
                            // Now you have the current user's character
                        } else {
                            // Handle the case where "character" field is null
                        }
                    } else {
                        // Handle the case where the user's document doesn't exist
                    }
                }
                .addOnFailureListener {
                    // Handle any errors that occur during the retrieval
                }
        } else {
            // User is not signed in, handle accordingly
        }



        // قم بالبحث عن الحرف المطابق في مجموعة الكلمات والحروف
        wordsCollection.document(userChar).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val wordsList = documentSnapshot.get("words") as List<String>
                    // عرض كلمة عشوائية في TextView
                    val random = Random()
                    val randomWord = wordsList[random.nextInt(wordsList.size)]
                    textView.text = randomWord
                } else {
                    textView.text = "لا توجد كلمات متاحة لهذا الحرف."
                }
            }
            .addOnFailureListener { exception ->
                textView.text = "حدث خطأ: ${exception.message}"
            }*/
    }





    private fun back() {
        binding.apply {
            back.setOnClickListener() {
                startActivity(Intent(this@page10, page11::class.java))
            }
        }
    }

    private fun next() {
        binding.apply {
           // btnNext.setOnClickListener() {
                startActivity(Intent(this@page10, LevelOne::class.java))
            }
        }
    }

    private fun acc() {
        binding.apply {
            account.setOnClickListener() {
               // startActivity(Intent(this@page10, page12::class.java))
            }
        }
    }

