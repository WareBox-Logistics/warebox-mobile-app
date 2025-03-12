package com.example.lp_logistics.presentation.components

import android.annotation.SuppressLint


@SuppressLint("DefaultLocale")
fun VolumeGeneratorInMeters(
    length: Float,
    width: Float,
    height: Float
): Float {
    val volumeInCm = length * width * height
    val volumeInMeters = volumeInCm / 1_000_000
    return String.format("%.2f", volumeInMeters).toFloat()
}

@SuppressLint("DefaultLocale")
fun adding2Floats(a: Float, b: Float): Float {
    return String.format("%.2f", a + b).toFloat()
}

@SuppressLint("DefaultLocale")
fun subtracting2Floats(a: Float, b: Float): Float {
    return String.format("%.2f", a - b).toFloat()
}