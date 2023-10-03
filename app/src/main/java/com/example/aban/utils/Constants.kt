package com.example.aban.utils

import android.os.Environment
import java.io.File


interface Constants {
    interface PATH {
        companion object {
            val BASE_PATH =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/recordings"
        }
    }

    companion object {
        fun createTempFolder(): String? {
            val dir = File(PATH.BASE_PATH)
            try {
                return if (!dir.exists()) {
                    dir.mkdirs()
                    println("Temp Directory created")
                    PATH.BASE_PATH
                } else if (dir.isDirectory) {
                    println("Temp Directory already existed !!")
                    PATH.BASE_PATH
                } else {
                    println("Temp Directory is not created")
                    ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}