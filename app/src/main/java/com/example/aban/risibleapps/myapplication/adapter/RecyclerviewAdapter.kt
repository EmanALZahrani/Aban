package com.example.aban.risibleapps.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aban.R
import com.example.aban.risibleapps.myapplication.model.AudioModelClass


internal class RecyclerviewAdapter(var list: List<AudioModelClass>) : RecyclerView.Adapter<RecyclerviewAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_item, parent, false)
        return viewholder(view)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        // Bind data to the ViewHolder
        val audioModelClass = list[position]
        holder.audioTitleTextView.text = audioModelClass.title
        holder.pitchTitleTextView.text = audioModelClass.time + " Htz"
        holder.TimeTitleTextView.text = audioModelClass.pitch
        holder.LoudnessTitleTextView.text = audioModelClass.loudness + " %"

        // Handle the click event to play the audio
        holder.itemView.setOnClickListener { v: View? ->
            val downloadUrl = audioModelClass.downloadUrl
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
        var pitchTitleTextView: AppCompatTextView
        var TimeTitleTextView: AppCompatTextView
        var LoudnessTitleTextView: AppCompatTextView

        init {
            audioTitleTextView = itemView.findViewById(R.id.audioTitleTextView)
            pitchTitleTextView = itemView.findViewById(R.id.pitch)
            TimeTitleTextView = itemView.findViewById(R.id.Time)
            LoudnessTitleTextView = itemView.findViewById(R.id.loudness)
        }
    }
}