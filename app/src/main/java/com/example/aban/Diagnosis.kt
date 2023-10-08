package com.example.aban

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.example.aban.utils.Constants
import com.example.aban.utils.PitchDetectionTarso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.Random







class Diagnosis : AppCompatActivity() {

    var storage: FirebaseStorage? = null
    var currentAudioFileName: String? = null
    var timeString: String? = null
    var type : String? = null
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
    private lateinit var resultTextView : TextView
    private val okHttpClient = OkHttpClient()


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
        setContentView(R.layout.diagnosis)
        btnRecord = findViewById<Button>(R.id.btnRecord)
        tvDuration = findViewById(R.id.tvDuration)
        firestore = FirebaseFirestore.getInstance()


        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@Diagnosis, LevelOne::class.java)
            startActivity(intent)}
        val button7 = findViewById<ImageButton>(R.id.account)
        button7.setOnClickListener {
            val intent1 = Intent(this@Diagnosis,account ::class.java)
            startActivity(intent1)}


        btnRecord.setOnClickListener {
            Constants.createTempFolder()
            if (isRecording) {
                accessFlaskServer()
                stopRecording()

            } else {
                startRecording()
            }
        }
        /* val btnShowFiles = findViewById<AppCompatButton>(R.id.btnShowFiles)
         btnShowFiles.setOnClickListener { view: View? ->
             startActivity(
                 Intent(
                     this@Cancellation,
                     DisplayAudioFilesActivity::class.java
                 )
             )
         }*/


    }




    private fun stopRecording() {
        if (isRecording) {
            btnRecord!!.text = "توقف التسجيل"
            btnRecord!!.background = getDrawable(R.drawable.button_bg_on)
            tvDuration!!.text = " المدة : ٠٠:٠٠"


            // Stop recording
            mediaRecorder!!.stop()
            mediaRecorder!!.release()
            mediaRecorder = null
            isRecording = false
            handler.removeCallbacks(updateTimerThread)
            timeString = minutes.toString() + ":" + String.format("%02d", seconds)
            saveAudioToFirebaseStorage()
            accessFlaskServer()

            // Starting Medium activity
            val intent = Intent(this, DiagnosisResult::class.java)
            intent.putExtra("durationIntent", timeString)
            intent.putExtra("typeIntent",type)//نضيف النتيجة حقت التشخيص هنا


        }

    }


    fun startRecording() {
        // Ensure that the MediaRecorder is not already recording
        if (isRecording) {
            return
        }
        btnRecord!!.text = " يتم التسجيل .."
        btnRecord!!.background = getDrawable(R.drawable.button_bg_off)


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
        data["Type"] = type
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
    private fun accessFlaskServer() {
        val audioFilePath = getOutputFilePath(currentAudioFileName)
        sendAudioToFlaskServer(audioFilePath)
    }

    private fun sendAudioToFlaskServer(audioFilePath: String) {
        val url = "https://aban-app-521459a5fe97.herokuapp.com/predict" // Replace with your Flask server URL

        val file = File(audioFilePath)
        val audioRequestBody = RequestBody.create("audio/*".toMediaTypeOrNull(), file.readBytes())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("audio", file.name, audioRequestBody)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure (e.g., network error)
                e.printStackTrace()
            }

            @SuppressLint("WrongViewCast")
            override fun onResponse(call: Call, response: Response) {
                // Check if the response was successful (HTTP status code 200)
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string() // Read the response body as a string

                        // Now you can use responseBody to update your UI with the result
                        runOnUiThread {
                            type = responseBody // Save the result in the 'type' variable as a String
                            resultTextView.text = type // Update the resultTextView with the result
                        }
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


    companion object {
        fun randInt(min: Int, max: Int): Int {
            val rand = Random()
            return rand.nextInt(max - min + 1) + min
        }
    }
}