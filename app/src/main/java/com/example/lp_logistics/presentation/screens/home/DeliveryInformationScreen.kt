package com.example.lp_logistics.presentation.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lp_logistics.data.remote.responses.RouteFromDelivery
import com.example.lp_logistics.data.remote.responses.Trailer
import com.example.lp_logistics.data.remote.responses.Truck

@Composable
fun DeliveryInformation(modifier: Modifier = Modifier, route: RouteFromDelivery, truck: Truck, trailer: Trailer) {
    LazyColumn(){

    }
}

//@Preview
//@Composable
//private fun PreviewDeliveryInformation() {
//    DeliveryInformation()
//}