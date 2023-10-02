package com.example.aban.risibleapps.myapplication.model

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioClassificationModel(private val context: Context, private val modelPath: String) {

    private lateinit var interpreter: Interpreter
    private val labels = arrayOf("noise", "stutter", "normal")

    fun loadModel() {
        val modelFile = loadModelFile()
        val options = Interpreter.Options()
        interpreter = Interpreter(modelFile, options)
    }
    private fun loadModelFile(): ByteBuffer {
        val inputStream = context.assets.open(modelPath)
        val modelBuffer = ByteArray(inputStream.available())
        inputStream.read(modelBuffer)
        inputStream.close()

        return ByteBuffer.wrap(modelBuffer)
    }

    fun classifyAudio(audioData: FloatArray): String {
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()
        val inputBuffer = inputBuffer(inputShape)
        val outputBuffer = outputBuffer(outputShape)

        val floatBuffer = inputBuffer.asFloatBuffer() // Convert ByteBuffer to FloatBuffer
        floatBuffer.put(audioData)

        interpreter.run(inputBuffer, outputBuffer)


        val result = outputBuffer.array()
        val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1

        return labels[maxIndex]
    }

    private fun inputBuffer(shape: IntArray): ByteBuffer {
        val inputSize = shape.reduce { acc, i -> acc * i }
        return ByteBuffer.allocateDirect(inputSize * 4).apply {
            order(ByteOrder.nativeOrder())
        }
    }

    private fun outputBuffer(shape: IntArray): ByteBuffer {
        val outputSize = shape.reduce { acc, i -> acc * i }
        return ByteBuffer.allocateDirect(outputSize * 4).apply {
            order(ByteOrder.nativeOrder())
        }
    }
}
