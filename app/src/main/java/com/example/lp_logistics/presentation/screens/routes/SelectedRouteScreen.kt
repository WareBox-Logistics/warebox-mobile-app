package com.example.lp_logistics.presentation.screens.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.presentation.components.DateTimeUtils
import com.example.lp_logistics.presentation.components.OrangeTextFields
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.TripsCard
import com.example.lp_logistics.presentation.screens.home.HomeViewModel
import com.example.lp_logistics.presentation.theme.Create
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedRouteScreen(navController: NavController, deliveryID: Int, viewModel: HomeViewModel = hiltViewModel()) {

    val context = navController.context
    val delivery by viewModel.delivery
    val loading by viewModel.loading.collectAsState()
    val truckParkingLot by viewModel.parkingLotResponse.observeAsState()
    val trailerParkingLot by viewModel.parkingLotResponseTrailer.observeAsState()

    val spotCodeStateTruck = remember { mutableStateOf("") }
    val spotCodeStateTrailer = remember { mutableStateOf("") }
    val trailerPlates = remember { mutableStateOf("") }
    val truckPlates = remember { mutableStateOf("") }

    val routeJson = remember(delivery) {
        delivery?.route?.let { route ->
            Json.encodeToString(route)
        } ?: ""
    }




    val formattedTime = remember(delivery) {
        delivery?.shipping_date?.let { DateTimeUtils.formatToTime(it) } ?: "Loading..."
    }

    val formattedDate = remember(delivery) {
        delivery?.shipping_date?.let { DateTimeUtils.formatToDate(it) } ?: "Loading..."
    }


    LaunchedEffect(Unit){
        println("Loading delivery...")
        viewModel.getDelivery(deliveryID, context)
    }

    LaunchedEffect(delivery) {
        if (delivery != null) {
            viewModel.findTruckParkingLocation(context, delivery!!.truck.id)
            viewModel.findTrailerParkingLocation(context, delivery!!.trailer.id)
            trailerPlates.value = delivery!!.trailer.plates
            truckPlates.value = delivery!!.truck.plates
        }
    }

    LaunchedEffect(truckParkingLot) {
        if(truckParkingLot != null){
            spotCodeStateTruck.value = truckParkingLot!!.parking_location.spot_code

        }
    }

    LaunchedEffect(trailerParkingLot) {
        if(trailerParkingLot != null){
            spotCodeStateTrailer.value = trailerParkingLot!!.parking_location.spot_code
        }
    }

    Scaffold (
        topBar = {
            TopBar(title = "Selected route", color = false)
        },
        content = { innerPadding ->
            // Screen content
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = LightOrange)) {
                when {
                    loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    delivery != null -> {
                        Box(modifier = Modifier.padding(innerPadding)) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(680.dp)
                                        .padding(15.dp)
                                        .background(
                                            LightCreme,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                ) {

                                    LazyColumn(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(
                                            bottom = 16.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        ),
                                    )
                                    {
                                        item {
                                            TripsCard(
                                                origin = delivery!!.origin.name,
                                                destination = delivery!!.destination.name,
                                                date = formattedDate,
                                                time = formattedTime,
                                                image = "1",
                                            )
                                        }

                                        item {
                                            HorizontalDivider(
                                                modifier = Modifier
                                                    .width(600.dp)
                                                    .padding(top = 20.dp),
                                                thickness = 4.dp,
                                                color = LightGray
                                            )

                                            Text(
                                                text = truckParkingLot?.parking_location?.parking_lot_name
                                                    ?: "Loading parking info...",
                                                color = Orange,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    top = 20.dp,
                                                    bottom = 5.dp
                                                )

                                            )

                                        }
                                        // line thingy
                                        item {

                                            Text(
                                                text = "Trailer",
                                                color = LightBlue,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    top = 20.dp,
                                                    bottom = 20.dp
                                                )

                                            )

                                            OrangeTextFields(
                                                spotCodeStateTrailer,
                                                "ParkingLot Code",
                                                KeyboardOptions(imeAction = ImeAction.Next),
                                                readOnly = true
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            OrangeTextFields(
                                                trailerPlates,
                                                "Plates",
                                                KeyboardOptions(imeAction = ImeAction.Next),
                                                readOnly = true
                                            )
                                        }

                                        item {

                                            Text(
                                                text = "Truck",
                                                color = LightBlue,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    top = 20.dp,
                                                    bottom = 20.dp
                                                )

                                            )

                                            OrangeTextFields(
                                                spotCodeStateTruck,
                                                "ParkingLot Code",
                                                KeyboardOptions(imeAction = ImeAction.Next),
                                                readOnly = true
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))


                                            OrangeTextFields(
                                                truckPlates,
                                                "Plates",
                                                KeyboardOptions(imeAction = ImeAction.Next),
                                                readOnly = true
                                            )
                                        }

                                        item {
                                            Text(
                                                text = "Docking Area",
                                                color = LightBlue,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    top = 20.dp,
                                                    bottom = 20.dp
                                                )

                                            )
                                        }

                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .height(55.dp)
                                        .background(Create, RoundedCornerShape(10.dp))
                                        .fillMaxWidth()
                                        .clickable {
                                            val routeJson =
                                                Json.encodeToString(delivery!!.route)
                                            navController.navigate(
                                                "navigation/${
                                                    URLEncoder.encode(
                                                        routeJson,
                                                        "UTF-8"
                                                    )
                                                }"
                                            )
                                        }) {

                                    Text(
                                        text = "Load Route",
                                        color = Color(0xFF009045),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


