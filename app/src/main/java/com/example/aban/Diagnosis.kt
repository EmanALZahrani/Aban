package com.example.aban


import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.net.ConnectivityManager
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
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Random


class Diagnosis : AppCompatActivity() {

    var storage: FirebaseStorage? = null
    var currentAudioFileName: String? = null
    var timeString: String? = null
    var type: String? = null
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
    private lateinit var resultTextView: TextView
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
        firestore = FirebaseFirestore.getInstance()


        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@Diagnosis, Levels::class.java)
            startActivity(intent)
        }
        val button7 = findViewById<ImageButton>(R.id.account)
        button7.setOnClickListener {
            val intent1 = Intent(this@Diagnosis, account::class.java)
            startActivity(intent1)
        }


        btnRecord.setOnClickListener {
            Constants.createTempFolder()
            if (isRecording) {
                stopRecording()

            } else {
                startRecording()
            }

        }
        initializeAppCheck()
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
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        // Create a unique file name for each recording (e.g., timestamp)
        val audioFileName = "audio_" + System.currentTimeMillis() + ".m4a"
        // Save the audio file name for later use
        currentAudioFileName = audioFileName // Declare this variable at the class level
        mediaRecorder!!.setOutputFile(getOutputFilePath(audioFileName)) // Use a local path for recording
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
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
            btnRecord?.text = "توقف التسجيل"
            btnRecord?.background = getDrawable(R.drawable.button_bg_on)
            tvDuration?.text = " المدة : ٠٠:٠٠"


            // Stop recording
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            handler.removeCallbacks(updateTimerThread)
            timeString = minutes.toString() + ":" + String.format("%02d", seconds)
            saveAudioToFirebaseStorage()
            if (isNetworkAvailable(this)) {
                // Proceed to send the audio file
                accessFlaskServer()
            } else {
                Toast.makeText(
                    this,
                    "No internet connection. Please check your network settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }


            // Starting Medium activity
            val intent = Intent(this@Diagnosis, DiagnosisResult::class.java)
            //intent.putExtra("durationIntent", timeString)
            intent.putExtra("typeIntent", type)//نضيف النتيجة حقت التشخيص هنا
            startActivity(intent)

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
        val df = firestore!!.collection("recordingsData").document(currentAudioFileName!!)
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
        val url =
            "https://aban-app-521459a5fe97.herokuapp.com/predict" // Replace with your Flask server URL

        val file = File(audioFilePath)
        val audioRequestBody =
            RequestBody.create("audio/x-m4a".toMediaTypeOrNull(), file.readBytes())

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
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (!response.isSuccessful) {
                        // Handle server error response here
                        val errorMessage = "استجاب الخادم بالحالة: ${response.code}"
                        val intent = Intent(this@Diagnosis, DiagnosisResult::class.java).apply {
                            putExtra(
                                "error",
                                errorMessage
                            ) // Pass the error message to the DiagnosisResult activity
                        }
                        startActivity(intent)
                        return@runOnUiThread
                    }

                    try {
                        val responseBody = response.body?.string()
                            ?: throw IOException("Unable to read response body.")
                        val jsonResponse = JSONObject(responseBody)

                        if (jsonResponse.has("error")) {
                            val error = jsonResponse.getString("error")

                            // Pass the error message to the DiagnosisResult activity
                            val intent = Intent(this@Diagnosis, DiagnosisResult::class.java).apply {
                                putExtra(
                                    "error",
                                    error
                                ) // Use the key "error" to pass the error message
                            }
                            startActivity(intent)
                        } else {
                            val stutter = jsonResponse.getString("Stutter")
                            val normal = jsonResponse.getString("Normal")

                            // Pass the results to the DiagnosisResult activity.
                            val intent = Intent(this@Diagnosis, DiagnosisResult::class.java).apply {
                                putExtra("typeIntent", "طبيعي: $stutter\nتأتأة: $normal")
                            }
                            startActivity(intent)
                        }
                    } catch (e: IOException) {
                        // This catch block can handle generic I/O errors.
                        Toast.makeText(
                            this@Diagnosis,
                            "Error processing the response: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: JSONException) {
                        // This catch block is to handle issues with JSON parsing.
                        Toast.makeText(
                            this@Diagnosis,
                            "Error parsing the response: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
    }


    private fun initializeAppCheck() {
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        val appCheckProviderFactory = SafetyNetAppCheckProviderFactory.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(appCheckProviderFactory)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}



