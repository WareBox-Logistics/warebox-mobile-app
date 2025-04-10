package com.example.lp_logistics.presentation.components.QR.Camera

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.tasks.await
import android.content.Context


class QRCodeScanner(context: Context) {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

    private val scanner = GmsBarcodeScanning.getClient(context, options)

    suspend fun startScan(): String? {
        return try {
            scanner.startScan().await().rawValue?.toString()
        } catch (e: Exception) {
            null
        }
    }
}