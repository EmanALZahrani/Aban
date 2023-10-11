package com.example.aban

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Levels : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.levels)
        fun hasCompletedActivity(userId: String): Boolean {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    val userDocRef = firestore.collection("recordingsData").document(userId)
                    val completionFlag = userDocRef.get().await().getBoolean("Levels_completed") ?: false
                    completionFlag
                }
            }

        }

        //Connect -> button with  previous page
        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@Levels, DiagnosisResult::class.java)
            startActivity(intent)
        }
        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@Levels, account::class.java)
            startActivity(intent)
        }

        //Connect level  button with  level one page
        val button2 = findViewById<Button>(R.id.button10)
        button2.setOnClickListener {
            val intent = Intent(this@Levels, LevelOne::class.java)
            startActivity(intent)
        }
        //click on an unavailable page
        val unavailablePageButton = findViewById<Button>(R.id.button11)
        unavailablePageButton.setOnClickListener {
            PopupUtils.PopupUtils.showUnavailablePageDialog(this)
        }

    }
    private fun hasCompletedActivity(userId: String): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val userDocRef = firestore.collection("users").document(userId)
                val completionFlag = userDocRef.get().await().getBoolean("Levels_completed") ?: false
                completionFlag
            }
        }
    }

    private suspend fun setActivityCompleted(userId: String) {
        withContext(Dispatchers.IO) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.update("Levels_completed", true).await()
        }
    }


}