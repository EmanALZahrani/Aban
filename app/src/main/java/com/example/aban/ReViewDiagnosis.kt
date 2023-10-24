package com.example.aban

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aban.model.DiagnosisModelClass
import com.google.firebase.firestore.FirebaseFirestore


internal class ReViewDiagnosis(var list: List<DiagnosisModelClass>) : RecyclerView.Adapter<ReViewDiagnosis.viewholder>() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_item_diagnosis, parent, false)
        return viewholder(view)
    }
//
    override fun onBindViewHolder(holder: viewholder, position: Int) {
        // Bind data to the ViewHolder
        val DiagnosisModelClass = list[position]
        holder.audioTitleTextView.text = DiagnosisModelClass.title
        holder.typeTitleTextView.text = DiagnosisModelClass.time + " Htz"
        holder.TimeTitleTextView.text = DiagnosisModelClass.type

        // Handle the click event to play the audio
        holder.itemView.setOnClickListener { v: View? ->
            val downloadUrl = DiagnosisModelClass.downloadUrl
            // Start playing the audio using a media player library or Android's MediaPlayer
            Toast.makeText(
                holder.itemView.context,
                "Play Audio or what else you want to do !!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        Log.d("TAG", "getItemCount:list " + list.size)
        return list.size
    }

    internal inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var audioTitleTextView: AppCompatTextView
        var typeTitleTextView: AppCompatTextView
        var TimeTitleTextView: AppCompatTextView

        init {
            audioTitleTextView = itemView.findViewById(R.id.audioTitleTextView)
            typeTitleTextView = itemView.findViewById(R.id.type)
            TimeTitleTextView = itemView.findViewById(R.id.Time)
        }
    }

    // Function to create a Firestore document for user tracking
    private fun createUserDocument(userId: String?, context: Context) {
        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            // Define the data
            val userData = hashMapOf(
                "RecyclerviewAdapter" to true,
            )

            // Specify the path for the user document
            val userDocumentRef = firestore.collection("recordingsData").document(userId)

            // Set the data in the Firestore document
            userDocumentRef.set(userData)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        " ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        " ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

}