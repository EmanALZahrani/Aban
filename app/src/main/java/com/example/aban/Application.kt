package com.example.aban


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