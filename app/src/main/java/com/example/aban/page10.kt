package com.example.aban

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.aban.databinding.ActivityPage10Binding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

private lateinit var binding: ActivityPage10Binding
const val REQUEST_CODE = 200
class page10 : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var recorder: MediaRecorder
    private var dirPath=""
    private var filename=""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPage10Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page10)
        setContentView(binding.root)
        next()

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted)
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE)

         binding.apply {
             btnrecord.setOnClickListener{
             startRecording()
         }  }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE)
            permissionGranted=grantResults[0]== PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording(){
        if(!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE)
            return
        }

        //start record
        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"
        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD.hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.wav")

            try {
                prepare()
            }catch (e: IOException){}

            start()
        }
        binding.apply {
        }

    }


    private fun next(){
        binding.apply {
            btnnext.setOnClickListener(){
                startActivity(Intent(this@page10,page11::class.java))
            }
        }
    }
}