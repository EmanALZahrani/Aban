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

  /*  private fun fetchUserChosenLetter(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
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
    }*/

    private fun fetchUserChosenLetter(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Check if the document has the field "selected_characters"
                    if (documentSnapshot.contains("selected_characters")) {
                        val letters = documentSnapshot.data?.get("selected_characters")

                        if (letters is List<*> && letters.isNotEmpty()) {
                            // Filter non-null and non-empty strings, and randomize the selection
                            val validLetters = letters.filterIsInstance<String>().filter { it.isNotBlank() }
                            if (validLetters.isNotEmpty()) {
                                val randomLetter = validLetters[Random.nextInt(validLetters.size)]
                                onSuccess(randomLetter)
                            } else {
                                onFailure(IllegalStateException("List of letters is empty or data is invalid."))
                            }
                        } else {
                            onFailure(IllegalStateException("Selected characters are not in the correct format or the list is empty."))
                        }
                    } else {
                        onFailure(IllegalStateException("The user document doesn't contain 'selected_characters' field."))
                    }
                } else {
                    onFailure(IllegalStateException("No user document found."))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /*private fun fetchRandomWord(chosenLetter: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
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
    }*/

    private fun fetchRandomWord(chosenLetter: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("LetterAndWord")
            .document("word")  // Accessing the 'word' document
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document exists, now we retrieve the array
                    val wordsField = documentSnapshot.get(chosenLetter)

                    if (wordsField is List<*>) {
                        val words = wordsField.filterIsInstance<String>()  // Filtering out possible non-String values

                        if (words.isNotEmpty()) {
                            // Now, we have the 'words' array, pick a random word from it
                            val randomWord = words[Random.nextInt(words.size)]
                            onSuccess(randomWord)  // Use the random word as needed
                        } else {
                            onFailure(IllegalStateException("The array of words is empty."))
                        }
                    } else {
                        onFailure(IllegalStateException("Field not found or it's not an array."))
                    }
                } else {
                    onFailure(IllegalStateException("Document does not exist."))
                }
            }
            .addOnFailureListener { exception ->
                // Handle and pass on the error
                onFailure(exception)
            }
    }
}
