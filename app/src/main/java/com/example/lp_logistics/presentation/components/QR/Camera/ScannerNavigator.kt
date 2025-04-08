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
fun ScannerNavigator(navController: NavHostController, isPallet: Boolean = false, delivery: Boolean = false, driver: Boolean = false) {
    val context = LocalContext.current
    val qrCodeScanner = remember { QRCodeScanner(context) }
    var scannedData by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        try{
        scannedData = qrCodeScanner.startScan().toString()
        }catch (e: Exception){
            println(e)
        }
    }

    LaunchedEffect (scannedData){
        if (scannedData.isNotEmpty()) {
            println(scannedData)
            println(isPallet)
            if (isPallet) {
                println("Navigate to Pallet Screen")
                navController.navigate("create-pallet?palletIDNav=${scannedData.toInt()}")
            } else if (delivery) {
                navController.navigate("delivery-info?deliveryID=${scannedData.toInt()}")
                } else if (driver) {
                navController.navigate("confirming-delivery?code=${scannedData}")
            } else {
                println("Navigate to Box Info Screen")
                navController.navigate("box-info?boxID=${scannedData.toInt()}")
            }
        }
    }
}