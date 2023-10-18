package com.example.aban


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class WordFetcher {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun fetchRandomWordForCurrentUser(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser

        if (user == null) {
            onFailure(IllegalStateException("User is not logged in"))
            return
        }

        val userId = user.uid

        fetchUserChosenLetter(userId,
            onSuccess = { chosenLetter ->
                fetchRandomWord(chosenLetter, onSuccess, onFailure)
            },
            onFailure = onFailure
        )
    }

    private fun fetchUserChosenLetter(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                val letter = document.getString("selected_characters")
                if (letter != null) {
                    onSuccess(letter)
                } else {
                    onFailure(IllegalStateException("No letter chosen by the user"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun fetchRandomWord(chosenLetter: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("LetterAndWord").document("word").collection(chosenLetter).get()
            .addOnSuccessListener { querySnapshot ->
                val wordsArray = querySnapshot.documents.flatMap {
                    it.get("word") as? List<String> ?: emptyList()
                }
                if (wordsArray.isNotEmpty()) {
                    val randomWord = wordsArray[Random.nextInt(wordsArray.size)]
                    onSuccess(randomWord)
                } else {
                    onFailure(IllegalStateException("No words available for the chosen letter"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
