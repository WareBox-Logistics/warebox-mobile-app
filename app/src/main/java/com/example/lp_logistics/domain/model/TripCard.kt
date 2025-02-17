package com.example.lp_logistics.domain.model

import android.content.Context
import com.example.lp_logistics.R

data class TravelItem(
    val origin: String,
    val destination: String,
    val date: String,
    val time: String,
    val image: String,
    val context: Context
)

