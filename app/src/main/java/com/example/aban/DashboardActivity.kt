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
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.example.aban.utils.PitchDetectionTarso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.Random

class DashboardActivity : AppCompatActivity() {
    private var storage: FirebaseStorage? = null
    private var firestore: FirebaseFirestore? = null
    private var currentAudioFileName: String? = null
    private var timeString: String? = null
    private var pitchValue: String? = null
    private var loudnessValue: String? = null
    private var lottieAnimationView: LottieAnimationView? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var startTime = 0L
    private val handler = Handler()
    private var tvDuration: TextView? = null
    private var seconds = 0
    private var timeInMilliseconds: Long = 0
    private var minutes = 0
    private val okHttpClient = OkHttpClient()

    private lateinit var btnRecord: Button


    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            seconds = (timeInMilliseconds / 1000).toInt()
            minutes = seconds / 60
            seconds = seconds % 60
            val tvDuration1: TextView = findViewById(R.id.tvDuration)
            tvDuration1.text = "Duration: " + minutes + ":" + String.format("%02d", seconds)
            handler.postDelayed(this, 1000)
        }
    }

    private fun accessFlaskServer() {
        val url = "https://aban-app-521459a5fe97.herokuapp.com/predict"

        // Create a request with the URL
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure (e.g., network error)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Check if the response was successful (HTTP status code 200)
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string()// Read the response body as a string
                        // Now you can use responseBody to update your TextView
                        val resultTextView = findViewById<AppCompatTextView>(R.id.resultidtxt)
                        resultTextView.text = responseBody
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle error while reading the response body
                    }
                } else {
                    // Handle non-successful response (e.g., HTTP status code is not 200)
                    // You can check response.code() for the HTTP status code
                }
            }
        })
    }


        // Declare a variable to store the temporary folder
    private lateinit var tempFolder: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record)
        btnRecord = findViewById<Button>(R.id.btnRecord)
        tvDuration = findViewById(R.id.tvDuration)
        lottieAnimationView = findViewById(R.id.lottie_animation_view)
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Create the temporary folder and store the reference
        tempFolder = createTempFolder()

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DashboardActivity, MainActivity::class.java)
            startActivity(intent)}
        val button7 = findViewById<ImageButton>(R.id.account)
        button7.setOnClickListener {
            val intent1 = Intent(this@DashboardActivity,page12 ::class.java)
            startActivity(intent1)}

        btnRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
                accessFlaskServer()
            } else {
                startRecording()
            }
        }

        val btnShowFiles = findViewById<AppCompatButton>(R.id.btnShowFiles)
        btnShowFiles.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@DashboardActivity,
                    DisplayAudioFilesActivity::class.java
                )
            )
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            btnRecord.text = "Recording Audio Stopped"
            btnRecord.background = getDrawable(R.drawable.button_bg_on)
            tvDuration?.text = "Duration : 00:00"
            lottieAnimationView?.visibility = View.GONE

            // Stop recording
            mediaRecorder?.stop()
            mediaRecorder?.release()
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
    private fun startRecording() {
    // Ensure that the MediaRecorder is not already recording
    if (isRecording) {
        return
    }
    btnRecord.text = "Recording Audio .."
    btnRecord.background = getDrawable(R.drawable.button_bg_off)

    // Lottie animation
    lottieAnimationView?.visibility = View.VISIBLE
    lottieAnimationView?.setAnimation(R.raw.animation_wave)
    lottieAnimationView?.speed = 1f
    lottieAnimationView?.loop(true)
    lottieAnimationView?.playAnimation()

    // Pitch detection on runtime
    val pitchDetector = PitchDetectionTarso() // Make sure to import this class
    Log.i("TAG", "startRecording: PitchInHertz : " + pitchDetector.lastResult.pitch)

    // Initialize MediaRecorder
    mediaRecorder = MediaRecorder()
    mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

    // Create a unique file name for each recording (e.g., timestamp)
    val audioFileName = "audio_" + System.currentTimeMillis() + ".wav"
    // Save the audio file name for later use
    currentAudioFileName = audioFileName
    mediaRecorder?.setOutputFile(getOutputFilePath(audioFileName))
    mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
    try {
        mediaRecorder?.prepare()
        mediaRecorder?.start()
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(updateTimerThread, 1000)
        isRecording = true
    } catch (e: Exception) {
        Log.d("TAG", "startRecording: " + e.localizedMessage)
    }
}

private fun getOutputFilePath(fileName: String?): String {
    return File(tempFolder, fileName).absolutePath
}

private fun saveAudioToFirebaseStorage() {
    val localFilePath = getOutputFilePath(currentAudioFileName)
    val audioRef = storage?.reference?.child("recordings")?.child(currentAudioFileName!!)
    val file = Uri.fromFile(File(localFilePath))
    val uploadTask = audioRef?.putFile(file)
    uploadTask?.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
        val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl.toString()
        saveRecordingDataToFirestore(downloadUrl)
    }?.addOnFailureListener { exception: Exception ->
        Log.d("TAG", "saveAudioToFirebaseStorage: onFailure: " + exception.localizedMessage)
    }
}

private fun saveRecordingDataToFirestore(audioUrl: String?) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        val userDocRef = firestore?.collection("users")?.document(userId)
        val userRecordingData = hashMapOf(
            "timestamp" to System.currentTimeMillis(),
            "audio_url" to audioUrl,
            // Add user information and check letter data here
        )
        userDocRef?.collection("recordings")?.add(userRecordingData)
            ?.addOnSuccessListener { documentReference ->
                Log.d("TAG", "saveRecordingDataToFirestore: DocumentSnapshot added with ID: ${documentReference.id}")
            }?.addOnFailureListener { e ->
                Log.d("TAG", "saveRecordingDataToFirestore: Error adding document: ${e.localizedMessage}")
            }
    }
}

private fun createTempFolder(): File {
    val folder = File(cacheDir, "audio_temp")
    if (!folder.exists()) {
        folder.mkdir()
    }
    return folder
}

companion object {
    fun randInt(min: Int, max: Int): Int {
        val rand = Random()
        return rand.nextInt(max - min + 1) + min
    }
}
}