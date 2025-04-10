package com.example.lp_logistics.presentation.components.QR.Camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun ScannerNavigator(
    navController: NavHostController,
    isPallet: Boolean = false,
    delivery: Boolean = false,
    driver: Boolean = false
) {
    val context = LocalContext.current
    val qrCodeScanner = remember { QRCodeScanner(context) }
    var scannedData by remember { mutableStateOf<String?>(null) }
    var scanAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val result = qrCodeScanner.startScan()
            scannedData = result
        } catch (e: Exception) {
            // User pressed back or scan failed
            scannedData = null
        } finally {
            scanAttempted = true
        }
    }

    LaunchedEffect(scannedData, scanAttempted) {
        // Only navigate if scan was attempted and we have data
        if (scanAttempted && scannedData != null) {
            when {
                isPallet -> navController.navigate("create-pallet?palletIDNav=${scannedData!!.toInt()}")
                delivery -> navController.navigate("delivery-info?deliveryID=${scannedData!!.toInt()}")
                driver -> navController.navigate("confirming-delivery?code=${scannedData!!}")
                else -> navController.navigate("box-info?boxID=${scannedData!!.toInt()}")
            }
        } else if (scanAttempted) {
            // If scan was attempted but no data (user pressed back), navigate back
            navController.popBackStack()
        }
    }
}