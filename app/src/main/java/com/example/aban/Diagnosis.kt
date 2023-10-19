package com.example.aban


import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.aban.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
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
    val okHttpClient = OkHttpClient()


    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            seconds = (timeInMilliseconds / 1000).toInt()
            minutes = seconds / 60
            seconds = seconds % 60
            var tvDuration1: TextView = findViewById(R.id.tvDuration)
            tvDuration1.setText("المدة: " + minutes + ":" + String.format("%02d", seconds))
            handler.postDelayed(this, 1000)
        }
    }

    private fun hasCompletedActivity(userId: String): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val userDocRef = firestore?.collection("users")?.document(userId)
                val completionFlag = userDocRef?.get()?.await()?.getBoolean("Diagnosis_completed")
                    ?: false
                completionFlag
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis)
        btnRecord = findViewById<ToggleButton>(R.id.btnRecord)
        tvDuration = findViewById(R.id.tvDuration)
        //firestore = FirebaseFirestore.getInstance()

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@Diagnosis, Levels::class.java)
            startActivity(intent)}
        val button7 = findViewById<ImageButton>(R.id.account)
        button7.setOnClickListener {
            val intent1 = Intent(this@Diagnosis,account ::class.java)
            startActivity(intent1)}


        btnRecord.setOnClickListener {
            Constants.createTempFolder()
            if (isRecording) {
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

            // Check if the audio file is not empty
            checkAudioFileNotEmpty()
        }
    }

    private fun checkAudioFileNotEmpty() {
        val filePath = getOutputFilePath(currentAudioFileName)
        val file = File(filePath)

        if (file.exists() && file.length() > 0) {
            // The file exists and is not empty, proceed with your logic (upload, etc.)
            //saveAudioToFirebaseStorage()
            accessFlaskServer()

            // Starting Medium activity
            val intent = Intent(this@Diagnosis, DiagnosisResult::class.java)
       //     intent.putExtra("durationIntent", timeString)
            intent.putExtra("typeIntent", type) // Add the result of the diagnosis here
            startActivity(intent)
        } else {
            // The file is empty or doesn't exist, handle this case (show a message, prompt for re-recording, etc.)
            runOnUiThread {
                Toast.makeText(this, "Recording failed or the audio is empty, please try again.", Toast.LENGTH_LONG).show()
            }
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
        val df = firestore!!.collection("users").document(currentAudioFileName!!)
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
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody)

                        if (jsonResponse.has("error")) {
                            val error = jsonResponse.getString("error")
                            runOnUiThread {
                                resultTextView.text = "Error: $error"
                            }
                        } else {
                            val normal = jsonResponse.getString("Normal")
                            val stutter = jsonResponse.getString("Stutter")
                            runOnUiThread {
                                resultTextView.text = "Normal: $normal\nStutter: $stutter"
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                            resultTextView.text = "Error reading response: ${e.message}"
                        }
                    }
                } else {
                    runOnUiThread {
                        resultTextView.text = "Server responded with status: ${response.code}"
                    }
                }
            }
        })
    }





}