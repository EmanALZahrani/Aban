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
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.example.aban.utils.Constants
import com.example.aban.utils.PitchDetectionTarso
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readByte
import com.google.common.io.ByteStreams.readBytes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Arrays
import java.util.Random
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class Diagnosis : AppCompatActivity() {
    private var storage: FirebaseStorage? = null
    private var firestore: FirebaseFirestore? = null
    private var currentAudioFileName: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var startTime = 0L
    private val handler = Handler()
    private val okHttpClient = OkHttpClient()

    private lateinit var btnRecord: Button
    private lateinit var resultTextView : TextView

    // Declare a variable to store the temporary folder
    private lateinit var tempFolder: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis)
        btnRecord = findViewById<Button>(R.id.btnRecord)
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Create the temporary folder and store the reference
        tempFolder = createTempFolder()

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@Diagnosis, SignUp::class.java)
            startActivity(intent)}
        val button7 = findViewById<ImageButton>(R.id.account)
        button7.setOnClickListener {
            val intent1 = Intent(this@Diagnosis,account ::class.java)
            startActivity(intent1)}

        btnRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
                accessFlaskServer()
            } else {
                startRecording()
            }
        }

//        val btnShowFiles = findViewById<AppCompatButton>(R.id.btnShowFiles) //هنا نربطه بالايدي حق الصفحة
        //       btnShowFiles.setOnClickListener { view: View? ->
        //           startActivity(
        //              Intent(
        //               this@DashboardActivity,
        //               DisplayAudioFilesActivity::class.java
        //            )
        //       )
        //    }
    }
    private fun startRecording() {
        // Ensure that the MediaRecorder is not already recording
        if (isRecording) {
            return
        }
        btnRecord.text = "يتم التسجيل"
        btnRecord.background = getDrawable(R.drawable.button_bg_off)//نغير الديزاين


        // Initialize MediaRecorder
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

        // Create a unique file name for each recording (e.g., timestamp)
        val audioFileName = "audio_" + System.currentTimeMillis() + ".wav" //نربط الاسم بالنتيجة والصفحة
        // Save the audio file name for later use
        currentAudioFileName = audioFileName
        mediaRecorder?.setOutputFile(getOutputFilePath(audioFileName))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startTime = SystemClock.uptimeMillis()
            isRecording = true
        } catch (e: Exception) {
            Log.d("TAG", "startRecording: " + e.localizedMessage)
        }
    }
    private fun stopRecording() {
        if (isRecording) {
            btnRecord.text = "توقف تسجيل الصوت"
            btnRecord.background = getDrawable(R.drawable.button_bg_on)

            // Stop recording
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            saveAudioToFirebaseStorage()
            accessFlaskServer()

            // Starting Medium activity
            val intent = Intent(this, DiagnosisResult::class.java) // مكان حفظ النتيجة
            intent.putExtra("typeIntent",resultTextView.text.toString())//نضيف النتيجة حقت التشخيص هنا
            startActivity(intent)
        }
    }


    private fun getOutputFilePath(fileName: String?): String {
        return Constants.createTempFolder() + "/" + fileName
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
                "timestamp" to System.currentTimeMillis(), // الوقت ماله داعي
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
                            val resultTextView = findViewById<AppCompatTextView>(R.id.nameresult) //نوصل بصفحة الريزلت
                            resultTextView.text = responseBody
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