package com.example.lp_logistics.domain.model

data class Vehicle(
    val id: Int,
    val plates: String,
    val vin: String,
    val modell: Model
)

data class Model(
    val id: Int,
    val name: String,
    val brand: Brand
)

data class Brand(
    val id: Int,
    val name: String
)
