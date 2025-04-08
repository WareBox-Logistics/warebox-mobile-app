package com.example.lp_logistics.domain.model

import com.google.gson.annotations.SerializedName


data class DeliveryDetails(
    val id: Int,
    val origin: String,
    val destination: String,
    val status: String,
    val pallets: List<Pallets>
)

data class Pallets(
    val pallet_id:Int,
    val boxes: List<Boxes>
)

data class Boxes(
    val box_id: Int,
    val quantity: Int,
    val product_name: String,
    val product_sku: String
)

data class PalletUIState(
    val pallet: Pallets,
    val isExpanded: Boolean = false,
    val boxes: List<BoxUIState>
)

data class BoxUIState(
    val box: Boxes,
    val isExpanded: Boolean = false
)

data class SetLoadingRequest(
    @SerializedName("delivery_id")
    val deliveryId: Int
)