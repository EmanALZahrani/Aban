package com.example.aban

import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.example.aban.utils.Constants
import com.example.aban.utils.PitchDetectionTarso
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.Random







class DashboardActivitycan : AppCompatActivity() {


    var storage: FirebaseStorage? = null
    var currentAudioFileName: String? = null
    var timeString: String? = null
    var pitchValue: String? = null
    var loudnessValue: String? = null
    var lottieAnimationView: LottieAnimationView? = null
    var firestore: FirebaseFirestore? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var startTime = 0L
    private val handler = Handler()
    private var tvDuration: TextView? = null
    private var seconds = 0
    private var timeInMilliseconds: Long = 0
    private var minutes = 0

    private lateinit var btnRecord: Button


    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            seconds = (timeInMilliseconds / 1000).toInt()
            minutes = seconds / 60
            seconds = seconds % 60
            var tvDuration1: TextView = findViewById(R.id.tvDuration)
            tvDuration1.setText("Duration: " + minutes + ":" + String.format("%02d", seconds))
            handler.postDelayed(this, 1000)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page10)
        btnRecord = findViewById<Button>(R.id.btnRecord)
        tvDuration = findViewById(R.id.tvDuration)
        lottieAnimationView = findViewById(R.id.lottie_animation_view)
        firestore = FirebaseFirestore.getInstance()



        btnRecord.setOnClickListener {
            Constants.createTempFolder()
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        val btnShowFiles = findViewById<AppCompatButton>(R.id.btnShowFiles)
        btnShowFiles.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@DashboardActivitycan,
                    DisplayAudioFilesActivity::class.java
                )
            )
        }


    }




    private fun stopRecording() {
        if (isRecording) {
            btnRecord!!.text = "Recording Audio Stopped"
            btnRecord!!.background = getDrawable(R.drawable.button_bg_on)
            tvDuration!!.text = "Duration : 00:00"
            lottieAnimationView!!.visibility = View.GONE

            // Stop recording
            mediaRecorder!!.stop()
            mediaRecorder!!.release()
            mediaRecorder = null
            isRecording = false
            handler.removeCallbacks(updateTimerThread)
            timeString = minutes.toString() + ":" + String.format("%02d", seconds)
            pitchValue = randInt(85, 300).toString()
            loudnessValue = randInt(10, 95).toString()
            saveAudioToFirebaseStorage()

            // Starting Medium activity
            val intent = Intent(this, SoundMediumActivity::class.java)
            intent.putExtra("pitchIntent", "$pitchValue Hrtz")
            intent.putExtra("durationIntent", timeString)
            intent.putExtra("loudnessIntent", "$loudnessValue %")

        }

        }


    fun startRecording() {
        // Ensure that the MediaRecorder is not already recording
        if (isRecording) {
            return
        }
        btnRecord!!.text = "Recording Audio .."
        btnRecord!!.background = getDrawable(R.drawable.button_bg_off)

//        Lottie animation ******************************************
        lottieAnimationView!!.visibility = View.VISIBLE
        // Set animation file (assuming it's in the res/raw directory)
        lottieAnimationView!!.setAnimation(R.raw.animation_wave)

        // Optional: Set animation speed
        lottieAnimationView!!.speed = 1f // Default speed is 1x

        // Optional: Set looping
        lottieAnimationView!!.loop(true) // Default is false

        // Start the animation
        lottieAnimationView!!.playAnimation()


        // ***********************************************************************

        // Pitch detection on runtime
        val pitchDetector = PitchDetectionTarso()
        Log.i("TAG", "startRecording: PitchInHertz : " + pitchDetector.lastResult.pitch)

        // Initialize MediaRecorder
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

        // Create a unique file name for each recording (e.g., timestamp)
        val audioFileName = "audio_" + System.currentTimeMillis() + ".wav"
        // Save the audio file name for later use
        currentAudioFileName = audioFileName // Declare this variable at the class level
        mediaRecorder!!.setOutputFile(getOutputFilePath(audioFileName)) // Use a local path for recording
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mediaRecorder!!.prepare()
            mediaRecorder!!.start() // Start recording
            startTime = SystemClock.uptimeMillis()
            handler.postDelayed(updateTimerThread, 1000)
            isRecording = true
        } catch (e: Exception) {
            Log.d("TAG", "startRecording: " + e.localizedMessage)
            // Handle the exception (e.g., display an error message to the user)
        }


    }

    private fun getOutputFilePath(fileName: String?): String {
        return Constants.createTempFolder() + "/" + fileName
    }

    // Later in your code, when you want to save the recorded audio to Firebase Storage:
    private fun saveAudioToFirebaseStorage() {
        // Upload the recorded audio to Firebase Storage
        val localFilePath = getOutputFilePath(currentAudioFileName)
        val audioRef = FirebaseStorage.getInstance().reference.child("recordings").child(
            currentAudioFileName!!
        )
        val file = Uri.fromFile(File(localFilePath))
        val uploadTask = audioRef.putFile(file)
        uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
            // Audio upload successful
            // You can store the Firebase Storage URL in Firestore or perform other actions
            val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
        }
            .addOnFailureListener { exception: Exception ->
                // Handle failed upload
                Log.d(
                    "TAG",
                    "saveAudioToFirebaseStorage: on Failure Called " + exception.localizedMessage
                )
            }
        val df = firestore!!.collection("recordingsData").document(
            currentAudioFileName!!
        )
        val data: MutableMap<String, Any?> = HashMap()
        data["time"] = timeString
        data["pitch"] = pitchValue
        data["Loudness"] = loudnessValue
        df.set(data).addOnSuccessListener { unused: Void? ->
            Log.d(
                "TAG",
                "saveAudioToFirebaseStorage: inner on Success"
            )
        }
            .addOnFailureListener { e: Exception ->
                Log.d(
                    "TAG",
                    "saveAudioToFirebaseStorage: EXCEp " + e.localizedMessage
                )
            }
    }

    companion object {
        fun randInt(min: Int, max: Int): Int {
            val rand = Random()
            return rand.nextInt(max - min + 1) + min
        }
    }
}