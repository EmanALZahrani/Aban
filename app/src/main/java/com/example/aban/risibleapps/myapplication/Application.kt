package com.example.aban.risibleapps.myapplication

// Created by Abdullah_Shah
// on 11,September,2023
// Either write something worth reading or do something worth writing ;)
class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: Application? = null
            private set
    }
}