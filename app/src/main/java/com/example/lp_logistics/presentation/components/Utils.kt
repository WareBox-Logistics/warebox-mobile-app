package com.example.lp_logistics.presentation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale


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


object DateTimeUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    private val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToTime(isoString: String): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(isoString, isoFormatter)
            val localTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
            localTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        } catch (e: Exception) {
            isoString // Fallback to original string
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToDate(isoString: String): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(isoString, isoFormatter)
            val localDate = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
            localDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault()))
        } catch (e: Exception) {
            isoString
        }
    }
}