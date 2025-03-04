package com.example.lp_logistics.data.remote.requests

data class CreateBoxRequest(
    val qty: Int,
    val weight: Float,
    val volume: Float,
    val pallet: Int,
    val inventory: Int
)

data class CreatePalletRequest(
    val company: Int,
    val warehouse: Int,
    val weight: Float,
    val volume: Float,
    val status: String,
    val verified: Boolean
)

