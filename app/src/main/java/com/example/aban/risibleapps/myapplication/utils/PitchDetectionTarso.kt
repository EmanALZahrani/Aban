package com.example.aban.risibleapps.myapplication.utils

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.Yin


class PitchDetectionTarso : AudioProcessor {
    private val yin: Yin
    var lastResult: PitchDetectionResult
        private set

    init {
        // Configure the YIN algorithm (adjust parameters as needed)
        yin = Yin(44100f, 1024) // Sample rate and buffer size
        lastResult = PitchDetectionResult() // Initialize with default values
    }

    override fun process(audioEvent: AudioEvent): Boolean {
        val audioBuffer = audioEvent.floatBuffer

        // Calculate pitch using the YIN algorithm
//        float pitchInHz = yin.getPitch(audioBuffer);
        val pitchResult = yin.getPitch(audioBuffer)
        val pitchInHz = pitchResult.pitch

        // Handle the pitch value as needed
        if (pitchInHz > 0) {
            lastResult = PitchDetectionResult(pitchResult)
        }
        return true
    }

    override fun processingFinished() {
        // Handle any cleanup or finalization
    }
}