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
import com.example.lp_logistics.presentation.components.NumericOrangeTextFieldContainers
import com.example.lp_logistics.presentation.components.OrangeTextFields
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.TripsCard
import com.example.lp_logistics.presentation.screens.home.HomeViewModel
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
    val loadingStatus by viewModel.loadingStatus.collectAsState()

    val spotCodeStateTruck = remember { mutableStateOf("") }
    val spotCodeStateTrailer = remember { mutableStateOf("") }
    val trailerPlates = remember { mutableStateOf("") }
    val vehicleInMovement = remember { mutableStateOf("Vehicle currently in movement") }

    val truckPlates = remember { mutableStateOf("") }
    val truckID = remember { mutableStateOf(0) }

    val currentStatus by viewModel.deliveryStatus


    val routeJson = remember(delivery) {
        delivery?.route?.let { route ->
            Json.encodeToString(route)
        } ?: ""
    }

    val buttonState = remember(currentStatus) {
        viewModel.getButtonState(currentStatus)
    }

    val onClickAction: () -> Unit = remember(currentStatus) {
        {
            when(currentStatus) {
                "Pending" -> {
                    viewModel.setDockingStatus(context, deliveryID)
                }
                "Docking" -> {
                    viewModel.setLoadingDock(
                        context,
                        deliveryID
                    )

                }
                "Loading" -> {
                    viewModel.startDelivering(context, deliveryID)
                    navController.navigate(
                        "navigation/$deliveryID/${delivery!!.type}/${
                            URLEncoder.encode(routeJson, "UTF-8")
                        }"
                    )
                }
                "Delivering" -> {
                    println("From selectedROute: ${delivery!!.type}")
                    //now viewmodel logic here cuz he just needs to access the map because he has already dock and everything
                    navController.navigate(
                        "navigation/$deliveryID/${delivery!!.type}/${
                            URLEncoder.encode(routeJson, "UTF-8")
                        }"
                    )                }
            }
        }
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
            truckID.value = delivery!!.truck.id
            truckID.value = delivery!!.truck.id
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
        }
    ) { innerPadding ->
        // Screen content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = LightOrange)
        ) {
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
                                            if(spotCodeStateTrailer.value.isEmpty()) vehicleInMovement else spotCodeStateTrailer,
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
                                            if(spotCodeStateTruck.value.isEmpty()) vehicleInMovement else spotCodeStateTruck,
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

                                        NumericOrangeTextFieldContainers(
                                            delivery?.dock?.number.toString(),
                                            "Dock Number",
                                        )

                                    }

                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .height(55.dp)
                                    .background(buttonState.color, RoundedCornerShape(10.dp))
                                    .fillMaxWidth()
                                    .clickable(
                                        enabled = buttonState.enabled,
                                        onClick = onClickAction
                                    )
                            ) {
                                if (loadingStatus) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = buttonState.text,
                                        color = buttonState.textColor,
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
    }
}


