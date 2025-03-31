package com.example.lp_logistics.data.remote.requests

import com.google.gson.annotations.SerializedName

data class CreateBoxRequest(
    val qty: Int,
    val weight: Float,
    val volume: Float,
    val pallet: Int,
    val product: Int
)

data class CreatePalletRequest(
    val company: Int,
    val warehouse: Int,
    val weight: Float,
    val volume: Float,
    val status: String,
    val verified: Boolean
)

data class DriverIdRequest(
    @SerializedName("driverID")
    val driverID: Int
)

data class ParkingLotRequest(
    @SerializedName("vehicleID")
    val vehicleID: Int
)

//dispatch
data class ReportDispatchRequest(
    val latitude: String,
    val longitude: String,
    val problem: Int, // this is the problem ID
    val issue: Boolean, // this is to say if its an issue or a boolean
    val description: String,
    val driver: Int
)

